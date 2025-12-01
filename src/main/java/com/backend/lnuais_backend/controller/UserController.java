package com.backend.lnuais_backend.controller;

import com.backend.lnuais_backend.model.User;
import com.backend.lnuais_backend.model.User.Experience;
import com.backend.lnuais_backend.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST: Create Account
    @PostMapping("/new_member")
    public void addUser(@RequestBody User request) {
        userService.addUser(
                request.getName(),
                request.getPassword(),
                request.getEmail(),
                request.getProgram(),
                request.getLevel());
    }

    // POST: Login (New)
    // URL: http://localhost:8080/users/login
    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        // 1. Verify credentials via service
        User user = userService.loginUser(email, password);

        // 2. Manually establish Spring Security Session
        // Since we are doing manual password check, we trust the user is valid here.
        // We create a token with authorities (if any) and set it in the context.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.emptyList() // Add authorities if you have roles
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);

        // 3. Save context to session
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);

        return user;
    }

    // NEW: Verify Endpoint
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String email = payload.get("email");
        String code = payload.get("code");

        System.out.println("Attempting verification for: " + email + " with code: " + code);

        boolean isVerified = userService.verifyUser(email, code);

        if (isVerified) {
            System.out.println("Verification successful for: " + email);
            // 1. Get the user
            User user = userService.findUserByEmail(email);

            // 2. Establish Session (Auto-Login)
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    Collections.emptyList());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            // 3. Return User
            return ResponseEntity.ok(user);
        } else {
            System.out.println("Verification failed for: " + email);
            User existing = userService.findUserByEmail(email);
            if (existing != null) {
                System.out.println(
                        "User exists. Enabled: " + existing.isEnabled() + ", Code: " + existing.getVerificationCode());
                if (existing.isEnabled()) {
                    return ResponseEntity.badRequest().body("Account already verified. Please login.");
                }
            }
            return ResponseEntity.badRequest().body("Invalid verification code.");
        }
    }

    @PutMapping("/{id}/update_profile")
    public User updateProfile(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String program = payload.get("program");
        // Convert String "HIGH" to Enum Experience.HIGH
        Experience level = payload.get("level") != null ? Experience.valueOf(payload.get("level")) : null;

        return userService.updateUserProfile(id, program, level);
    }

    // GET: Get User Info (New)
    // URL: http://localhost:8080/users/{id}
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // PUT: Change Password
    @PutMapping("/{id}/change_password")
    public void changePassword(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newPassword = payload.get("password");
        userService.changePassword(id, newPassword);
    }

    // DELETE: Delete Account (Refactored to POST for body support)
    @PostMapping("/{id}/delete")
    public void deleteUser(@PathVariable Long id, @RequestBody(required = false) Map<String, String> payload,
            HttpServletRequest request) throws ServletException {
        // Extract password safely (it might be null if coming from a Google user)
        String password = (payload != null) ? payload.get("password") : "";

        userService.deleteUser(id, password);
        request.logout(); // Invalidate session
        SecurityContextHolder.clearContext(); // Double ensure context is cleared
    }

    @GetMapping("/profile")
    public Map<String, Object> userProfile(@AuthenticationPrincipal Object principal) {
        if (principal == null)
            return Collections.emptyMap();

        String email = null;
        Map<String, Object> attributes = new HashMap<>();

        if (principal instanceof OAuth2User) {
            OAuth2User oauthUser = (OAuth2User) principal;
            email = oauthUser.getAttribute("email");
            attributes.putAll(oauthUser.getAttributes());
        } else if (principal instanceof User) {
            // For form login, principal is our User object
            User user = (User) principal;
            email = user.getEmail();
            attributes.put("name", user.getName());
            attributes.put("email", user.getEmail());
        } else {
            // Fallback
            return Collections.emptyMap();
        }

        // 2. Find the ACTUAL database user to get their ID
        User dbUser = userService.findUserByEmail(email);

        // 3. CRITICAL FIX: If user is not in DB, they are deleted. Kill the session.
        if (dbUser == null) {
            SecurityContextHolder.clearContext();
            throw new IllegalStateException("User account no longer exists.");
        }

        // 4. Merge Google Info with Database Info
        attributes.put("id", dbUser.getId());
        attributes.put("program", dbUser.getProgram());
        attributes.put("level", dbUser.getLevel());

        return attributes;
    }

    // --- Password Reset Endpoints ---

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        try {
            userService.forgotPassword(email);
            return ResponseEntity.ok(Map.of("message", "Reset code sent to email."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        boolean valid = userService.verifyResetCode(email, code);
        if (valid) {
            return ResponseEntity.ok(Map.of("message", "Code verified."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired code."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        String newPassword = payload.get("newPassword");
        try {
            userService.resetPassword(email, code, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}