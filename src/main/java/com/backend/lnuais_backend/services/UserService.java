package com.backend.lnuais_backend.services;
import org.springframework.stereotype.Service;

import com.backend.lnuais_backend.model.User;
import com.backend.lnuais_backend.model.User.Experience;
import com.backend.lnuais_backend.repository.UserRepository;
import java.util.Optional;

@Service
public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void addUser(String name, String password, String email, String program, Experience experience){
        User user = new User(name, password, email, program, experience);
        userRepository.save(user);
    }


    /**
     * Updates the password for a specific user.
     * @param userId The ID of the user to update.
     * @param newPassword The new password to set.
     */
    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User with id " + userId + " does not exist"));

    
        user.setPassword(newPassword);


        userRepository.save(user);
    }


    /**
     * Deletes a user account.
     * @param userId The ID of the user to delete.
     */
    public void deleteUser(Long userId) {
        boolean exists = userRepository.existsById(userId);
        
        if (!exists) {
            throw new IllegalStateException("User with id " + userId + " does not exist");
        }
        
        userRepository.deleteById(userId);
    }
}
