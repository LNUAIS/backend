package com.backend.lnuais_backend.services;

import com.backend.lnuais_backend.model.User;
import com.backend.lnuais_backend.model.User.Experience;
import com.backend.lnuais_backend.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 1. Let the default Spring class talk to Google first to get the data
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("DEBUG: Google User found: " + oAuth2User.getAttribute("email"));

        // 2. Extract the user's info from the Google result
        String googleEmail = oAuth2User.getAttribute("email");
        String googleName = oAuth2User.getAttribute("name");

        // 3. Check if this user is already in our database
        User existingUser = userRepository.findByEmail(googleEmail);

        if (existingUser == null) {
            // 4. If not, REGISTER them automatically
            User newUser = new User();
            newUser.setEmail(googleEmail);
            newUser.setName(googleName);
            newUser.setPassword(""); // No password for Google users
            newUser.setProgram("N/A"); // Placeholders until they update their profile
            newUser.setLevel(Experience.LOW); 
            
            userRepository.save(newUser);
        }
        
        // 5. Return the user so Spring Security can finish the login
        return oAuth2User;
    }
}