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
 *   <li>Declares operations to insert a new product.</li>
 *   <li>Declares operations to fetch all existing products.</li>
 *   <li>Declares operations to modify product details (such as price and quantity).</li>
 *   <li>Declares operations to delete a product by its ID.</li>
 * </ul>
 */
public interface ProductService {

    /**
     * Persists a new product into the catalog.
     *
     * @param prod The {@link Product} object to be added.
     */
    public void insert(Product prod);

    /**
     * Retrieves all products available in the catalog.
     *
     * @return A {@link List} containing all {@link Product} records.
     */
    public List<Product> fetchProd();

    /**
     * Updates an existing product's details identified by its ID.
     *
     * @param id   The primary key identifier of the product to modify.
     * @param prod A {@link Product} instance containing the updated property values.
     */
    public void modifyProd(String id, Product prod);

    /**
     * Removes a product from the catalog by its ID.
     *
     * @param id The primary key identifier of the product to delete.
     */
    public void delProd(String id);
}
