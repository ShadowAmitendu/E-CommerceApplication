package com.example.api.service;

import com.example.api.entity.Product;

import java.util.List;

/**
 * Service interface defining the business contract for Product management.
 *
 * <p>What's happening here:
 * This interface establishes an abstraction layer between the REST controller and the
 * data access layer. It decouples the API controller from the concrete database manipulation logic.
 *
 * <p>What is done:
 * <ul>
 *   <li>Declares operations to add a new product.</li>
 *   <li>Declares operations to retrieve all existing products.</li>
 *   <li>Declares operations to retrieve a single product by its unique ID.</li>
 *   <li>Declares operations to update product details (name, price, quantity).</li>
 *   <li>Declares operations to delete a product by its ID.</li>
 * </ul>
 */
public interface ProductService {

    /**
     * Persists a new product into the catalog.
     *
     * @param product The {@link Product} object to be added.
     */
    public void addProduct(Product product);

    /**
     * Retrieves all products available in the catalog.
     *
     * @return A {@link List} containing all {@link Product} records.
     */
    public List<Product> getAllProducts();

    /**
     * Retrieves a single product by its unique primary key identifier.
     *
     * @param id The unique identifier of the product.
     * @return The matching {@link Product} entity.
     */
    public Product getProductById(String id);

    /**
     * Updates an existing product's details identified by its ID.
     *
     * @param id      The primary key identifier of the product to modify.
     * @param product A {@link Product} instance containing updated property values.
     */
    public void updateProduct(String id, Product product);

    /**
     * Removes a product from the catalog by its ID.
     *
     * @param id The primary key identifier of the product to delete.
     */
    public void deleteProduct(String id);
}
