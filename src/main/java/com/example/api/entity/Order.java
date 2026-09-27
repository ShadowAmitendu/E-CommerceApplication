package com.example.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

/**
 * Entity representing an Order placed by a customer in the e-commerce
 * application.
 *
 * <p>
 * What's happening here:
 * This class is an ORM (Object-Relational Mapping) entity mapped to the
 * {@code orders} table in the database.
 * It establishes a transactional record associating an authenticated
 * {@link User} with a purchased {@link Product},
 * tracking the unit quantity and the exact order timestamp.
 *
 * <p>
 * What is done:
 * <ul>
 * <li>Maps table columns to Java properties: {@code id}, {@code user},
 * {@code product}, {@code quantity}, and {@code orderDate}.</li>
 * <li>Designates {@code id} as an auto-incrementing primary key using
 * {@link GenerationType#IDENTITY}.</li>
 * <li>Maps a foreign-key relationship to {@link User} via {@link ManyToOne} and
 * {@link JoinColumn} ({@code user_id}).</li>
 * <li>Maps a foreign-key relationship to {@link Product} via {@link ManyToOne}
 * and {@link JoinColumn} ({@code product_id}).</li>
 * <li>Exposes standard getter and setter methods to access and modify order
 * attributes.</li>
 * </ul>
 */
@Entity
@Table(name = "order")
public class Order {

    /**
     * Primary key identifier for the order.
     * Auto-incremented by the underlying database sequence/identity column.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The registered user who placed this order.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The product item purchased in this order.
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Number of product units purchased.
     * Validated to ensure at least one unit is ordered.
     */
    @Min(value = 1, message = "Order quantity must be at least 1")
    private int quantity;

    /**
     * Timestamp recording when the order was placed.
     */
    private LocalDateTime orderDate;

    /**
     * Gets the unique order ID.
     *
     * @return The auto-generated order primary key ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the order ID.
     *
     * @param id The primary key ID to assign.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the customer who placed the order.
     *
     * @return The associated {@link User} entity.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the customer for this order.
     *
     * @param user The {@link User} who placed the order.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the product that was ordered.
     *
     * @return The ordered {@link Product} entity.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the ordered product.
     *
     * @param product The {@link Product} entity.
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Gets the quantity of product ordered.
     *
     * @return The count of items ordered.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of product ordered.
     *
     * @param quantity The count of items ordered.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the date and time when the order was placed.
     *
     * @return A {@link LocalDateTime} timestamp.
     */
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the date and time when the order was placed.
     *
     * @param orderDate The timestamp of the transaction.
     */
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
}
