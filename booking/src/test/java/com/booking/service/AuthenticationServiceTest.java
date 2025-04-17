package com.booking.service;

import com.booking.model.User;
import com.booking.repository.UserRepository;
import com.booking.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void testAuthenticate() {
        when(authenticationManager.authenticate(any()))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new User());
        when(jwtUtils.generateToken(any())).thenReturn("test-token");

        String token = authenticationService.authenticate("testuser", "password");
        assertEquals("test-token", token);
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void testRegisterSuccess() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded-password");
        when(userRepository.save(any())).thenReturn(user);

        User registered = authenticationService.register(user);
        assertNotNull(registered);
        verify(userRepository).save(user);
    }

    @Test
    void testRegisterUsernameTaken() {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.existsByUsername(any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authenticationService.register(user));
    }

    @Test
    void testRegisterEmailTaken() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authenticationService.register(user));
    }
}