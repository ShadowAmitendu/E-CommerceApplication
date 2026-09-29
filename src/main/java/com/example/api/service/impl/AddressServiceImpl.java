package com.example.api.service.impl;

import com.example.api.dto.AddressRequest;
import com.example.api.dto.AddressResponse;
import com.example.api.entity.Address;
import com.example.api.entity.User;
import com.example.api.exception.AuthenticationRequiredException;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.exception.UnauthorizedAccessException;
import com.example.api.repository.AddressRepository;
import com.example.api.repository.UserRepository;
import com.example.api.service.AddressService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation providing address management and default address enforcement.
 */
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds a new address for the currently logged-in user.
     * Enforces default address exclusivity: exactly one address is default.
     */
    @Override
    @Transactional
    public AddressResponse addAddress(AddressRequest request, HttpSession session) {
        User user = getAuthenticatedUser(session);

        long count = addressRepository.countByUserId(user.getId());
        boolean makeDefault = (count == 0) || Boolean.TRUE.equals(request.getIsDefault());

        if (makeDefault && count > 0) {
            addressRepository.resetDefaultAddressesForUser(user.getId());
        }

        Address address = new Address();
        address.setUser(user);
        address.setFullName(request.getFullName().trim());
        address.setPhone(request.getPhone().trim());
        address.setStreet(request.getStreet().trim());
        address.setCity(request.getCity().trim());
        address.setState(request.getState().trim());
        address.setPostalCode(request.getPostalCode().trim());
        address.setCountry(request.getCountry().trim());
        address.setDefault(makeDefault);

        Address savedAddress = addressRepository.save(address);
        return mapToResponse(savedAddress);
    }

    /**
     * Retrieves all addresses for the authenticated user, sorted with the default address first.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(HttpSession session) {
        User user = getAuthenticatedUser(session);
        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDescIdAsc(user.getId());
        return addresses.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Retrieves a single address by ID, verifying ownership or administrator privileges.
     */
    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long addressId, HttpSession session) {
        User user = getAuthenticatedUser(session);
        boolean admin = isAdmin(session);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (!admin && (address.getUser() == null || !address.getUser().getId().equals(user.getId()))) {
            throw new UnauthorizedAccessException("Unauthorized access to address");
        }

        return mapToResponse(address);
    }

    /**
     * Updates an existing address.
     */
    @Override
    @Transactional
    public AddressResponse updateAddress(Long addressId, AddressRequest request, HttpSession session) {
        User user = getAuthenticatedUser(session);
        boolean admin = isAdmin(session);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (!admin && (address.getUser() == null || !address.getUser().getId().equals(user.getId()))) {
            throw new UnauthorizedAccessException("Unauthorized access to address");
        }

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            address.setFullName(request.getFullName().trim());
        }
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            address.setPhone(request.getPhone().trim());
        }
        if (request.getStreet() != null && !request.getStreet().trim().isEmpty()) {
            address.setStreet(request.getStreet().trim());
        }
        if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
            address.setCity(request.getCity().trim());
        }
        if (request.getState() != null && !request.getState().trim().isEmpty()) {
            address.setState(request.getState().trim());
        }
        if (request.getPostalCode() != null && !request.getPostalCode().trim().isEmpty()) {
            address.setPostalCode(request.getPostalCode().trim());
        }
        if (request.getCountry() != null && !request.getCountry().trim().isEmpty()) {
            address.setCountry(request.getCountry().trim());
        }

        if (Boolean.TRUE.equals(request.getIsDefault()) && !address.isDefault()) {
            addressRepository.resetDefaultAddressesForUser(address.getUser().getId());
            address.setDefault(true);
        }

        Address savedAddress = addressRepository.save(address);
        return mapToResponse(savedAddress);
    }

    /**
     * Explicitly sets the designated address as the default address for the user.
     */
    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long addressId, HttpSession session) {
        User user = getAuthenticatedUser(session);
        boolean admin = isAdmin(session);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (!admin && (address.getUser() == null || !address.getUser().getId().equals(user.getId()))) {
            throw new UnauthorizedAccessException("Unauthorized access to address");
        }

        Integer targetUserId = address.getUser().getId();
        addressRepository.resetDefaultAddressesForUser(targetUserId);

        address.setDefault(true);
        Address savedAddress = addressRepository.save(address);
        return mapToResponse(savedAddress);
    }

    /**
     * Deletes an address. If the deleted address was default, promotes another address to default if available.
     */
    @Override
    @Transactional
    public void deleteAddress(Long addressId, HttpSession session) {
        User user = getAuthenticatedUser(session);
        boolean admin = isAdmin(session);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (!admin && (address.getUser() == null || !address.getUser().getId().equals(user.getId()))) {
            throw new UnauthorizedAccessException("Unauthorized access to address");
        }

        Integer targetUserId = address.getUser().getId();
        boolean wasDefault = address.isDefault();

        addressRepository.delete(address);
        addressRepository.flush();

        if (wasDefault) {
            List<Address> remaining = addressRepository.findByUserId(targetUserId);
            if (!remaining.isEmpty()) {
                Address newDefault = remaining.get(0);
                newDefault.setDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }

    private User getAuthenticatedUser(HttpSession httpSession) {
        if (httpSession == null) {
            throw new AuthenticationRequiredException("Login required");
        }
        Object sessionUserId = httpSession.getAttribute("userId");
        String userEmail = (String) httpSession.getAttribute("userEmail");

        if (sessionUserId == null && userEmail == null) {
            throw new AuthenticationRequiredException("Login required");
        }

        if (sessionUserId instanceof Number) {
            return userRepository.findById(((Number) sessionUserId).intValue())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + sessionUserId));
        }

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
    }

    private boolean isAdmin(HttpSession httpSession) {
        if (httpSession == null) {
            return false;
        }
        String userRole = (String) httpSession.getAttribute("userRole");
        return userRole != null && userRole.equalsIgnoreCase("admin");
    }

    private AddressResponse mapToResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getUser() != null ? address.getUser().getId() : null,
                address.getFullName(),
                address.getPhone(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.isDefault()
        );
    }
}
