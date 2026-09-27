package com.example.api.service.impl;

import com.example.api.entity.Order;
import com.example.api.entity.Product;
import com.example.api.entity.User;
import com.example.api.exception.AuthenticationRequiredException;
import com.example.api.exception.InsufficientStockException;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.exception.UnauthorizedAccessException;
import com.example.api.repository.OrderRepository;
import com.example.api.repository.ProductRepository;
import com.example.api.repository.UserRepository;
import com.example.api.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation providing business logic for Order operations.
 *
 * <p>What's happening here:
 * Annotated with {@link Service}, this class is managed as a Spring singleton bean in the service layer.
 * It coordinates order processing workflows, validates customer authorization and inventory availability,
 * deducts product stock, maintains order records using {@link OrderRepository}, and handles cancellation/deletion.
 *
 * <p>What is done:
 * <ul>
 *   <li>Injects {@link OrderRepository}, {@link UserRepository}, and {@link ProductRepository} via constructor.</li>
 *   <li>Implements {@link #placeOrder(String, int, HttpSession)}:
 *     <ul>
 *       <li>Validates the active session and verifies that the caller has a valid user role.</li>
 *       <li>Verifies available stock and decrements inventory accordingly.</li>
 *       <li>Persists a new order record linked to the authenticated user and product.</li>
 *       <li>Wrapped in {@link Transactional} to ensure stock decrement and order save are atomic.</li>
 *     </ul>
 *   </li>
 *   <li>Implements {@link #getUserOrders(HttpSession)}:
 *     <ul>
 *       <li>Extracts user credentials from {@link HttpSession}.</li>
 *       <li>Retrieves and returns all orders belonging to that specific user.</li>
 *     </ul>
 *   </li>
 *   <li>Implements {@link #getOrderById(Long, HttpSession)}:
 *     <ul>
 *       <li>Fetches an individual order by its unique primary key ID.</li>
 *       <li>Enforces security checks so users can only view their own orders unless they are administrators.</li>
 *     </ul>
 *   </li>
 *   <li>Implements {@link #deleteOrder(Long, HttpSession)}:
 *     <ul>
 *       <li>Validates order ownership or administrator privileges.</li>
 *       <li>Restores purchased quantity back to the product stock in inventory.</li>
 *       <li>Deletes the order record from the database.</li>
 *       <li>Wrapped in {@link Transactional} to ensure stock restoration and order deletion are atomic.</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@Service
public class OrderServiceImpl implements OrderService {

    /**
     * Repository bean for executing CRUD operations on the {@code orders} table.
     */
    private final OrderRepository orderRepository;

    /**
     * Repository bean for querying registered {@link User} records.
     */
    private final UserRepository userRepository;

    /**
     * Repository bean for checking inventory and updating {@link Product} records.
     */
    private final ProductRepository productRepository;

    /**
     * Constructs an OrderServiceImpl with the required repository dependencies.
     *
     * @param orderRepository   The {@link OrderRepository} for order persistence operations.
     * @param userRepository    The {@link UserRepository} for user lookups.
     * @param productRepository The {@link ProductRepository} for inventory management.
     */
    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * Places a new order for a specified product after validating session state and stock levels.
     *
     * <p>What's happening:
     * 1. Inspects the {@link HttpSession} for authenticated user email and role attributes.
     * 2. Throws an {@link AuthenticationRequiredException} if the user is unauthenticated.
     * 3. Throws an {@link UnauthorizedAccessException} if the user does not hold customer privileges.
     * 4. Validates positive purchase quantity.
     * 5. Retrieves the user and product records from the database.
     * 6. Checks inventory levels and decrements the purchased quantity.
     * 7. Creates, timestamps, and persists the new {@link Order} entity.
     *
     * <p>This method is {@link Transactional} to ensure that stock decrement and order creation
     * are committed atomically — if either operation fails, both are rolled back.
     *
     * @param productId   The primary key identifier of the product to purchase.
     * @param quantity    The count of items to purchase.
     * @param httpSession The active HTTP session holding authenticated user attributes.
     * @return The saved {@link Order} entity.
     * @throws AuthenticationRequiredException If user is not logged in.
     * @throws UnauthorizedAccessException     If user does not have customer role.
     * @throws ResourceNotFoundException       If user or product is not found.
     * @throws InsufficientStockException      If product stock is less than requested quantity.
     */
    @Override
    @Transactional
    public Order placeOrder(String productId, int quantity, HttpSession httpSession) {
        if (httpSession == null) {
            throw new AuthenticationRequiredException("Please login first");
        }

        // Retrieve user authentication credentials and role from session
        String userEmail = (String) httpSession.getAttribute("userEmail");
        String userRole = (String) httpSession.getAttribute("userRole");

        // Enforce user authentication
        if (userEmail == null) {
            throw new AuthenticationRequiredException("Please login first");
        }

        // Enforce role authorization (case-insensitive check for user/customer)
        if (userRole == null || (!userRole.equalsIgnoreCase("USER") && !userRole.equalsIgnoreCase("CUSTOMER"))) {
            throw new UnauthorizedAccessException("Only users can place orders");
        }

        // Validate requested quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // Retrieve user record
        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        // Retrieve product record
        Product targetProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if sufficient inventory exists
        if (targetProduct.getQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId
                    + " (requested: " + quantity + ", available: " + targetProduct.getQuantity() + ")");
        }

        // Construct new order record
        Order placedOrder = new Order();
        placedOrder.setUser(authenticatedUser);
        placedOrder.setProduct(targetProduct);
        placedOrder.setQuantity(quantity);
        placedOrder.setOrderDate(LocalDateTime.now());

        // Deduct ordered quantity from inventory and persist updated product
        targetProduct.setQuantity(targetProduct.getQuantity() - quantity);
        productRepository.save(targetProduct);

        // Persist and return the newly placed order
        return orderRepository.save(placedOrder);
    }

    /**
     * Retrieves all orders placed by the currently authenticated user.
     *
     * <p>What's happening:
     * 1. Inspects the active {@link HttpSession} to identify the authenticated user (via userId or email).
     * 2. Throws an {@link AuthenticationRequiredException} if no user session is established.
     * 3. Queries {@link OrderRepository#findByUserId(int)} to fetch only orders belonging to the user.
     *
     * @param httpSession The active HTTP session containing authenticated user attributes.
     * @return A list of orders belonging to the authenticated user.
     * @throws AuthenticationRequiredException If user is not authenticated.
     */
    @Override
    public List<Order> getUserOrders(HttpSession httpSession) {
        if (httpSession == null) {
            throw new AuthenticationRequiredException("Login required");
        }

        // Extract user identity from session attributes
        Object sessionUserId = httpSession.getAttribute("userId");
        String userEmail = (String) httpSession.getAttribute("userEmail");

        if (sessionUserId == null && userEmail == null) {
            throw new AuthenticationRequiredException("Login required");
        }

        int authenticatedUserId;
        if (sessionUserId instanceof Number) {
            authenticatedUserId = ((Number) sessionUserId).intValue();
        } else {
            User authenticatedUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
            authenticatedUserId = authenticatedUser.getId();
        }

        // Retrieve and return orders specific to this user
        return orderRepository.findByUserId(authenticatedUserId);
    }

    /**
     * Retrieves an order by its unique primary key ID with authorization verification.
     *
     * <p>What's happening:
     * 1. Validates that an active HTTP session is present.
     * 2. Finds the target order by ID or throws a {@link ResourceNotFoundException} if not found.
     * 3. Verifies that the requesting user owns the order or has administrator privileges.
     *
     * @param orderId     The primary key identifier of the order.
     * @param httpSession The active HTTP session containing user credentials.
     * @return The matching {@link Order} entity.
     * @throws AuthenticationRequiredException If user is not logged in.
     * @throws ResourceNotFoundException       If order is not found.
     * @throws UnauthorizedAccessException     If caller does not own the order and is not an admin.
     */
    @Override
    public Order getOrderById(Long orderId, HttpSession httpSession) {
        if (httpSession == null) {
            throw new AuthenticationRequiredException("Login required");
        }

        // Retrieve order by ID or throw exception if missing
        Order foundOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Determine user identity and role from session
        Object sessionUserId = httpSession.getAttribute("userId");
        String userEmail = (String) httpSession.getAttribute("userEmail");
        String userRole = (String) httpSession.getAttribute("userRole");

        if (sessionUserId == null && userEmail == null) {
            throw new AuthenticationRequiredException("Login required");
        }

        // Check if user is an administrator
        boolean isAdministrator = userRole != null && userRole.equalsIgnoreCase("admin");
        if (!isAdministrator) {
            int authenticatedUserId = (sessionUserId instanceof Number)
                    ? ((Number) sessionUserId).intValue()
                    : userRepository.findByEmail(userEmail).map(User::getId).orElse(-1);

            // Verify order ownership
            if (foundOrder.getUser() == null || !foundOrder.getUser().getId().equals(authenticatedUserId)) {
                throw new UnauthorizedAccessException("Unauthorized access to order");
            }
        }

        return foundOrder;
    }

    /**
     * Cancels an existing order by ID, restores product stock, and removes the order record.
     *
     * <p>What's happening:
     * 1. Validates that an active HTTP session is present.
     * 2. Finds the target order by ID or throws a {@link ResourceNotFoundException} if not found.
     * 3. Verifies that the authenticated user owns the order or has administrator privileges.
     * 4. Restores the ordered quantity back to the product inventory.
     * 5. Deletes the order record from the database.
     *
     * <p>This method is {@link Transactional} to ensure that stock restoration and order deletion
     * are committed atomically — if either operation fails, both are rolled back.
     *
     * @param orderId     The primary key identifier of the order to delete.
     * @param httpSession The active HTTP session containing user credentials.
     * @throws AuthenticationRequiredException If user is not logged in.
     * @throws ResourceNotFoundException       If order is not found.
     * @throws UnauthorizedAccessException     If caller does not own the order and is not an admin.
     */
    @Override
    @Transactional
    public void deleteOrder(Long orderId, HttpSession httpSession) {
        if (httpSession == null) {
            throw new AuthenticationRequiredException("Login required");
        }

        // Retrieve order by ID or throw exception if missing
        Order orderToDelete = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Determine user identity and role from session
        Object sessionUserId = httpSession.getAttribute("userId");
        String userEmail = (String) httpSession.getAttribute("userEmail");
        String userRole = (String) httpSession.getAttribute("userRole");

        if (sessionUserId == null && userEmail == null) {
            throw new AuthenticationRequiredException("Login required");
        }

        // Check if user is an administrator or owns the order
        boolean isAdministrator = userRole != null && userRole.equalsIgnoreCase("admin");
        if (!isAdministrator) {
            int authenticatedUserId = (sessionUserId instanceof Number)
                    ? ((Number) sessionUserId).intValue()
                    : userRepository.findByEmail(userEmail).map(User::getId).orElse(-1);

            if (orderToDelete.getUser() == null || !orderToDelete.getUser().getId().equals(authenticatedUserId)) {
                throw new UnauthorizedAccessException("Unauthorized access to order");
            }
        }

        // Restore product stock in inventory
        if (orderToDelete.getProduct() != null) {
            Product orderedProduct = orderToDelete.getProduct();
            orderedProduct.setQuantity(orderedProduct.getQuantity() + orderToDelete.getQuantity());
            productRepository.save(orderedProduct);
        }

        // Delete order record from database
        orderRepository.deleteById(orderId);
    }
}
