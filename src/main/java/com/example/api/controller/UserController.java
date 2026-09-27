package com.example.api.controller;

import com.example.api.entity.User;
import com.example.api.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller exposing API endpoints for user registration, authentication, and session management.
 *
 * <p>What's happening here:
 * This controller handles incoming HTTP requests under the {@code /api/users} path prefix.
 * It manages user onboarding, login, session inspection, and logout by coordinating with {@link UserService},
 * maintaining session-based state tracking via {@link HttpSession}.
 *
 * <p>What is done:
 * <ul>
 *   <li>{@code POST /api/users/register} (201 Created): Accepts user profile data in JSON format and creates a new account.</li>
 *   <li>{@code POST /api/users/login} (200 OK): Verifies user credentials, sets up session attributes, and returns user details.</li>
 *   <li>{@code GET|POST /api/users/logout} (200 OK): Terminates active session and unbinds stored user credentials.</li>
 *   <li>{@code GET /api/users/me} (200 OK): Fetches current logged-in user profile details from the HTTP session.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * Service layer dependency handling user registration and login workflows.
     * Injected via constructor for immutability and testability.
     */
    private final UserService userService;

    /**
     * Constructs a UserController with the required service dependency.
     *
     * @param userService The {@link UserService} implementation to delegate business logic to.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user in the system.
     *
     * <p>Endpoint: {@code POST /api/users/register}
     * <p>Response: HTTP 201 Created
     * <p>What's happening:
     * Receives JSON payload with user details, deserializes into a {@link User} object,
     * calls {@link UserService#register(User)} to store the user, and returns a success message.
     *
     * @param user The {@link User} entity deserialized from the HTTP request body.
     * @return A {@link ResponseEntity} with HTTP 201 status and a confirmation message.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user) {
        // Delegate user registration to the service layer
        userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Register Successfully");
    }

    /**
     * Authenticates a user with email and password, establishing a session.
     *
     * <p>Endpoint: {@code POST /api/users/login}
     * <p>Response: HTTP 200 OK
     * <p>What's happening:
     * Takes email and password from the request body, invokes {@link UserService#login(String, String, HttpSession)},
     * binds authenticated user details to the {@link HttpSession}, and returns the user entity as JSON.
     *
     * @param user    The {@link User} object containing {@code email} and {@code password}.
     * @param session The active {@link HttpSession} injected by Spring MVC.
     * @return A {@link ResponseEntity} with HTTP 200 status and the authenticated {@link User} details.
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user, HttpSession session) {
        // Delegate authentication and session initialization to the service layer
        User authenticatedUser = userService.login(user.getEmail(), user.getPassword(), session);
        return ResponseEntity.ok(authenticatedUser);
    }

    /**
     * Terminates the active session and signs out the currently authenticated user.
     *
     * <p>Endpoint: {@code GET|POST /api/users/logout}
     * <p>Response: HTTP 200 OK
     * <p>What's happening:
     * Delegates to {@link UserService#logout(HttpSession)} to clear session attributes and invalidate
     * the HTTP session, returning a confirmation message.
     *
     * @param httpSession The active {@link HttpSession} injected by Spring MVC.
     * @return A {@link ResponseEntity} with HTTP 200 status and a confirmation message.
     */
    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> logout(HttpSession httpSession) {
        // Delegate session termination to the service layer
        userService.logout(httpSession);
        return ResponseEntity.ok("LoggedOut!");
    }

    /**
     * Retrieves profile information of the currently logged-in user from the active session.
     *
     * <p>Endpoint: {@code GET /api/users/me}
     * <p>Response: HTTP 200 OK
     * <p>What's happening:
     * Reads the {@code userName} and {@code userRole} attributes from the {@link HttpSession}.
     * Returns an informational prompt if no active user session exists, or formatted user profile details if logged in.
     *
     * @param httpSession The active {@link HttpSession} injected by Spring MVC.
     * @return A {@link ResponseEntity} with HTTP 200 status and formatted user details or a login prompt.
     */
    @GetMapping("/me")
    public ResponseEntity<String> fetchProfile(HttpSession httpSession) {
        // Verify that an active session exists
        if (httpSession == null) {
            return ResponseEntity.ok("Login to view details!");
        }

        // Retrieve user attributes stored during authentication
        String authenticatedUserName = (String) httpSession.getAttribute("userName");
        String authenticatedUserRole = (String) httpSession.getAttribute("userRole");

        // Return prompt if user is not authenticated
        if (authenticatedUserRole == null) {
            return ResponseEntity.ok("Login to view details!");
        }

        // Format and return user identity summary
        return ResponseEntity.ok("Name is: " + authenticatedUserName + "\n" + "Role is: " + authenticatedUserRole);
    }
}
