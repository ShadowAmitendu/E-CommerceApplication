package com.example.api.service;

import com.example.api.dto.CartResponse;
import com.example.api.entity.Order;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * Service interface defining operations for managing customer shopping carts and cart checkout.
 */
public interface CartService {

    /**
     * Retrieves the current user's shopping cart, calculating subtotals and totals.
     *
     * @param session The active {@link HttpSession}.
     * @return The populated {@link CartResponse}.
     */
    CartResponse getCart(HttpSession session);

    /**
     * Adds a product to the user's cart or increments the quantity if already present.
     *
     * @param productId The ID of the product.
     * @param quantity  The quantity to add.
     * @param session   The active {@link HttpSession}.
     * @return The updated {@link CartResponse}.
     */
    CartResponse addToCart(String productId, int quantity, HttpSession session);

    /**
     * Updates the quantity of an existing item in the cart.
     *
     * @param cartItemId The unique identifier of the cart item.
     * @param quantity   The new quantity.
     * @param session    The active {@link HttpSession}.
     * @return The updated {@link CartResponse}.
     */
    CartResponse updateCartItem(Long cartItemId, int quantity, HttpSession session);

    /**
     * Removes an item from the cart.
     *
     * @param cartItemId The unique identifier of the cart item to remove.
     * @param session    The active {@link HttpSession}.
     * @return The updated {@link CartResponse}.
     */
    CartResponse removeCartItem(Long cartItemId, HttpSession session);

    /**
     * Clears all items from the user's shopping cart.
     *
     * @param session The active {@link HttpSession}.
     */
    void clearCart(HttpSession session);

    /**
     * Completes checkout for all items in the shopping cart:
     * verifies inventory stock, deducts stock, creates {@link Order} records,
     * and clears the cart atomically.
     *
     * @param session The active {@link HttpSession}.
     * @return A list of created {@link Order} entities.
     */
    List<Order> checkout(HttpSession session);
}
