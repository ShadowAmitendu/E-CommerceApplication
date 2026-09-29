package com.example.api.service;

import com.example.api.dto.UpdateProfileRequest;
import com.example.api.entity.User;
import com.example.api.exception.AuthenticationRequiredException;
import com.example.api.exception.InvalidCredentialsException;
import com.example.api.exception.UnauthorizedAccessException;
import com.example.api.repository.UserRepository;
import com.example.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private MockHttpSession session;
    private User existingUser;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("userId", 1);
        session.setAttribute("userEmail", "test@example.com");
        session.setAttribute("userName", "Test User");
        session.setAttribute("userRole", "USER");

        existingUser = new User();
        existingUser.setId(1);
        existingUser.setName("Test User");
        existingUser.setEmail("test@example.com");
        existingUser.setPhone("1234567890");
        existingUser.setPassword("OldPassword123");
        existingUser.setRole("USER");
    }

    @Test
    void testUpdateProfile_Success_NameAndPhone() {
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByPhone("9876543210")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateProfileRequest request = new UpdateProfileRequest("New Name", "9876543210", null, null);
        User result = userService.updateProfile(request, session);

        assertEquals("New Name", result.getName());
        assertEquals("9876543210", result.getPhone());
        assertEquals("New Name", session.getAttribute("userName"));
        verify(userRepository).save(existingUser);
    }

    @Test
    void testUpdateProfile_Success_PasswordChange() {
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateProfileRequest request = new UpdateProfileRequest(null, null, "OldPassword123", "NewPassword123");
        User result = userService.updateProfile(request, session);

        assertEquals("NewPassword123", result.getPassword());
        verify(userRepository).save(existingUser);
    }

    @Test
    void testUpdateProfile_WrongCurrentPassword_ThrowsException() {
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));

        UpdateProfileRequest request = new UpdateProfileRequest(null, null, "WrongPassword", "NewPassword123");
        assertThrows(InvalidCredentialsException.class, () -> userService.updateProfile(request, session));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateProfile_DuplicatePhone_ThrowsException() {
        User otherUser = new User();
        otherUser.setId(2);
        otherUser.setPhone("9876543210");

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByPhone("9876543210")).thenReturn(Optional.of(otherUser));

        UpdateProfileRequest request = new UpdateProfileRequest(null, "9876543210", null, null);
        assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(request, session));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetAllUsers_Admin_Success() {
        session.setAttribute("userRole", "ADMIN");
        User user2 = new User();
        user2.setId(2);
        user2.setName("Second User");

        when(userRepository.findAll()).thenReturn(Arrays.asList(existingUser, user2));

        List<User> users = userService.getAllUsers(session);
        assertEquals(2, users.size());
        verify(userRepository).findAll();
    }

    @Test
    void testGetAllUsers_NonAdmin_ThrowsUnauthorized() {
        session.setAttribute("userRole", "USER");
        assertThrows(UnauthorizedAccessException.class, () -> userService.getAllUsers(session));
        verify(userRepository, never()).findAll();
    }

    @Test
    void testGetUserById_Owner_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));

        User result = userService.getUserById(1, session);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void testGetUserById_OtherUser_ThrowsUnauthorized() {
        assertThrows(UnauthorizedAccessException.class, () -> userService.getUserById(2, session));
        verify(userRepository, never()).findById(2);
    }
}
