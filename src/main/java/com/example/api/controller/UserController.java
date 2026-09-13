package com.example.api.controller;

import com.example.api.entity.User;
import com.example.api.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller exposing API endpoints for user registration and authentication.
 *
 * <p>What's happening here:
 * This controller handles incoming HTTP requests under the {@code /api/users} path prefix.
 * It manages user onboarding and login by coordinating with {@link UserService}, while enabling
 * session-based state tracking via {@link HttpSession}.
 *
 * <p>What is done:
 * <ul>
 *   <li>{@code POST /api/users/register}: Accepts user profile data in JSON format and creates a new account.</li>
 *   <li>{@code POST /api/users/login}: Verifies user credentials, sets up session attributes, and returns user details.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * Injected service layer dependency handling user registration and login workflows.
     */
    @Autowired
    private UserService userService;

    /**
     * Registers a new user in the system.
     *
     * <p>Endpoint: {@code POST /api/users/register}
     * <p>What's happening:
     * Receives JSON payload with user details, deserializes into a {@link User} object,
     * calls {@link UserService#register(User)} to store the user, and returns a success message.
     *
     * @param user The {@link User} entity deserialized from the HTTP request body.
     * @return A confirmation string indicating successful registration.
     */
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // Delegate user registration to the service layer
        userService.register(user);
        return "Register Successfully";
    }

    /**
     * Authenticates a user with email and password, establishing a session.
     *
     * <p>Endpoint: {@code POST /api/users/login}
     * <p>What's happening:
     * Takes email and password from the request body, invokes {@link UserService#login(String, String, HttpSession)},
     * binds authenticated user details to the {@link HttpSession}, and returns the user entity as JSON.
     *
     * @param user    The {@link User} object containing {@code email} and {@code password}.
     * @param session The active {@link HttpSession} injected by Spring MVC.
     * @return The authenticated {@link User} details.
     */
    @PostMapping("/login")
    public User login(@RequestBody User user, HttpSession session) {
        // Delegate authentication and session initialization to the service layer
        return userService.login(user.getEmail(), user.getPassword(), session);
    }

}
