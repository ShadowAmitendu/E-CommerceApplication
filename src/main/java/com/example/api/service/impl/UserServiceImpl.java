package com.example.api.service.impl;

import com.example.api.entity.User;
import com.example.api.repository.UserRepository;
import com.example.api.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation providing user registration, authentication, and session handling.
 *
 * <p>What's happening here:
 * This class implements {@link UserService} and is registered as a Spring bean using the {@link Service} annotation.
 * It interacts with {@link UserRepository} to persist new users, look up users by email for authentication,
 * and maintains user state across HTTP requests via {@link HttpSession}.
 *
 * <p>What is done:
 * <ul>
 *   <li>Injects {@link UserRepository} using {@link Autowired} to query and persist user entities.</li>
 *   <li>{@link #signUp(User)}: Persists a new user record into the database.</li>
 *   <li>{@link #signIn(String, String, HttpSession)}:
 *     <ul>
 *       <li>Searches the database for a user matching the provided email.</li>
 *       <li>Throws a {@link RuntimeException} if the user does not exist.</li>
 *       <li>Validates that the entered password matches the stored password.</li>
 *       <li>Stores user details (email, name, role) in the {@link HttpSession} upon successful login.</li>
 *       <li>Returns the authenticated user entity.</li>
 *     </ul>
 *   </li>
 *   <li>{@link #signOut(HttpSession)}: Placeholder for session termination/invalidation.</li>
 * </ul>
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * Injected repository bean for querying and persisting {@link User} entities.
     */
    @Autowired
    UserRepository userRepository;

    /**
     * Registers a new user account in the system.
     *
     * <p>What's happening:
     * Saves the provided user entity into the database using {@link UserRepository#save(Object)}.
     *
     * @param user The {@link User} object containing registration details.
     */
    @Override
    public void signUp(User user) {
        // Persist the user record into the database
        userRepository.save(user);
    }

    /**
     * Authenticates a user by email and password, setting up session attributes if successful.
     *
     * <p>What's happening:
     * 1. Looks up the user by email using {@link UserRepository#findByEmail(String)}.
     * 2. Throws an exception if no matching user record is found.
     * 3. Compares the stored password with the supplied password.
     * 4. Throws an exception if the passwords do not match.
     * 5. Populates session attributes: "userEmail", "userName", and "userRole".
     * 6. Returns the logged-in user object.
     *
     * @param email    The email address entered by the user.
     * @param password The plaintext password to verify.
     * @param session  The current HTTP session for storing session attributes.
     * @return The authenticated {@link User} entity.
     * @throws RuntimeException If the user is not found or the password is invalid.
     */
    @Override
    public User signIn(String email, String password, HttpSession session) {
        // Look up the user by email address; throw an exception if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify that the supplied password matches the persisted password
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        // Store user identity and role information in HTTP session state
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userName", user.getName());
        session.setAttribute("userRole", user.getRole());

        // Return the authenticated user object
        return user;
    }

    /**
     * Signs out the user by terminating the active HTTP session.
     *
     * <p>What's happening:
     * Currently provides an empty implementation hook to invalidate or clear the {@link HttpSession}.
     *
     * @param session The current HTTP session to invalidate.
     */
    @Override
    public void signOut(HttpSession session) {
        // Can be implemented to invalidate the session, e.g., session.invalidate();
    }

}
