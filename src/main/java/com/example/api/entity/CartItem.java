package com.example.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

/**
 * Entity representing an individual product item inside a user's shopping cart.
 *
 * <p>What's happening here:
 * This class maps to the {@code cart_items} table in the database.
 * Each record associates a {@link User} with a {@link Product} and tracks
 * the desired purchase quantity before an order is placed.
 *
 * <p>What is done:
 * <ul>
 *   <li>Enforces a unique constraint on (user_id, product_id) so a user has at most one cart row per product.</li>
 *   <li>Establishes foreign keys to {@link User} and {@link Product}.</li>
 *   <li>Enforces that {@code quantity} is at least 1.</li>
 * </ul>
 */
@Entity
@Table(name = "cart_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private int quantity;

    public CartItem() {
    }

    public CartItem(User user, Product product, int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
