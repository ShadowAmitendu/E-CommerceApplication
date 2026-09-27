package com.example.api.controller;

import com.example.api.entity.Order;
import com.example.api.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller exposing API endpoints for placing and managing customer orders.
 *
 * <p>What's happening here:
 * This controller handles incoming HTTP requests targeted at the {@code /api/orders} base URI.
 * It matches the URL patterns and REST conventions established across the application (e.g., in {@link ProductController}),
 * delegating order operations to {@link OrderService} while leveraging {@link HttpSession} for authentication.
 *
 * <p>What is done:
 * <ul>
 *   <li>{@code POST /api/orders} (201 Created): Places a new order for a specified product.</li>
 *   <li>{@code GET /api/orders} (200 OK): Retrieves all orders for the current user.</li>
 *   <li>{@code GET /api/orders/{id}} (200 OK): Retrieves details of a specific order by ID.</li>
 *   <li>{@code DELETE /api/orders/{id}} (200 OK): Cancels and deletes an order, restocking inventory.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    /**
     * Service layer dependency handling order processing logic.
     * Injected via constructor for immutability and testability.
     */
    private final OrderService orderService;

    /**
     * Constructs an OrderController with the required service dependency.
     *
     * @param orderService The {@link OrderService} implementation to delegate business logic to.
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Places a new order for a product on behalf of the logged-in customer.
     *
     * <p>Endpoint: {@code POST /api/orders} (alias: {@code /add})
     * <p>Response: HTTP 201 Created
     * <p>What's happening:
     * Accepts order parameters (product identifier and quantity) via query parameters, form data, or JSON request body.
     * Resolves the target product identifier and purchase quantity, delegates order creation and inventory deduction to
     * {@link OrderService#placeOrder(String, int, HttpSession)}, and returns the persisted {@link Order} entity.
     *
     * @param productId        The product identifier requested by the client.
     * @param quantity         The number of product units requested by the client.
     * @param orderRequestBody Optional JSON payload containing product and quantity details.
     * @param httpSession      The active {@link HttpSession} injected by Spring MVC.
     * @return A {@link ResponseEntity} with HTTP 201 status and the created {@link Order} record as JSON.
     */
    @PostMapping({"", "/add"})
    public ResponseEntity<Order> placeOrder(
            @RequestParam(name = "productId", required = false) String productId,
            @RequestParam(name = "quantity", required = false) Integer quantity,
            @RequestBody(required = false) Map<String, Object> orderRequestBody,
            HttpSession httpSession) {

        // Resolve product identifier from query parameter
        String resolvedProductId = productId;

        // Resolve purchase quantity from query parameter
        Integer resolvedQuantity = quantity;

        // Resolve attributes from the JSON request body payload if present
        if (orderRequestBody != null) {
            if (resolvedProductId == null || resolvedProductId.trim().isEmpty()) {
                Object extractedProductId = orderRequestBody.get("productId");
                if (extractedProductId == null && orderRequestBody.get("product") instanceof Map) {
                    extractedProductId = ((Map<?, ?>) orderRequestBody.get("product")).get("id");
                }
                if (extractedProductId != null) {
                    resolvedProductId = extractedProductId.toString();
                }
            }
            if (resolvedQuantity == null) {
                Object extractedQuantity = orderRequestBody.get("quantity");
                if (extractedQuantity instanceof Number) {
                    resolvedQuantity = ((Number) extractedQuantity).intValue();
                } else if (extractedQuantity != null) {
                    try {
                        resolvedQuantity = Integer.parseInt(extractedQuantity.toString());
                    } catch (NumberFormatException ignored) {
                        // Handled by validation below
                    }
                }
            }
        }

        // Validate presence of required inputs
        if (resolvedProductId == null || resolvedProductId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product identifier is required (parameter 'productId')");
        }
        if (resolvedQuantity == null) {
            throw new IllegalArgumentException("Order quantity is required (parameter 'quantity')");
        }

        // Delegate order placement to the service layer
        Order createdOrder = orderService.placeOrder(resolvedProductId, resolvedQuantity, httpSession);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * Retrieves all orders placed by the currently authenticated user.
     *
     * <p>Endpoint: {@code GET /api/orders} (alias: {@code /view})
     * <p>Response: HTTP 200 OK
     * <p>What's happening:
     * Validates active session state and delegates to {@link OrderService#getUserOrders(HttpSession)} to query
     * orders matching the authenticated user's ID, returning the collection as JSON.
     *
     * @param httpSession The active {@link HttpSession} injected by Spring MVC.
     * @return A {@link ResponseEntity} with HTTP 200 status and a list of {@link Order} entities belonging to the user.
     */
    @GetMapping({"", "/view"})
    public ResponseEntity<List<Order>> getAllOrders(HttpSession httpSession) {
        // Fetch order history for the active session user
        return ResponseEntity.ok(orderService.getUserOrders(httpSession));
    }

    /**
     * Retrieves a single order by its unique primary key ID.
     *
     * <p>Endpoint: {@code GET /api/orders/{id}}
     * <p>Response: HTTP 200 OK
     * <p>What's happening:
     * Extracts the target order ID from the URL path and delegates to {@link OrderService#getOrderById(Long, HttpSession)},
     * ensuring that only authorized users or administrators can view the order.
     *
     * @param orderId     The primary key identifier of the order.
     * @param httpSession The active {@link HttpSession} injected by Spring MVC.
     * @return A {@link ResponseEntity} with HTTP 200 status and the matching {@link Order} entity.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") Long orderId, HttpSession httpSession) {
        // Retrieve order details by ID from the service layer
        return ResponseEntity.ok(orderService.getOrderById(orderId, httpSession));
    }

    /**
     * Cancels and deletes an order by its unique identifier, restoring inventory stock.
     *
     * <p>Endpoint: {@code DELETE /api/orders/{id}} (alias: {@code /delete/{id}})
     * <p>Response: HTTP 200 OK
     * <p>What's happening:
     * Extracts the target order ID from the URL path and delegates to {@link OrderService#deleteOrder(Long, HttpSession)}
     * to verify caller authorization, restore purchased quantities back to the product stock, and remove the order record.
     *
     * @param orderId     The unique identifier of the order to cancel.
     * @param httpSession The active {@link HttpSession} injected by Spring MVC.
     * @return A {@link ResponseEntity} with HTTP 200 status and a confirmation message.
     */
    @DeleteMapping({"/{id}", "/delete/{id}"})
    public ResponseEntity<String> deleteOrder(@PathVariable("id") Long orderId, HttpSession httpSession) {
        // Delegate order deletion and stock restoration to the service layer
        orderService.deleteOrder(orderId, httpSession);
        return ResponseEntity.ok("Deleted Successfully!");
    }
}
