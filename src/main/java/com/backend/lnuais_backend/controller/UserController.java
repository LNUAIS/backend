package com.backend.lnuais_backend.controller;

import com.backend.lnuais_backend.model.User;
import com.backend.lnuais_backend.model.User.Experience;
import com.backend.lnuais_backend.services.UserService;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    // POST: Create Account
    @PostMapping("/new_member")
    public void addUser(@RequestBody User request){
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
    public User login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        return userService.loginUser(email, password);
    }

    // NEW: Verify Endpoint
    @PostMapping("/verify")
    public String verifyAccount(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");

        boolean isVerified = userService.verifyUser(email, code);
        
        if (isVerified) {
            return "Account verified successfully!";
        } else {
            throw new IllegalStateException("Invalid verification code.");
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

    // DELETE: Delete Account
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id, @RequestBody(required = false) Map<String, String> payload) {
        // Extract password safely (it might be null if coming from a Google user)
        String password = (payload != null) ? payload.get("password") : "";
        
        userService.deleteUser(id, password);
    }

    @GetMapping("/profile")
public Map<String, Object> userProfile(@AuthenticationPrincipal OAuth2User principal) {
    if (principal == null) return Collections.emptyMap();
    
    // 1. Get Email from Google Info
    String email = principal.getAttribute("email");
    
    // 2. Find the ACTUAL database user to get their ID
    User dbUser = userService.findUserByEmail(email); // You might need to add this method to UserService if not public
    
    // 3. Merge Google Info with Database Info
    Map<String, Object> response = new HashMap<>(principal.getAttributes());
    if (dbUser != null) {
        response.put("id", dbUser.getId());       // <--- CRITICAL FOR UPDATES
        response.put("program", dbUser.getProgram());
        response.put("level", dbUser.getLevel());
    }
    return response;
}
}