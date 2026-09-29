package com.example.api.service;

import com.example.api.entity.User;
import jakarta.servlet.http.HttpSession;

/**
 * Service interface defining user management and authentication contracts.
 *
 * <p>What's happening here:
 * This interface abstracts user registration, authentication, and session sign-out
 * operations for the application.
 *
 * <p>What is done:
 * <ul>
 *   <li>Defines user registration through {@link #register(User)}.</li>
 *   <li>Defines credential validation and session establishment through {@link #login(String, String, HttpSession)}.</li>
 *   <li>Defines session termination through {@link #logout(HttpSession)}.</li>
 * </ul>
 */
public interface UserService {

    /**
     * Registers a new user into the system.
     *
     * @param user The {@link User} entity to be persisted.
     */
    public void register(User user);

    /**
     * Authenticates a user with the provided credentials and initializes an active session.
     *
     * @param email    The email address provided by the user during login.
     * @param password The plaintext password provided during login.
     * @param session  The current HTTP session object to store logged-in user state.
     * @return The authenticated {@link User} entity.
     * @throws com.example.api.exception.ResourceNotFoundException   If the email is not registered.
     * @throws com.example.api.exception.InvalidCredentialsException If the password does not match.
     */
    public User login(String email, String password, HttpSession session);

    /**
     * Terminates the user's active session and clears session data.
     *
     * @param session The HTTP session to invalidate.
     */
    public void logout(HttpSession session);

    /**
     * Updates the profile of the currently logged-in user.
     *
     * @param request The profile update details.
     * @param session The active HTTP session.
     * @return The updated User entity.
     */
    public User updateProfile(com.example.api.dto.UpdateProfileRequest request, HttpSession session);

    /**
     * Retrieves all registered users in the system. Requires administrator privileges.
     *
     * @param session The active HTTP session to verify administrator authority.
     * @return List of all users.
     */
    public java.util.List<User> getAllUsers(HttpSession session);

    /**
     * Retrieves a single user by primary key ID. Requires administrator privileges or account ownership.
     *
     * @param id      The user ID to fetch.
     * @param session The active HTTP session.
     * @return The matched User entity.
     */
    public User getUserById(Integer id, HttpSession session);

}
