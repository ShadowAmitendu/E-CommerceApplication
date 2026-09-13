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
 *   <li>{@code POST /api/products/add}: Adds a new product record from the JSON payload.</li>
 *   <li>{@code GET /api/products/view}: Fetches and returns all products in JSON format.</li>
 *   <li>{@code PUT /api/products/update/{id}}: Updates an existing product identified by path variable ID.</li>
 *   <li>{@code DELETE /api/products/delete/{id}}: Removes a product matching the path variable ID.</li>
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
     * <p>Endpoint: {@code POST /api/products/add}
     * <p>What's happening:
     * Accepts a JSON representation of a {@link Product} in the request body, deserializes it,
     * calls {@link ProductService#insert(Product)} to persist it, and returns a confirmation message.
     *
     * @param prod The {@link Product} entity deserialized from the HTTP request body.
     * @return A confirmation string indicating successful creation.
     */
    @PostMapping("/add")
    public String addProduct(@RequestBody Product prod) {
        // Delegate insertion to the service layer
        productService.insert(prod);
        return "Added Successfully!";
    }

    /**
     * Retrieves all products available in the catalog.
     *
     * <p>Endpoint: {@code GET /api/products/view}
     * <p>What's happening:
     * Calls {@link ProductService#fetchProd()} to obtain the product list and serializes the result into a JSON array.
     *
     * @return A {@link List} of all {@link Product} objects.
     */
    @GetMapping("/view")
    public List<Product> getAllProduct() {
        // Fetch all product records from the service layer
        return productService.fetchProd();
    }

    /**
     * Updates an existing product's information.
     *
     * <p>Endpoint: {@code PUT /api/products/update/{id}}
     * <p>What's happening:
     * Extracts the target product ID from the URI path and new values from the request body JSON,
     * then delegates the update to {@link ProductService#modifyProd(String, Product)}.
     *
     * @param id   The primary key ID of the product from the URL path.
     * @param prod The {@link Product} object containing updated fields.
     * @return A confirmation string indicating successful update.
     */
    @PutMapping("/update/{id}")
    public String updateProduct(@PathVariable String id, @RequestBody Product prod) {
        // Delegate modification to the service layer
        productService.modifyProd(id, prod);
        return "Updated Successfully!";
    }

    /**
     * Deletes a product by its unique identifier.
     *
     * <p>Endpoint: {@code DELETE /api/products/delete/{id}}
     * <p>What's happening:
     * Extracts the target product ID from the URI path and calls {@link ProductService#delProd(String)}
     * to delete the product from the database.
     *
     * @param id The unique identifier of the product to delete from the URL path.
     * @return A confirmation string indicating successful deletion.
     */
    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable String id) {
        // Delegate deletion to the service layer
        productService.delProd(id);
        return "Deleted Successfully!";
    }
}
