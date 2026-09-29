package com.example.api.service;

import com.example.api.dto.AddressRequest;
import com.example.api.dto.AddressResponse;
import com.example.api.entity.Address;
import com.example.api.entity.User;
import com.example.api.exception.UnauthorizedAccessException;
import com.example.api.repository.AddressRepository;
import com.example.api.repository.UserRepository;
import com.example.api.service.impl.AddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private MockHttpSession session;
    private User testUser;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("userId", 1);
        session.setAttribute("userEmail", "test@example.com");
        session.setAttribute("userName", "Test User");
        session.setAttribute("userRole", "USER");

        testUser = new User();
        testUser.setId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
    }

    @Test
    void testAddAddress_FirstAddress_AutomaticallyDefault() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(addressRepository.countByUserId(1)).thenReturn(0L);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address a = invocation.getArgument(0);
            a.setId(100L);
            return a;
        });

        AddressRequest request = new AddressRequest("Jane Doe", "9876543210", "123 Main St", "Springfield", "IL", "62701", "USA", false);
        AddressResponse response = addressService.addAddress(request, session);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertTrue(response.isDefault(), "First address must automatically be marked as default");
        verify(addressRepository, never()).resetDefaultAddressesForUser(any());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testAddAddress_SecondAddress_DefaultTrue_ResetsExistingDefault() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(addressRepository.countByUserId(1)).thenReturn(1L);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address a = invocation.getArgument(0);
            a.setId(101L);
            return a;
        });

        AddressRequest request = new AddressRequest("Jane Doe", "9876543210", "456 Oak St", "Springfield", "IL", "62701", "USA", true);
        AddressResponse response = addressService.addAddress(request, session);

        assertNotNull(response);
        assertEquals(101L, response.getId());
        assertTrue(response.isDefault());
        verify(addressRepository).resetDefaultAddressesForUser(1);
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testSetDefaultAddress_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        Address address2 = new Address(testUser, "Jane Doe", "9876543210", "456 Oak St", "City", "State", "12345", "USA", false);
        address2.setId(200L);

        when(addressRepository.findById(200L)).thenReturn(Optional.of(address2));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse response = addressService.setDefaultAddress(200L, session);

        assertTrue(response.isDefault());
        verify(addressRepository).resetDefaultAddressesForUser(1);
        verify(addressRepository).save(address2);
    }

    @Test
    void testDeleteAddress_DefaultDeleted_PromotesRemainingAddress() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        Address defaultAddress = new Address(testUser, "Jane Doe", "9876543210", "123 Main St", "City", "State", "12345", "USA", true);
        defaultAddress.setId(100L);

        Address remainingAddress = new Address(testUser, "Jane Doe", "9876543210", "456 Oak St", "City", "State", "12345", "USA", false);
        remainingAddress.setId(101L);

        when(addressRepository.findById(100L)).thenReturn(Optional.of(defaultAddress));
        when(addressRepository.findByUserId(1)).thenReturn(new ArrayList<>(Collections.singletonList(remainingAddress)));

        addressService.deleteAddress(100L, session);

        verify(addressRepository).delete(defaultAddress);
        assertTrue(remainingAddress.isDefault(), "Remaining address should be promoted to default");
        verify(addressRepository).save(remainingAddress);
    }

    @Test
    void testGetAddressById_OtherUser_ThrowsUnauthorized() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        User otherUser = new User();
        otherUser.setId(2);
        Address otherAddress = new Address(otherUser, "Other", "1112223333", "789 Pine St", "City", "State", "12345", "USA", true);
        otherAddress.setId(300L);

        when(addressRepository.findById(300L)).thenReturn(Optional.of(otherAddress));

        assertThrows(UnauthorizedAccessException.class, () -> addressService.getAddressById(300L, session));
    }
}
