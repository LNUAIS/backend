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
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}