package com.example.api.controller;


import com.example.api.entity.User;
import com.example.api.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String Register(@RequestBody User user) {
        userService.signUp(user);
        return "Register Successfully";
    }

    @PostMapping("/login")
    public User login(@RequestBody User user, HttpSession session) {
        return userService.signIn(user.getEmail(), user.getPassword(), session);
    }

}
