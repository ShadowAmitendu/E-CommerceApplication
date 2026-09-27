package com.example.api.controller;

import com.example.api.entity.Product;
import com.example.api.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 *   <li>{@code POST /api/products} (201 Created): Adds a new product record from the JSON payload.</li>
 *   <li>{@code GET /api/products} (200 OK): Fetches and returns all products in JSON format.</li>
 *   <li>{@code GET /api/products/{id}} (200 OK): Fetches a single product by its unique ID.</li>
 *   <li>{@code PUT /api/products/{id}} (200 OK): Updates an existing product identified by path variable ID.</li>
 *   <li>{@code DELETE /api/products/{id}} (200 OK): Removes a product matching the path variable ID.</li>
 *   <li>{@link #checkLogin(HttpSession)}: Enforces admin access restriction via {@link HttpSession} attributes.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    /**
     * Service layer dependency handling product business logic.
     * Injected via constructor for immutability and testability.
     */
    private final ProductService productService;

    /**
     * Constructs a ProductController with the required service dependency.
     *
     * @param productService The {@link ProductService} implementation to delegate business logic to.
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Validates that the active HTTP session belongs to an authenticated administrator.
     *
     * <p>What's happening:
     * Inspects the session for user role attribute ("userRole") and verifies that it matches "ADMIN"
     * (case-insensitive). Throws a {@link com.example.api.exception.UnauthorizedAccessException}
     * if the user is not authenticated or lacks administrator privileges.
     *
     * @param httpSession The active {@link HttpSession} to validate.
     * @throws com.example.api.exception.UnauthorizedAccessException If the session is missing or the user does not possess admin authority.
     */
    private void checkLogin(HttpSession httpSession) {
        if (httpSession == null) {
            throw new com.example.api.exception.UnauthorizedAccessException("Admin access only");
        }
        String userRole = (String) httpSession.getAttribute("userRole");
        if (userRole == null || !userRole.equalsIgnoreCase("admin")) {
            throw new com.example.api.exception.UnauthorizedAccessException("Admin access only");
        }
    }

    /**
     * Adds a new product to the catalog.
     *
     * <p>Endpoint: {@code POST /api/products} (also supports alias {@code /api/products/add})
     * <p>Response: HTTP 201 Created
     * <p>What's happening:
     * Accepts a JSON representation of a {@link Product} in the request body, deserializes it,
     * calls {@link ProductService#addProduct(Product)} to persist it, and returns a confirmation message.
     *
     * @param product     The {@link Product} entity deserialized from the HTTP request body.
     * @param httpSession The active {@link HttpSession} injected by Spring MVC for admin verification.
     * @return A {@link ResponseEntity} with HTTP 201 status and a confirmation message.
     */
    @PostMapping({"", "/add"})
    public ResponseEntity<String> createProduct(@Valid @RequestBody Product product, HttpSession httpSession) {
        // Verify that the caller is an authenticated administrator
        checkLogin(httpSession);
        // Delegate insertion to the service layer
        productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body("Added Successfully!");
    }

    /**
     * Retrieves all products available in the catalog.
     *
     * <p>Endpoint: {@code GET /api/products} (also supports alias {@code /api/products/view})
     * <p>Response: HTTP 200 OK
     * <p>What's happening:
     * Calls {@link ProductService#getAllProducts()} to obtain the product list and serializes the result into a JSON array.
     *
     * @return A {@link ResponseEntity} with HTTP 200 status and a list of all {@link Product} objects.
     */
    @GetMapping({"", "/view"})
    public ResponseEntity<List<Product>> getAllProducts() {
        // Fetch all product records from the service layer
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Retrieves a single product by its unique ID.
     *
     * <p>Endpoint: {@code GET /api/products/{id}}
     * <p>Response: HTTP 200 OK
     * <p>What's happening:
     * Extracts the target product ID from the URI path and calls {@link ProductService#getProductById(String)}.
     *
     * @param productId The unique identifier of the product from the URL path.
     * @return A {@link ResponseEntity} with HTTP 200 status and the matching {@link Product} entity.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") String productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    /**
     * Updates an existing product's information.
     *
     * <p>Endpoint: {@code PUT /api/products/{id}} (also supports alias {@code /api/products/update/{id}})
     * <p>Response: HTTP 200 OK
     * <p>What's happening:
     * Extracts the target product ID from the URI path and new values from the request body JSON,
     * then delegates the update to {@link ProductService#updateProduct(String, Product)}.
     *
     * @param productId   The primary key ID of the product from the URL path.
     * @param product     The {@link Product} object containing updated fields.
     * @param httpSession The active {@link HttpSession} injected by Spring MVC for admin verification.
     * @return A {@link ResponseEntity} with HTTP 200 status and a confirmation message.
     */
    @PutMapping({"/{id}", "/update/{id}"})
    public ResponseEntity<String> updateProduct(@PathVariable("id") String productId, @Valid @RequestBody Product product, HttpSession httpSession) {
        // Verify that the caller is an authenticated administrator
        checkLogin(httpSession);
        // Delegate modification to the service layer
        productService.updateProduct(productId, product);
        return ResponseEntity.ok("Updated Successfully!");
    }

    /**
     * Deletes a product by its unique identifier.
     *
     * <p>Endpoint: {@code DELETE /api/products/{id}} (also supports alias {@code /api/products/delete/{id}})
     * <p>Response: HTTP 200 OK
     * <p>What's happening:
     * Extracts the target product ID from the URI path and calls {@link ProductService#deleteProduct(String)}
     * to delete the product from the database.
     *
     * @param productId   The unique identifier of the product to delete from the URL path.
     * @param httpSession The active {@link HttpSession} injected by Spring MVC for admin verification.
     * @return A {@link ResponseEntity} with HTTP 200 status and a confirmation message.
     */
    @DeleteMapping({"/{id}", "/delete/{id}"})
    public ResponseEntity<String> deleteProduct(@PathVariable("id") String productId, HttpSession httpSession) {
        // Verify that the caller is an authenticated administrator
        checkLogin(httpSession);
        // Delegate deletion to the service layer
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Deleted Successfully!");
    }
}
