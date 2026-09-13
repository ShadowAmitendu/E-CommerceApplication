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
 *   <li>Defines session termination through {@link #signOut(HttpSession)}.</li>
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
     * Backward-compatible alias for {@link #register(User)}.
     *
     * @param user The {@link User} entity to be persisted.
     */
    default void signUp(User user) {
        register(user);
    }

    /**
     * Authenticates a user with the provided credentials and initializes an active session.
     *
     * @param email    The email address provided by the user during login.
     * @param password The plaintext password provided during login.
     * @param session  The current HTTP session object to store logged-in user state.
     * @return The authenticated {@link User} entity.
     * @throws RuntimeException If the email is not registered or if password does not match.
     */
    public User login(String email, String password, HttpSession session);

    /**
     * Backward-compatible alias for {@link #login(String, String, HttpSession)}.
     *
     * @param email    The email address.
     * @param password The password.
     * @param session  The HTTP session.
     * @return The authenticated {@link User} entity.
     */
    default User signIn(String email, String password, HttpSession session) {
        return login(email, password, session);
    }

    /**
     * Terminates the user's active session and clears session data.
     *
     * @param session The HTTP session to invalidate.
     */
    public void signOut(HttpSession session);

}
