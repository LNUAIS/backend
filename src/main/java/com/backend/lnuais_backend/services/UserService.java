package com.backend.lnuais_backend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.backend.lnuais_backend.model.User;
import com.backend.lnuais_backend.model.User.Experience;
import com.backend.lnuais_backend.repository.UserRepository;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // Add EmailService

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // 1. Create User (Updated with Verification Code)
    public void addUser(String name, String password, String email, String program, Experience experience) {
        User existingUser = userRepository.findByEmail(email);

        // A. If user exists AND is already verified, block them.
        if (existingUser != null && existingUser.isEnabled()) {
            throw new IllegalStateException("Email is already registered and verified. Please login.");
        }

        // B. Setup the user object
        User user;
        if (existingUser != null) {
            // Recycle the existing unverified row (Fixes the "Email Taken" bug)
            user = existingUser;
        } else {
            // Create a brand new row
            user = new User();
            user.setEmail(email);
        }

        // C. Update/Set fields (Overwrites old data if retrying)
        user.setName(name);
        user.setProgram(program);
        user.setLevel(experience);
        user.setPassword(passwordEncoder.encode(password)); // Re-hash password
        user.setEnabled(false); // Ensure it's locked until verified

        // D. Generate NEW Code
        String randomCode = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setVerificationCode(randomCode);

        // E. Save and Send
        userRepository.save(user);
        emailService.sendVerificationEmail(email, randomCode);
    }

    // 2. Verify User
    public boolean verifyUser(String email, String code) {
        User user = userRepository.findByEmail(email);

        if (user == null)
            return false;

        // Check if code matches (and make sure user isn't already verified)
        if (user.getVerificationCode() != null && user.getVerificationCode().equals(code)) {
            user.setEnabled(true);
            user.setVerificationCode(null); // Clear code
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUserProfile(Long userId, String program, Experience level) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (program != null && !program.isEmpty()) {
            user.setProgram(program);
        }
        if (level != null) {
            user.setLevel(level);
        }

        return userRepository.save(user);
    }

    // 3. Login Logic (Updated to check if Enabled)
    public User loginUser(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalStateException("User not found");
        }

        // 1. CHECK IF UNVERIFIED
        if (!user.isEnabled()) {
            // A. Generate a new code to be safe
            String newCode = String.valueOf(new Random().nextInt(900000) + 100000);
            user.setVerificationCode(newCode);
            userRepository.save(user);

            // B. Resend the Email
            emailService.sendVerificationEmail(email, newCode);

            // C. Throw specific error for Frontend to catch
            throw new IllegalStateException("UNVERIFIED_ACCOUNT");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalStateException("Invalid password");
        }

        return user;
    }

    // 4. Reset Password By Email (New Request)
    public void resetPasswordByEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalStateException("User not found");
        }

        // Prevent Google users from resetting password
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalStateException("Google accounts cannot reset passwords here.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 5. Get User Details
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    // 6. Change Password (LoggedIn)
    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 7. Delete User
    public void deleteUser(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Case A: Google User (No password set in DB)
        // We allow deletion immediately because they are authenticated via OAuth
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            userRepository.deleteById(userId);
            return;
        }

        // Case B: Normal User (Must verify password)
        // If password is null/empty coming from frontend, block it
        if (password == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalStateException("Incorrect password. Please try again.");
        }

        userRepository.deleteById(userId);
    }

    // --- Password Reset Logic ---

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        // Generate 6-digit code
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);

        user.setResetToken(code);
        user.setResetTokenExpiry(java.time.LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(email, code);
    }

    public boolean verifyResetCode(String email, String code) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            return false;

        if (user.getResetToken() == null || user.getResetTokenExpiry() == null) {
            return false;
        }
        boolean matches = user.getResetToken().equals(code);
        boolean notExpired = user.getResetTokenExpiry().isAfter(java.time.LocalDateTime.now());
        return matches && notExpired;
    }

    public void resetPassword(String email, String code, String newPassword) {
        if (!verifyResetCode(email, code)) {
            throw new RuntimeException("Invalid or expired reset code.");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}