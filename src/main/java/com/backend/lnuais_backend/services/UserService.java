package com.backend.lnuais_backend.services;
import org.springframework.stereotype.Service;
import com.backend.lnuais_backend.model.User;
import com.backend.lnuais_backend.model.User.Experience;
import com.backend.lnuais_backend.repository.UserRepository;

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
}
