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
 *   <li>Implements {@link #insert(Product)} to persist a new product record.</li>
 *   <li>Implements {@link #fetchProd()} to retrieve all product records.</li>
 *   <li>Implements {@link #modifyProd(String, Product)} to fetch an existing entity, update its quantity
 *       and price, and save the updated entity back to the database.</li>
 *   <li>Implements {@link #delProd(String)} to delete a product by its identifier.</li>
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
     * @param prod The {@link Product} entity to persist.
     */
    @Override
    public void insert(Product prod) {
        // Save the product entity into the database
        productRepository.save(prod);
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
    public List<Product> fetchProd() {
        // Retrieve and return all product records
        return productRepository.findAll();
    }

    /**
     * Modifies the price and quantity of an existing product.
     *
     * <p>What's happening:
     * 1. Looks up the product by primary key using {@link ProductRepository#findById(Object)}.
     * 2. Throws a {@link NoSuchElementException} if the product does not exist.
     * 3. Mutates the retrieved entity with the new quantity and price values.
     * 4. Persists the modified entity back into the database using {@link ProductRepository#save(Object)}.
     *
     * @param id   The unique identifier of the product to modify.
     * @param prod A {@link Product} object holding the new quantity and price values.
     * @throws NoSuchElementException If no product exists with the given ID.
     */
    @Override
    public void modifyProd(String id, Product prod) {
        // Find existing product or throw NoSuchElementException if missing
        Product product = productRepository.findById(id).orElseThrow();
        
        // Update product fields with new values
        product.setQuantity(prod.getQuantity());
        product.setPrice(prod.getPrice());
        
        // Persist the updated product
        productRepository.save(product);
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
    public void delProd(String id) {
        // Delete the product matching the given primary key
        productRepository.deleteById(id);
    }

}
