package com.example.api.service;

import com.example.api.entity.Order;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * Service interface defining business contracts for Order management.
 *
 * <p>What's happening here:
 * This interface establishes an abstraction layer between the order REST controller and the
 * persistence layer. It provides methods for placing orders, retrieving user orders, fetching order details,
 * and deleting/cancelling orders while validating user authentication and authorization using {@link HttpSession}.
 *
 * <p>What is done:
 * <ul>
 *   <li>Declares {@link #placeOrder(String, int, HttpSession)} to process purchasing a product.</li>
 *   <li>Declares {@link #getUserOrders(HttpSession)} to retrieve orders for the currently authenticated user.</li>
 *   <li>Declares {@link #getOrderById(Long, HttpSession)} to look up a specific order by its primary key.</li>
 *   <li>Declares {@link #deleteOrder(Long, HttpSession)} to cancel an order and restock inventory.</li>
 * </ul>
 */
public interface OrderService {

    /**
     * Places a new order for a product on behalf of the authenticated user.
     *
     * @param productId   The unique identifier of the product being purchased.
     * @param quantity    The quantity of the product to order.
     * @param httpSession The active {@link HttpSession} containing authenticated user state.
     * @return The created and persisted {@link Order} entity.
     * @throws RuntimeException If the user is not logged in, unauthorized, product not found, or stock is insufficient.
     */
    Order placeOrder(String productId, int quantity, HttpSession httpSession);

    /**
     * Retrieves all orders placed by the currently authenticated user in the session.
     *
     * @param httpSession The active {@link HttpSession} containing authenticated user credentials.
     * @return A {@link List} of {@link Order} objects belonging to the user.
     * @throws RuntimeException If the user is not logged in.
     */
    List<Order> getUserOrders(HttpSession httpSession);

    /**
     * Retrieves an order by its unique primary key ID, ensuring access authorization.
     *
     * @param orderId     The primary key identifier of the order.
     * @param httpSession The active {@link HttpSession} containing user credentials.
     * @return The matching {@link Order} entity.
     * @throws RuntimeException If the order is not found or the user is not authorized to access it.
     */
    Order getOrderById(Long orderId, HttpSession httpSession);

    /**
     * Cancels and deletes an order by its ID, restoring product inventory.
     *
     * @param orderId     The primary key identifier of the order to cancel.
     * @param httpSession The active {@link HttpSession} containing user credentials.
     * @throws RuntimeException If user is not authenticated or not authorized.
     */
    void deleteOrder(Long orderId, HttpSession httpSession);
}
