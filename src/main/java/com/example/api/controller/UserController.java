package com.example.api.controller;

import com.example.api.entity.User;
import com.example.api.exception.AuthenticationRequiredException;
import com.example.api.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> fetchProfile(HttpSession httpSession) {
        // Verify that an active session exists
        if (httpSession == null) {
            throw new AuthenticationRequiredException("Login required to view profile");
        }

        // Retrieve user attributes stored during authentication
        Object authenticatedUserId = httpSession.getAttribute("userId");
        String authenticatedUserEmail = (String) httpSession.getAttribute("userEmail");
        String authenticatedUserName = (String) httpSession.getAttribute("userName");
        String authenticatedUserRole = (String) httpSession.getAttribute("userRole");

        // Enforce user authentication
        if (authenticatedUserRole == null || authenticatedUserEmail == null) {
            throw new AuthenticationRequiredException("Login required to view profile");
        }

        // Return structured user profile JSON
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", authenticatedUserId);
        profile.put("name", authenticatedUserName);
        profile.put("email", authenticatedUserEmail);
        profile.put("role", authenticatedUserRole);

        if (authenticatedUserId instanceof Number) {
            try {
                User user = userService.getUserById(((Number) authenticatedUserId).intValue(), httpSession);
                profile.put("name", user.getName());
                profile.put("phone", user.getPhone());
            } catch (Exception ignored) {
            }
        }

        return ResponseEntity.ok(profile);
    }

    /**
     * Updates profile details for the currently logged-in user.
     *
     * <p>Endpoint: {@code PUT /api/users/me} (alias: {@code /profile})
     * <p>Response: HTTP 200 OK
     *
     * @param request     The updated profile fields (name, phone, password).
     * @param httpSession The active {@link HttpSession}.
     * @return A {@link ResponseEntity} with HTTP 200 status and updated {@link User} details.
     */
    @PutMapping({"/me", "/profile"})
    public ResponseEntity<User> updateProfile(@RequestBody com.example.api.dto.UpdateProfileRequest request, HttpSession httpSession) {
        User updatedUser = userService.updateProfile(request, httpSession);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Retrieves all registered users in the system. Requires administrator privileges.
     *
     * <p>Endpoint: {@code GET /api/users} (aliases: {@code /all}, {@code /view})
     * <p>Response: HTTP 200 OK
     *
     * @param httpSession The active {@link HttpSession} injected by Spring MVC.
     * @return A {@link ResponseEntity} with HTTP 200 status and list of all users.
     */
    @GetMapping({"", "/all", "/view"})
    public ResponseEntity<java.util.List<User>> getAllUsers(HttpSession httpSession) {
        return ResponseEntity.ok(userService.getAllUsers(httpSession));
    }

    /**
     * Retrieves an individual user by primary key ID.
     * Accessible by administrators or the account owner.
     *
     * <p>Endpoint: {@code GET /api/users/{id}}
     * <p>Response: HTTP 200 OK
     *
     * @param id          The user primary key ID.
     * @param httpSession The active {@link HttpSession}.
     * @return A {@link ResponseEntity} with HTTP 200 status and the matched {@link User} entity.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Integer id, HttpSession httpSession) {
        return ResponseEntity.ok(userService.getUserById(id, httpSession));
    }
}
