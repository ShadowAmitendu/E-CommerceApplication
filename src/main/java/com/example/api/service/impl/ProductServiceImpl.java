package com.example.api.service.impl;

import com.example.api.entity.Product;
import com.example.api.repository.ProductRepository;
import com.example.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service implementation providing business logic for Product operations.
 *
 * <p>What's happening here:
 * Annotated with {@link Service}, this class is managed as a Spring singleton bean in the service layer.
 * It coordinates business rules and communicates directly with {@link ProductRepository} to interact
 * with the underlying database.
 *
 * <p>What is done:
 * <ul>
 *   <li>Injects {@link ProductRepository} via {@link Autowired} for database interaction.</li>
 *   <li>Implements {@link #addProduct(Product)} to persist a new product record.</li>
 *   <li>Implements {@link #getAllProducts()} to retrieve all product records.</li>
 *   <li>Implements {@link #getProductById(String)} to fetch a single product by ID.</li>
 *   <li>Implements {@link #updateProduct(String, Product)} to fetch an existing entity, update its name,
 *       quantity, and price, and save the updated entity back to the database.</li>
 *   <li>Implements {@link #deleteProduct(String)} to delete a product by its identifier.</li>
 * </ul>
 */
@Service
public class ProductServiceImpl implements ProductService {

    /**
     * Injected repository bean for executing CRUD operations on the {@code product} table.
     */
    @Autowired
    ProductRepository productRepository;

    /**
     * Inserts a new product into the database.
     *
     * <p>What's happening:
     * Calls {@link ProductRepository#save(Object)} with the incoming product entity to persist it in the table.
     *
     * @param product The {@link Product} entity to persist.
     */
    @Override
    public void addProduct(Product product) {
        // Save the product entity into the database
        productRepository.save(product);
    }

    /**
     * Fetches all products currently stored in the database.
     *
     * <p>What's happening:
     * Executes a {@code SELECT * FROM product} query via {@link ProductRepository#findAll()}.
     *
     * @return A list containing all {@link Product} entities.
     */
    @Override
    public List<Product> getAllProducts() {
        // Retrieve and return all product records
        return productRepository.findAll();
    }

    /**
     * Retrieves a single product by its unique primary key identifier.
     *
     * <p>What's happening:
     * Queries the database using {@link ProductRepository#findById(Object)}. Throws a
     * {@link NoSuchElementException} if no matching product record is found.
     *
     * @param id The unique identifier of the product.
     * @return The found {@link Product} entity.
     * @throws NoSuchElementException If no product exists with the given ID.
     */
    @Override
    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }

    /**
     * Modifies the details (name, price, quantity) of an existing product.
     *
     * <p>What's happening:
     * 1. Looks up the product by primary key using {@link ProductRepository#findById(Object)}.
     * 2. Throws a {@link NoSuchElementException} if the product does not exist.
     * 3. Mutates the retrieved entity with the updated values.
     * 4. Persists the modified entity back into the database using {@link ProductRepository#save(Object)}.
     *
     * @param id      The unique identifier of the product to modify.
     * @param product A {@link Product} object holding the updated field values.
     * @throws NoSuchElementException If no product exists with the given ID.
     */
    @Override
    public void updateProduct(String id, Product product) {
        // Find existing product or throw NoSuchElementException if missing
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        
        // Update product fields with new values
        if (product.getName() != null) {
            existingProduct.setName(product.getName());
        }
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setPrice(product.getPrice());
        
        // Persist the updated product
        productRepository.save(existingProduct);
    }

    /**
     * Deletes a product from the database by its identifier.
     *
     * <p>What's happening:
     * Calls {@link ProductRepository#deleteById(Object)} which executes a delete query for the corresponding row.
     *
     * @param id The unique identifier of the product to delete.
     */
    @Override
    public void deleteProduct(String id) {
        // Delete the product matching the given primary key
        productRepository.deleteById(id);
    }

}
