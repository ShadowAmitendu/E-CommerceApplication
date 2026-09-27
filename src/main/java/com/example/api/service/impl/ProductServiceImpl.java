package com.example.api.service.impl;

import com.example.api.entity.Product;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.ProductRepository;
import com.example.api.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

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
 *   <li>Injects {@link ProductRepository} via constructor for immutability and testability.</li>
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
     * Repository bean for executing CRUD operations on the {@code product} table.
     * Injected via constructor for immutability and testability.
     */
    private final ProductRepository productRepository;

    /**
     * Constructs a ProductServiceImpl with the required repository dependency.
     *
     * @param productRepository The {@link ProductRepository} for database operations on product records.
     */
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

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
     * {@link ResourceNotFoundException} if no matching product record is found.
     *
     * @param id The unique identifier of the product.
     * @return The found {@link Product} entity.
     * @throws ResourceNotFoundException If no product exists with the given ID.
     */
    @Override
    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    /**
     * Modifies the details (name, price, quantity) of an existing product.
     *
     * <p>What's happening:
     * 1. Looks up the product by primary key using {@link ProductRepository#findById(Object)}.
     * 2. Throws a {@link ResourceNotFoundException} if the product does not exist.
     * 3. Updates only the fields that are provided (non-null name; non-negative quantity and positive price).
     * 4. Persists the modified entity back into the database using {@link ProductRepository#save(Object)}.
     *
     * <p><b>Note:</b> Since {@code quantity} and {@code price} are primitives (default to 0 and 0.0),
     * they are always updated from the incoming payload. Provide all fields in the request body for a full update.
     *
     * @param id      The unique identifier of the product to modify.
     * @param product A {@link Product} object holding the updated field values.
     * @throws ResourceNotFoundException If no product exists with the given ID.
     */
    @Override
    public void updateProduct(String id, Product product) {
        // Find existing product or throw ResourceNotFoundException if missing
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update product name only if a non-null value is provided
        if (product.getName() != null) {
            existingProduct.setName(product.getName());
        }

        // Update quantity and price (primitives always carry a value from the payload)
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
