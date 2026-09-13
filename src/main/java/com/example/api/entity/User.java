package com.example.api.entity;

import jakarta.persistence.*;

/**
 * Entity representing a User in the e-commerce application.
 *
 * <p>What's happening here:
 * This class is an ORM (Object-Relational Mapping) entity mapped to the {@code users} table in the database.
 * It stores customer, merchant, or administrator account credentials and contact details.
 *
 * <p>What is done:
 * <ul>
 *   <li>Designates an auto-incrementing primary key ID using {@link GenerationType#IDENTITY}.</li>
 *   <li>Enforces unique and non-null constraints on the {@code email} field to prevent duplicate accounts.</li>
 *   <li>Enforces a unique constraint on the {@code phone} field.</li>
 *   <li>Stores user profile details, credentials (password), and security roles (e.g., "CUSTOMER", "ADMIN").</li>
 *   <li>Exposes standard getter and setter methods to access and modify user information.</li>
 * </ul>
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Primary key identifier for the user.
     * Auto-incremented by the underlying database sequence/identity column.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Full name of the user.
     */
    private String name;

    /**
     * Email address used for authentication and communications.
     * Must be unique across all user records and cannot be null.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Contact phone number of the user.
     * Must be unique across all registered users.
     */
    @Column(unique = true)
    private String phone;

    /**
     * User's account password.
     */
    private String password;

    /**
     * Authorization role assigned to the user (e.g., "ADMIN", "USER", "CUSTOMER").
     */
    private String role;

    /**
     * Gets the unique user ID.
     *
     * @return The auto-generated user ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user ID.
     *
     * @param id The user ID to assign.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the full name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the full name of the user.
     *
     * @param name The user's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email address.
     *
     * @return The email string.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param email The user's email string.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's contact phone number.
     *
     * @return The phone number string.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the user's contact phone number.
     *
     * @param phone The phone number string.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the user's password.
     *
     * @return The password string.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password The password string.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the security role assigned to the user.
     *
     * @return The role string (e.g., "ADMIN" or "USER").
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the security role assigned to the user.
     *
     * @param role The role string.
     */
    public void setRole(String role) {
        this.role = role;
    }
}
