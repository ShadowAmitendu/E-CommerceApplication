package com.example.api.repository;

import com.example.api.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (Repository) for {@link Address} entities.
 */
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Retrieves all addresses for a specific user, with the default address listed first.
     *
     * @param userId The ID of the user.
     * @return List of addresses belonging to the user.
     */
    List<Address> findByUserIdOrderByIsDefaultDescIdAsc(Integer userId);

    /**
     * Retrieves all addresses for a user.
     *
     * @param userId The ID of the user.
     * @return List of addresses belonging to the user.
     */
    List<Address> findByUserId(Integer userId);

    /**
     * Finds the current default address for a specific user.
     *
     * @param userId The ID of the user.
     * @return An Optional containing the default address if one is set.
     */
    Optional<Address> findByUserIdAndIsDefaultTrue(Integer userId);

    /**
     * Finds an address by its ID and owning user's ID.
     *
     * @param id     The address ID.
     * @param userId The ID of the user who owns the address.
     * @return An Optional containing the address if found and owned by the user.
     */
    Optional<Address> findByIdAndUserId(Long id, Integer userId);

    /**
     * Counts how many addresses exist for a specific user.
     *
     * @param userId The ID of the user.
     * @return Count of addresses.
     */
    long countByUserId(Integer userId);

    /**
     * Resets the is_default flag to false for all addresses belonging to the user.
     *
     * @param userId The ID of the user.
     */
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void resetDefaultAddressesForUser(@Param("userId") Integer userId);
}
