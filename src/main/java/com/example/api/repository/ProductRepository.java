package com.example.api.repository;

import com.example.api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data Access Object (Repository) for {@link Product} entities.
 *
 * <p>What's happening here:
 * This interface extends Spring Data JPA's {@link JpaRepository}, supplying type-safe CRUD operations,
 * pagination, and sorting for the {@link Product} entity with a {@link String} primary key.
 *
 * <p>What is done:
 * <ul>
 *   <li>Inherits standard persistence methods without boilerplate code, including:
 *     <ul>
 *       <li>{@code save(Product)} - inserts or updates a product.</li>
 *       <li>{@code findById(String id)} - fetches a product by its unique ID.</li>
 *       <li>{@code findAll()} - retrieves all products.</li>
 *       <li>{@code deleteById(String id)} - removes a product record by its primary key.</li>
 *     </ul>
 *   </li>
 *   <li>Spring Data generates the proxy implementation at runtime and registers it in the application context.</li>
 * </ul>
 */
public interface ProductRepository extends JpaRepository<Product, String> {

}
