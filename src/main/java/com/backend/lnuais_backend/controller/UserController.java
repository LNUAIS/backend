package com.backend.lnuais_backend.controller;

import com.backend.lnuais_backend.model.User;
import com.backend.lnuais_backend.services.UserService;
import org.springframework.web.bind.annotation.*; 

import java.util.Map; 

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    // 1. Create User
    // POST http://localhost:8080/users/new_member
    @PostMapping("/new_member")
    public void addUser(@RequestBody User request){
        userService.addUser(
            request.getName(),
            request.getPassword(), 
            request.getEmail(), 
            request.getProgram(), 
            request.getLevel());
    }

    // 2. Change Password
    // PUT http://localhost:8080/users/{id}/change_password
    @PutMapping("/{id}/change_password")
    public void changePassword(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        // This allows you to send a JSON body like: { "password": "myNewPassword123" }
        String newPassword = payload.get("password");
        userService.changePassword(id, newPassword);
    }

    // 3. Delete User
    // DELETE http://localhost:8080/users/{id}
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}