package com.backend.lnuais_backend.controller;

import com.backend.lnuais_backend.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.lnuais_backend.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/new_member")
    public void addUser(@RequestBody User request){
        userService.addUser(
            request.getName(),
            request.getPassword(), 
            request.getEmail(), 
            request.getProgram(), 
            request.getLevel());
    }
}
