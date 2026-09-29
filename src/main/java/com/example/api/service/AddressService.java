package com.example.api.service;

import com.example.api.dto.AddressRequest;
import com.example.api.dto.AddressResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * Service interface for managing user shipping and billing addresses.
 */
public interface AddressService {

    /**
     * Adds a new address for the currently logged-in user.
     * If this is the user's first address, it is automatically marked as default.
     * If marked as default, any existing default address for this user is demoted.
     *
     * @param request The address creation payload.
     * @param session The active HTTP session.
     * @return The created address as an AddressResponse DTO.
     */
    AddressResponse addAddress(AddressRequest request, HttpSession session);

    /**
     * Retrieves all addresses for the logged-in user, sorted with the default address first.
     *
     * @param session The active HTTP session.
     * @return List of AddressResponse DTOs.
     */
    List<AddressResponse> getUserAddresses(HttpSession session);

    /**
     * Retrieves a single address by ID. Requires ownership or admin privileges.
     *
     * @param addressId The primary key ID of the address.
     * @param session   The active HTTP session.
     * @return The AddressResponse DTO.
     */
    AddressResponse getAddressById(Long addressId, HttpSession session);

    /**
     * Updates an existing address.
     *
     * @param addressId The primary key ID of the address.
     * @param request   The updated address fields.
     * @param session   The active HTTP session.
     * @return The updated AddressResponse DTO.
     */
    AddressResponse updateAddress(Long addressId, AddressRequest request, HttpSession session);

    /**
     * Sets a designated address as the default address for the logged-in user.
     * All other addresses belonging to the user are marked non-default.
     *
     * @param addressId The address ID to make default.
     * @param session   The active HTTP session.
     * @return The updated AddressResponse DTO.
     */
    AddressResponse setDefaultAddress(Long addressId, HttpSession session);

    /**
     * Deletes an address by ID.
     * If the deleted address was the default address and other addresses exist,
     * one of the remaining addresses is automatically promoted to default.
     *
     * @param addressId The address ID to delete.
     * @param session   The active HTTP session.
     */
    void deleteAddress(Long addressId, HttpSession session);
}
