package com.example.api.controller;

import com.example.api.dto.AddressRequest;
import com.example.api.dto.AddressResponse;
import com.example.api.service.AddressService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing API endpoints for managing user addresses.
 *
 * <p>What is done:
 * <ul>
 *   <li>{@code POST /api/addresses} (201 Created): Adds a new address for the authenticated user.</li>
 *   <li>{@code GET /api/addresses} (200 OK): Lists all addresses for the authenticated user.</li>
 *   <li>{@code GET /api/addresses/{id}} (200 OK): Retrieves address details by ID.</li>
 *   <li>{@code PUT /api/addresses/{id}} (200 OK): Updates an existing address.</li>
 *   <li>{@code PATCH /api/addresses/{id}/default} (200 OK): Sets the address as default.</li>
 *   <li>{@code DELETE /api/addresses/{id}} (200 OK): Removes an address.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Adds a new address for the currently authenticated user.
     *
     * <p>Endpoint: {@code POST /api/addresses} (alias: {@code /add})
     * <p>Response: HTTP 201 Created with {@link AddressResponse}
     */
    @PostMapping({"", "/add"})
    public ResponseEntity<AddressResponse> addAddress(
            @Valid @RequestBody AddressRequest request,
            HttpSession session) {
        AddressResponse createdAddress = addressService.addAddress(request, session);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    /**
     * Retrieves all addresses for the currently authenticated user.
     *
     * <p>Endpoint: {@code GET /api/addresses} (alias: {@code /view})
     * <p>Response: HTTP 200 OK with list of {@link AddressResponse}
     */
    @GetMapping({"", "/view"})
    public ResponseEntity<List<AddressResponse>> getUserAddresses(HttpSession session) {
        return ResponseEntity.ok(addressService.getUserAddresses(session));
    }

    /**
     * Retrieves an address by its unique identifier.
     *
     * <p>Endpoint: {@code GET /api/addresses/{id}}
     * <p>Response: HTTP 200 OK with {@link AddressResponse}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddressById(
            @PathVariable("id") Long id,
            HttpSession session) {
        return ResponseEntity.ok(addressService.getAddressById(id, session));
    }

    /**
     * Updates an existing address.
     *
     * <p>Endpoint: {@code PUT /api/addresses/{id}} (alias: {@code /update/{id}})
     * <p>Response: HTTP 200 OK with {@link AddressResponse}
     */
    @PutMapping({"/{id}", "/update/{id}"})
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable("id") Long id,
            @Valid @RequestBody AddressRequest request,
            HttpSession session) {
        return ResponseEntity.ok(addressService.updateAddress(id, request, session));
    }

    /**
     * Sets the specified address as the default address.
     *
     * <p>Endpoint: {@code PATCH /api/addresses/{id}/default} (also supports {@code PUT})
     * <p>Response: HTTP 200 OK with updated {@link AddressResponse}
     */
    @RequestMapping(value = {"/{id}/default", "/default/{id}"}, method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @PathVariable("id") Long id,
            HttpSession session) {
        return ResponseEntity.ok(addressService.setDefaultAddress(id, session));
    }

    /**
     * Deletes an address by ID.
     *
     * <p>Endpoint: {@code DELETE /api/addresses/{id}} (alias: {@code /delete/{id}})
     * <p>Response: HTTP 200 OK with confirmation message
     */
    @DeleteMapping({"/{id}", "/delete/{id}"})
    public ResponseEntity<String> deleteAddress(
            @PathVariable("id") Long id,
            HttpSession session) {
        addressService.deleteAddress(id, session);
        return ResponseEntity.ok("Address deleted successfully!");
    }
}
