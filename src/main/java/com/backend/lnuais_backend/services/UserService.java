package com.backend.lnuais_backend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.backend.lnuais_backend.model.User;
import com.backend.lnuais_backend.model.User.Experience;
import com.backend.lnuais_backend.repository.UserRepository;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; 
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Create User (Now with Hashing)
    public void addUser(String name, String password, String email, String program, Experience experience){
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalStateException("Email taken");
        }
        // Hash the password before saving
        String encodedPassword = passwordEncoder.encode(password);
        
        User user = new User(name, encodedPassword, email, program, experience);
        userRepository.save(user);
    }

    // 2. Login Logic (New)
    public User loginUser(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            throw new IllegalStateException("User not found");
        }

        // Compare the raw password (from login form) with the hashed password (in DB)
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalStateException("Invalid password");
        }

        return user; // Login successful
    }

    // 3. Get User Details (New)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    // 4. Change Password (Now with Hashing)
    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Hash the NEW password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 5. Delete User
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
             throw new IllegalStateException("User not found");
        }
        userRepository.deleteById(userId);
    }
}