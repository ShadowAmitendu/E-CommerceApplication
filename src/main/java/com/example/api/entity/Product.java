package com.example.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing a Product in the e-commerce inventory database.
 *
 * <p>What's happening here:
 * This class is a JPA (Jakarta Persistence) entity mapped to the database table {@code product}.
 * Hibernate / JPA uses this class to read, persist, update, and delete product records in the database.
 *
 * <p>What is done:
 * <ul>
 *   <li>Maps table columns to private Java member variables: {@code id}, {@code name}, {@code quantity}, and {@code price}.</li>
 *   <li>Designates the {@code id} attribute as the primary key using {@link Id}.</li>
 *   <li>Provides standard getter and setter methods to access and mutate product properties.</li>
 * </ul>
 */
@Entity
@Table(name = "product")
public class Product {

    /**
     * Unique identifier for the product (Primary Key).
     * Typically assigned as a String identifier or SKU code.
     */
    @Id
    private String id;

    /**
     * Name or title describing the product.
     */
    private String name;

    /**
     * Available stock quantity in the inventory.
     */
    private int quantity;

    /**
     * Unit selling price of the product.
     */
    private double price;

    /**
     * Gets the unique product ID.
     *
     * @return The product ID string.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique product ID.
     *
     * @param id The new product ID string.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the product name.
     *
     * @return The name of the product.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name.
     *
     * @param name The new name of the product.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the current stock quantity.
     *
     * @return The number of items in stock.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the stock quantity.
     *
     * @param quantity The new inventory quantity count.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the unit price.
     *
     * @return The unit price of the product.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the unit price.
     *
     * @param price The new unit price.
     */
    public void setPrice(double price) {
        this.price = price;
    }

}
