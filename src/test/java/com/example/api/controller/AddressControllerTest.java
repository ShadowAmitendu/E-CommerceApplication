package com.example.api.controller;

import com.example.api.dto.AddressRequest;
import com.example.api.dto.AddressResponse;
import com.example.api.exception.GlobalExceptionHandler;
import com.example.api.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(addressController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        session = new MockHttpSession();
        session.setAttribute("userId", 1);
        session.setAttribute("userEmail", "test@example.com");
    }

    @Test
    void testAddAddress_ReturnsCreated() throws Exception {
        AddressResponse response = new AddressResponse(1L, 1, "Jane Doe", "9876543210", "123 Main St", "City", "State", "12345", "Country", true);

        when(addressService.addAddress(any(AddressRequest.class), any())).thenReturn(response);

        String json = "{\"fullName\":\"Jane Doe\",\"phone\":\"9876543210\",\"street\":\"123 Main St\",\"city\":\"City\",\"state\":\"State\",\"postalCode\":\"12345\",\"country\":\"Country\",\"isDefault\":true}";

        mockMvc.perform(post("/api/addresses")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.default").value(true));
    }

    @Test
    void testGetUserAddresses_ReturnsOk() throws Exception {
        AddressResponse response = new AddressResponse(1L, 1, "Jane Doe", "9876543210", "123 Main St", "City", "State", "12345", "Country", true);

        when(addressService.getUserAddresses(any())).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/addresses")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Jane Doe"));
    }

    @Test
    void testSetDefaultAddress_ReturnsOk() throws Exception {
        AddressResponse response = new AddressResponse(2L, 1, "Jane Doe", "9876543210", "456 Oak St", "City", "State", "12345", "Country", true);

        when(addressService.setDefaultAddress(eq(2L), any())).thenReturn(response);

        mockMvc.perform(patch("/api/addresses/2/default")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.default").value(true));
    }

    @Test
    void testDeleteAddress_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/addresses/1")
                        .session(session))
                .andExpect(status().isOk());

        verify(addressService).deleteAddress(eq(1L), any());
    }
}
