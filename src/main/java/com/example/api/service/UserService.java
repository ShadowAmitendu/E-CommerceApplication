package com.example.api.service;

import com.example.api.entity.User;
import jakarta.servlet.http.HttpSession;

public interface UserService {

    public void signUp(User user);

    public User signIn(String email, String password, HttpSession session);

    public void signOut(HttpSession session);


}
