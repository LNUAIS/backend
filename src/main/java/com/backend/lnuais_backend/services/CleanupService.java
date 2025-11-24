package com.backend.lnuais_backend.services;

import com.backend.lnuais_backend.repository.UserRepository;
import com.backend.lnuais_backend.model.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CleanupService {

    private final UserRepository userRepository;

    public CleanupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Run every hour
    @Scheduled(fixedRate = 3600000) 
    public void removeUnverifiedUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.isEnabled()) {
                // In a real app, check creation time > 24 hours
                // For now, we just leave them or delete them if you prefer
                // userRepository.delete(user); 
                System.out.println("Found unverified user: " + user.getEmail());
            }
        }
    }
}