package com.example.api.service;

import com.example.api.entity.User;
import jakarta.servlet.http.HttpSession;

/**
 * Service interface defining user management and authentication contracts.
 *
 * <p>What's happening here:
 * This interface abstracts user registration (sign-up), user authentication (sign-in),
 * and session management (sign-out) operations for the application.
 *
 * <p>What is done:
 * <ul>
 *   <li>Defines user registration through {@link #signUp(User)}.</li>
 *   <li>Defines credential validation and session establishment through {@link #signIn(String, String, HttpSession)}.</li>
 *   <li>Defines session termination through {@link #signOut(HttpSession)}.</li>
 * </ul>
 */
public interface UserService {

    /**
     * Registers a new user into the system.
     *
     * @param user The {@link User} entity to be persisted.
     */
    public void signUp(User user);

    /**
     * Authenticates a user with the provided credentials and initializes an active session.
     *
     * @param email    The email address provided by the user during login.
     * @param password The plaintext password provided during login.
     * @param session  The current HTTP session object to store logged-in user state.
     * @return The authenticated {@link User} entity.
     * @throws RuntimeException If the email is not registered or if password does not match.
     */
    public User signIn(String email, String password, HttpSession session);

    /**
     * Terminates the user's active session and clears session data.
     *
     * @param session The HTTP session to invalidate.
     */
    public void signOut(HttpSession session);

}
