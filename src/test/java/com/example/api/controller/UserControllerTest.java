package com.example.api.controller;

import com.example.api.dto.UpdateProfileRequest;
import com.example.api.entity.User;
import com.example.api.exception.GlobalExceptionHandler;
import com.example.api.exception.UnauthorizedAccessException;
import com.example.api.service.UserService;
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

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        session = new MockHttpSession();
        session.setAttribute("userId", 1);
        session.setAttribute("userEmail", "test@example.com");
        session.setAttribute("userName", "Test User");
        session.setAttribute("userRole", "USER");
    }

    @Test
    void testUpdateProfile_ReturnsOk() throws Exception {
        User updated = new User();
        updated.setId(1);
        updated.setName("Updated Name");
        updated.setEmail("test@example.com");
        updated.setPhone("9999999999");
        updated.setRole("USER");

        when(userService.updateProfile(any(UpdateProfileRequest.class), any())).thenReturn(updated);

        String json = "{\"name\":\"Updated Name\",\"phone\":\"9999999999\"}";

        mockMvc.perform(put("/api/users/me")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.phone").value("9999999999"));
    }

    @Test
    void testGetAllUsers_Admin_ReturnsList() throws Exception {
        session.setAttribute("userRole", "ADMIN");

        User u1 = new User();
        u1.setId(1);
        u1.setName("Admin");
        User u2 = new User();
        u2.setId(2);
        u2.setName("Customer");

        when(userService.getAllUsers(any())).thenReturn(Arrays.asList(u1, u2));

        mockMvc.perform(get("/api/users")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Admin"))
                .andExpect(jsonPath("$[1].name").value("Customer"));
    }

    @Test
    void testGetAllUsers_NonAdmin_ReturnsForbidden() throws Exception {
        when(userService.getAllUsers(any())).thenThrow(new UnauthorizedAccessException("Admin access only"));

        mockMvc.perform(get("/api/users")
                        .session(session))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Admin access only"));
    }
}
