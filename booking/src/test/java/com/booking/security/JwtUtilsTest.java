package com.booking.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import({com.booking.config.TestConfig.class, com.booking.config.TestSecurityConfig.class})
class JwtUtilsTest {
    @Autowired
    private JwtUtils jwtUtils;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtils.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token, userDetails));
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtils.generateToken(userDetails);
        assertEquals(userDetails.getUsername(), jwtUtils.extractUsername(token));
    }

    @Test
    void testTokenExpiration() {
        String token = jwtUtils.generateToken(userDetails);
        assertFalse(jwtUtils.isTokenExpired(token));
    }
}