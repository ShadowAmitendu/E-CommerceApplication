package com.example.api.repository;

import com.example.api.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (Repository) for {@link CartItem} entities.
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Retrieves all cart items belonging to a specific user.
     *
     * @param userId The ID of the user.
     * @return List of {@link CartItem} records for that user.
     */
    List<CartItem> findByUserId(Integer userId);

    /**
     * Finds an existing cart item for a specific user and product.
     *
     * @param userId    The ID of the user.
     * @param productId The ID of the product.
     * @return An {@link Optional} containing the {@link CartItem} if found.
     */
    Optional<CartItem> findByUserIdAndProductId(Integer userId, String productId);

    /**
     * Finds a specific cart item belonging to a user by cart item ID.
     *
     * @param id     The cart item primary key.
     * @param userId The ID of the user.
     * @return An {@link Optional} containing the {@link CartItem} if found.
     */
    Optional<CartItem> findByIdAndUserId(Long id, Integer userId);

    /**
     * Removes all cart items for a given user.
     *
     * @param userId The ID of the user.
     */
    void deleteByUserId(Integer userId);
}
