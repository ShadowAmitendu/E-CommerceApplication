package com.example.api.controller;

import com.example.api.entity.Product;
import com.example.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing API endpoints for managing the product catalog.
 *
 * <p>What's happening here:
 * Annotated with {@link RestController} and {@link RequestMapping}, this controller receives and processes
 * HTTP requests targeted at the {@code /api/products} base URI. It serializes responses automatically into JSON
 * or plain text and delegates all business operations to {@link ProductService}.
 *
 * <p>What is done:
 * <ul>
 *   <li>{@code POST /api/products} (alias: {@code /add}): Adds a new product record from the JSON payload.</li>
 *   <li>{@code GET /api/products} (alias: {@code /view}): Fetches and returns all products in JSON format.</li>
 *   <li>{@code GET /api/products/{id}}: Fetches a single product by its unique ID.</li>
 *   <li>{@code PUT /api/products/{id}} (alias: {@code /update/{id}}): Updates an existing product identified by path variable ID.</li>
 *   <li>{@code DELETE /api/products/{id}} (alias: {@code /delete/{id}}): Removes a product matching the path variable ID.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    /**
     * Injected service layer dependency handling product business logic.
     */
    @Autowired
    private ProductService productService;

    /**
     * Adds a new product to the catalog.
     *
     * <p>Endpoint: {@code POST /api/products} (also supports legacy {@code /api/products/add})
     * <p>What's happening:
     * Accepts a JSON representation of a {@link Product} in the request body, deserializes it,
     * calls {@link ProductService#addProduct(Product)} to persist it, and returns a confirmation message.
     *
     * @param product The {@link Product} entity deserialized from the HTTP request body.
     * @return A confirmation string indicating successful creation.
     */
    @PostMapping({"", "/add"})
    public String createProduct(@RequestBody Product product) {
        // Delegate insertion to the service layer
        productService.addProduct(product);
        return "Added Successfully!";
    }

    /**
     * Retrieves all products available in the catalog.
     *
     * <p>Endpoint: {@code GET /api/products} (also supports legacy {@code /api/products/view})
     * <p>What's happening:
     * Calls {@link ProductService#getAllProducts()} to obtain the product list and serializes the result into a JSON array.
     *
     * @return A {@link List} of all {@link Product} objects.
     */
    @GetMapping({"", "/view"})
    public List<Product> getAllProducts() {
        // Fetch all product records from the service layer
        return productService.getAllProducts();
    }

    /**
     * Retrieves a single product by its unique ID.
     *
     * <p>Endpoint: {@code GET /api/products/{id}}
     * <p>What's happening:
     * Extracts the target product ID from the URI path and calls {@link ProductService#getProductById(String)}.
     *
     * @param id The unique identifier of the product from the URL path.
     * @return The matching {@link Product} entity.
     */
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    /**
     * Updates an existing product's information.
     *
     * <p>Endpoint: {@code PUT /api/products/{id}} (also supports legacy {@code /api/products/update/{id}})
     * <p>What's happening:
     * Extracts the target product ID from the URI path and new values from the request body JSON,
     * then delegates the update to {@link ProductService#updateProduct(String, Product)}.
     *
     * @param id      The primary key ID of the product from the URL path.
     * @param product The {@link Product} object containing updated fields.
     * @return A confirmation string indicating successful update.
     */
    @PutMapping({"/{id}", "/update/{id}"})
    public String updateProduct(@PathVariable String id, @RequestBody Product product) {
        // Delegate modification to the service layer
        productService.updateProduct(id, product);
        return "Updated Successfully!";
    }

    /**
     * Deletes a product by its unique identifier.
     *
     * <p>Endpoint: {@code DELETE /api/products/{id}} (also supports legacy {@code /api/products/delete/{id}})
     * <p>What's happening:
     * Extracts the target product ID from the URI path and calls {@link ProductService#deleteProduct(String)}
     * to delete the product from the database.
     *
     * @param id The unique identifier of the product to delete from the URL path.
     * @return A confirmation string indicating successful deletion.
     */
    @DeleteMapping({"/{id}", "/delete/{id}"})
    public String deleteProduct(@PathVariable String id) {
        // Delegate deletion to the service layer
        productService.deleteProduct(id);
        return "Deleted Successfully!";
    }
}
