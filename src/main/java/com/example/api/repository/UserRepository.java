package com.example.api.repository;

import com.example.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Data Access Object (Repository) for {@link User} entities.
 *
 * <p>What's happening here:
 * This repository interface manages persistence operations for the {@link User} entity
 * against the database, using an {@link Integer} primary key. It leverages Spring Data JPA's
 * method derivation mechanism to declare custom query methods.
 *
 * <p>What is done:
 * <ul>
 *   <li>Inherits standard CRUD operations from {@link JpaRepository}.</li>
 *   <li>Declares a query method {@link #findByEmail(String)} to look up a user by email address.</li>
 *   <li>Wraps the return type in {@link Optional} to provide null-safe handling when a user is not found.</li>
 * </ul>
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Retrieves a user record matching the given email address.
     *
     * <p>Spring Data JPA derives the SQL query automatically from the method name:
     * {@code SELECT * FROM users WHERE email = ?}
     *
     * @param email The email address to look up.
     * @return An {@link Optional} containing the matched {@link User}, or empty if no user exists with this email.
     */
    public Optional<User> findByEmail(String email);
}
