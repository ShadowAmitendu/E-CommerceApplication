package com.example.api.controller;

import com.example.api.dto.AddToCartRequest;
import com.example.api.dto.CartResponse;
import com.example.api.dto.UpdateCartItemRequest;
import com.example.api.entity.Order;
import com.example.api.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing API endpoints for managing the user's shopping cart and performing checkout.
 *
 * <p>What is done:
 * <ul>
 *   <li>{@code GET /api/cart} (200 OK): Retrieves current user's cart, items, subtotals, and total price.</li>
 *   <li>{@code POST /api/cart/add} (200 OK): Adds a product to cart or increments quantity if already present.</li>
 *   <li>{@code PUT /api/cart/{itemId}} (200 OK): Updates the quantity for a specific item in the cart.</li>
 *   <li>{@code DELETE /api/cart/{itemId}} (200 OK): Removes an item from the cart.</li>
 *   <li>{@code DELETE /api/cart} (200 OK): Clears all items in the cart.</li>
 *   <li>{@code POST /api/cart/checkout} (201 Created): Converts all cart items into orders, decrements inventory, and empties cart.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Retrieves the current user's shopping cart.
     *
     * <p>Endpoint: {@code GET /api/cart}
     * <p>Response: HTTP 200 OK with {@link CartResponse}
     */
    @GetMapping({"", "/view"})
    public ResponseEntity<CartResponse> getCart(HttpSession session) {
        return ResponseEntity.ok(cartService.getCart(session));
    }

    /**
     * Adds an item to the shopping cart.
     *
     * <p>Endpoint: {@code POST /api/cart/add} (also supports {@code POST /api/cart})
     * <p>Response: HTTP 200 OK with updated {@link CartResponse}
     */
    @PostMapping({"", "/add", "/items"})
    public ResponseEntity<CartResponse> addToCart(
            @RequestParam(name = "productId", required = false) String paramProductId,
            @RequestParam(name = "quantity", required = false) Integer paramQuantity,
            @RequestBody(required = false) AddToCartRequest bodyRequest,
            HttpSession session) {

        String resolvedProductId = paramProductId;
        Integer resolvedQuantity = paramQuantity;

        if (bodyRequest != null) {
            if (resolvedProductId == null || resolvedProductId.trim().isEmpty()) {
                resolvedProductId = bodyRequest.getProductId();
            }
            if (resolvedQuantity == null) {
                resolvedQuantity = bodyRequest.getQuantity();
            }
        }

        if (resolvedProductId == null || resolvedProductId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID is required ('productId')");
        }
        if (resolvedQuantity == null || resolvedQuantity <= 0) {
            resolvedQuantity = 1;
        }

        CartResponse updatedCart = cartService.addToCart(resolvedProductId, resolvedQuantity, session);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Updates quantity of an existing cart item.
     *
     * <p>Endpoint: {@code PUT /api/cart/{itemId}}
     * <p>Response: HTTP 200 OK with updated {@link CartResponse}
     */
    @PutMapping({"/{itemId}", "/items/{itemId}", "/update/{itemId}"})
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            HttpSession session) {
        CartResponse updatedCart = cartService.updateCartItem(itemId, request.getQuantity(), session);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Removes an item from the cart.
     *
     * <p>Endpoint: {@code DELETE /api/cart/{itemId}}
     * <p>Response: HTTP 200 OK with updated {@link CartResponse}
     */
    @DeleteMapping({"/{itemId}", "/items/{itemId}", "/delete/{itemId}"})
    public ResponseEntity<CartResponse> removeCartItem(
            @PathVariable("itemId") Long itemId,
            HttpSession session) {
        CartResponse updatedCart = cartService.removeCartItem(itemId, session);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Clears all items in the cart.
     *
     * <p>Endpoint: {@code DELETE /api/cart}
     * <p>Response: HTTP 200 OK with confirmation message
     */
    @DeleteMapping({"", "/clear"})
    public ResponseEntity<String> clearCart(HttpSession session) {
        cartService.clearCart(session);
        return ResponseEntity.ok("Cart cleared successfully!");
    }

    /**
     * Checks out all items in the shopping cart.
     *
     * <p>Endpoint: {@code POST /api/cart/checkout}
     * <p>Response: HTTP 201 Created with list of created {@link Order} objects
     */
    @PostMapping("/checkout")
    public ResponseEntity<List<Order>> checkout(HttpSession session) {
        List<Order> placedOrders = cartService.checkout(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(placedOrders);
    }
}
