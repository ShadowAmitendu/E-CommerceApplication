package com.example.api.service.impl;

import com.example.api.dto.CartItemDto;
import com.example.api.dto.CartResponse;
import com.example.api.entity.CartItem;
import com.example.api.entity.Order;
import com.example.api.entity.Product;
import com.example.api.entity.User;
import com.example.api.exception.AuthenticationRequiredException;
import com.example.api.exception.InsufficientStockException;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.CartItemRepository;
import com.example.api.repository.OrderRepository;
import com.example.api.repository.ProductRepository;
import com.example.api.repository.UserRepository;
import com.example.api.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation managing user shopping carts and cart-to-order checkout.
 */
@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           OrderRepository orderRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Resolves the authenticated user from the active session.
     */
    private User getUserFromSession(HttpSession session) {
        if (session == null) {
            throw new AuthenticationRequiredException("Please login to access your shopping cart");
        }
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            throw new AuthenticationRequiredException("Please login to access your shopping cart");
        }
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(HttpSession session) {
        User user = getUserFromSession(session);
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        List<CartItemDto> dtos = new ArrayList<>();
        double totalAmount = 0.0;
        int totalItems = 0;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            double subtotal = Math.round(product.getPrice() * item.getQuantity() * 100.0) / 100.0;
            totalAmount += subtotal;
            totalItems += item.getQuantity();

            dtos.add(new CartItemDto(
                    item.getId(),
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    item.getQuantity(),
                    subtotal,
                    product.getQuantity()
            ));
        }

        return new CartResponse(dtos, totalItems, totalAmount);
    }

    @Override
    @Transactional
    public CartResponse addToCart(String productId, int quantity, HttpSession session) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        User user = getUserFromSession(session);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserIdAndProductId(user.getId(), productId);

        int targetQuantity = quantity;
        if (existingItemOpt.isPresent()) {
            targetQuantity = existingItemOpt.get().getQuantity() + quantity;
        }

        if (targetQuantity > product.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product '" + product.getName()
                    + "' (requested total: " + targetQuantity + ", available stock: " + product.getQuantity() + ")");
        }

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(targetQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(user, product, quantity);
            cartItemRepository.save(newItem);
        }

        return getCart(session);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long cartItemId, int quantity, HttpSession session) {
        User user = getUserFromSession(session);

        if (quantity <= 0) {
            return removeCartItem(cartItemId, session);
        }

        CartItem item = cartItemRepository.findByIdAndUserId(cartItemId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (quantity > item.getProduct().getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product '" + item.getProduct().getName()
                    + "' (requested: " + quantity + ", available stock: " + item.getProduct().getQuantity() + ")");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return getCart(session);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long cartItemId, HttpSession session) {
        User user = getUserFromSession(session);
        CartItem item = cartItemRepository.findByIdAndUserId(cartItemId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        cartItemRepository.delete(item);
        return getCart(session);
    }

    @Override
    @Transactional
    public void clearCart(HttpSession session) {
        User user = getUserFromSession(session);
        cartItemRepository.deleteByUserId(user.getId());
    }

    @Override
    @Transactional
    public List<Order> checkout(HttpSession session) {
        User user = getUserFromSession(session);
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout with an empty cart");
        }

        // Validate stock for all items prior to making changes
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Cannot checkout: product '" + product.getName()
                        + "' has insufficient stock (required: " + item.getQuantity()
                        + ", available: " + product.getQuantity() + ")");
            }
        }

        List<Order> placedOrders = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Atomically deduct inventory stock and record orders
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            Order order = new Order();
            order.setUser(user);
            order.setProduct(product);
            order.setQuantity(item.getQuantity());
            order.setOrderDate(now);

            placedOrders.add(orderRepository.save(order));
        }

        // Clear cart items upon successful order generation
        cartItemRepository.deleteByUserId(user.getId());

        return placedOrders;
    }
}
