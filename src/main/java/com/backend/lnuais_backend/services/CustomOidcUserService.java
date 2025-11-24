package com.backend.lnuais_backend.services;

import com.backend.lnuais_backend.model.User;
import com.backend.lnuais_backend.model.User.Experience;
import com.backend.lnuais_backend.repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Let Spring get the OIDC user details from Google
        OidcUser oidcUser = super.loadUser(userRequest);
        
        System.out.println(" DEBUG: OIDC (Google) User found: " + oidcUser.getEmail()); 

        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName(); // Google provides "Full Name"

        // 2. Check and Save (Same logic as before)
        User existingUser = userRepository.findByEmail(email);

        if (existingUser == null) {
            System.out.println("DEBUG: Creating new Google user...");
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword("");
            newUser.setProgram("N/A");
            newUser.setLevel(null);
            
            userRepository.save(newUser);
        } else {
            System.out.println("DEBUG: User already exists.");
        }

        return oidcUser;
    }
}