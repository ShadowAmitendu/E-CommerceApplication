package com.example.api.repository;

import com.example.api.entity.Order;
import com.example.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data Access Object (Repository) for {@link Order} entities.
 *
 * <p>What's happening here:
 * This interface extends Spring Data JPA's {@link JpaRepository}, supplying type-safe CRUD operations,
 * pagination, and sorting for the {@link Order} entity with a {@link Long} primary key.
 *
 * <p>What is done:
 * <ul>
 *   <li>Inherits standard persistence methods (e.g., {@code save}, {@code findById}, {@code findAll}, {@code deleteById}).</li>
 *   <li>Declares query method {@link #findByUserId(int)} to retrieve all orders placed by a specific user ID.</li>
 *   <li>Declares query method {@link #findByUser(User)} to retrieve orders by user entity.</li>
 *   <li>Spring Data JPA generates the proxy implementation at runtime and registers it in the application context.</li>
 * </ul>
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Retrieves all orders associated with a specific user ID.
     *
     * <p>Spring Data JPA derives the query automatically:
     * {@code SELECT * FROM orders WHERE user_id = ?}
     *
     * @param userId The unique integer identifier of the user.
     * @return A list of {@link Order} entities belonging to the specified user.
     */
    List<Order> findByUserId(int userId);

    /**
     * Retrieves all orders placed by the specified {@link User} entity.
     *
     * @param user The {@link User} entity who placed the orders.
     * @return A list of {@link Order} entities matching the user.
     */
    List<Order> findByUser(User user);
}
