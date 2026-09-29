package com.example.api.service.impl;

import com.example.api.dto.UpdateProfileRequest;
import com.example.api.entity.User;
import com.example.api.exception.AuthenticationRequiredException;
import com.example.api.exception.InvalidCredentialsException;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.exception.UnauthorizedAccessException;
import com.example.api.repository.UserRepository;
import com.example.api.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;

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
 *   <li>Injects {@link UserRepository} via constructor for immutability and testability.</li>
 *   <li>{@link #register(User)}: Persists a new user record into the database.</li>
 *   <li>{@link #login(String, String, HttpSession)}:
 *     <ul>
 *       <li>Searches the database for a user matching the provided email.</li>
 *       <li>Throws a {@link ResourceNotFoundException} if the user does not exist.</li>
 *       <li>Validates that the entered password matches the stored password.</li>
 *       <li>Throws an {@link InvalidCredentialsException} if passwords do not match.</li>
 *       <li>Stores user details (userId, userEmail, userName, userRole) in the {@link HttpSession} upon successful login.</li>
 *       <li>Returns the authenticated user entity.</li>
 *     </ul>
 *   </li>
 *   <li>{@link #logout(HttpSession)}: Clears session attributes and invalidates the active HTTP session.</li>
 * </ul>
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * Repository bean for querying and persisting {@link User} entities.
     * Injected via constructor for immutability and testability.
     */
    private final UserRepository userRepository;

    /**
     * Constructs a UserServiceImpl with the required repository dependency.
     *
     * @param userRepository The {@link UserRepository} for database operations on user records.
     */
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user account in the system.
     *
     * <p>What's happening:
     * Saves the provided user entity into the database using {@link UserRepository#save(Object)}.
     *
     * @param user The {@link User} object containing registration details.
     */
    @Override
    public void register(User user) {
        // Persist the user record into the database
        userRepository.save(user);
    }

    /**
     * Authenticates a user by email and password, setting up session attributes if successful.
     *
     * <p>What's happening:
     * 1. Looks up the user by email using {@link UserRepository#findByEmail(String)}.
     * 2. Throws a {@link ResourceNotFoundException} if no matching user record is found.
     * 3. Compares the stored password with the supplied password.
     * 4. Throws an {@link InvalidCredentialsException} if the passwords do not match.
     * 5. Populates session attributes: "userId", "userEmail", "userName", and "userRole".
     * 6. Returns the logged-in user object.
     *
     * @param email       The email address entered by the user.
     * @param password    The plaintext password to verify.
     * @param httpSession The current HTTP session for storing session attributes.
     * @return The authenticated {@link User} entity.
     * @throws ResourceNotFoundException   If the user is not found by email.
     * @throws InvalidCredentialsException If the password is invalid.
     */
    @Override
    public User login(String email, String password, HttpSession httpSession) {
        // Look up user by email or throw exception if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Verify that the supplied password matches the stored password
        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid password");
        }

        // Store user identity, name, and role information in HTTP session state
        httpSession.setAttribute("userId", user.getId());
        httpSession.setAttribute("userEmail", user.getEmail());
        httpSession.setAttribute("userName", user.getName());
        httpSession.setAttribute("userRole", user.getRole());

        // Return the authenticated user object
        return user;
    }

    /**
     * Signs out the user by clearing attributes and invalidating the active HTTP session.
     *
     * <p>What's happening:
     * Unbinds user-specific identity attributes ("userId", "userEmail", "userName", "userRole")
     * and invalidates the session to prevent session fixation and clear server resources.
     *
     * @param httpSession The current HTTP session to invalidate.
     */
    @Override
    public void logout(HttpSession httpSession) {
        if (httpSession != null) {
            // Remove all stored user attributes from the session
            httpSession.removeAttribute("userId");
            httpSession.removeAttribute("userEmail");
            httpSession.removeAttribute("userName");
            httpSession.removeAttribute("userRole");

            // Invalidate the session
            try {
                httpSession.invalidate();
            } catch (IllegalStateException ignored) {
                // Session may already have been invalidated
            }
        }
    }

    /**
     * Updates profile details of the currently logged-in user.
     *
     * @param request     The profile fields to update.
     * @param httpSession The active HTTP session.
     * @return The updated User entity.
     */
    @Override
    public User updateProfile(UpdateProfileRequest request, HttpSession httpSession) {
        if (httpSession == null) {
            throw new AuthenticationRequiredException("Login required to update profile");
        }

        Object sessionUserId = httpSession.getAttribute("userId");
        String userEmail = (String) httpSession.getAttribute("userEmail");

        if (sessionUserId == null && userEmail == null) {
            throw new AuthenticationRequiredException("Login required to update profile");
        }

        User user;
        if (sessionUserId instanceof Number) {
            user = userRepository.findById(((Number) sessionUserId).intValue())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + sessionUserId));
        } else {
            user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }

        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String newPhone = request.getPhone().trim();
            if (!newPhone.equals(user.getPhone())) {
                userRepository.findByPhone(newPhone).ifPresent(existing -> {
                    if (!existing.getId().equals(user.getId())) {
                        throw new IllegalArgumentException("Phone number is already registered to another account: " + newPhone);
                    }
                });
                user.setPhone(newPhone);
            }
        }

        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
            if (request.getCurrentPassword() == null || !request.getCurrentPassword().equals(user.getPassword())) {
                throw new InvalidCredentialsException("Current password does not match");
            }
            user.setPassword(request.getNewPassword());
        }

        User updatedUser = userRepository.save(user);

        // Keep session attribute synchronized
        httpSession.setAttribute("userName", updatedUser.getName());

        return updatedUser;
    }

    /**
     * Retrieves all registered users in the system. Requires administrator privileges.
     *
     * @param httpSession The active HTTP session to verify administrator authority.
     * @return List of all User entities.
     */
    @Override
    public List<User> getAllUsers(HttpSession httpSession) {
        if (httpSession == null) {
            throw new UnauthorizedAccessException("Admin access only");
        }
        String userRole = (String) httpSession.getAttribute("userRole");
        if (userRole == null || !userRole.equalsIgnoreCase("admin")) {
            throw new UnauthorizedAccessException("Admin access only");
        }
        return userRepository.findAll();
    }

    /**
     * Retrieves a single user by primary key ID. Requires administrator privileges or account ownership.
     *
     * @param id          The user ID to fetch.
     * @param httpSession The active HTTP session.
     * @return The matched User entity.
     */
    @Override
    public User getUserById(Integer id, HttpSession httpSession) {
        if (httpSession == null) {
            throw new AuthenticationRequiredException("Login required");
        }
        Object sessionUserId = httpSession.getAttribute("userId");
        String userRole = (String) httpSession.getAttribute("userRole");
        if (sessionUserId == null) {
            throw new AuthenticationRequiredException("Login required");
        }
        boolean isAdmin = userRole != null && userRole.equalsIgnoreCase("admin");
        int currentUserId = (sessionUserId instanceof Number)
                ? ((Number) sessionUserId).intValue()
                : Integer.parseInt(sessionUserId.toString());
        if (!isAdmin && currentUserId != id) {
            throw new UnauthorizedAccessException("Unauthorized access to user profile");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

}
