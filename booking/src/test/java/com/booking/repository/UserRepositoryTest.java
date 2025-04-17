package com.booking.repository;

import com.booking.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenFindByUsername_thenReturnUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = userRepository.findByUsername(user.getUsername());
        assertTrue(found.isPresent());
        assertEquals(user.getUsername(), found.get().getUsername());
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = userRepository.findByEmail(user.getEmail());
        assertTrue(found.isPresent());
        assertEquals(user.getEmail(), found.get().getEmail());
    }

    @Test
    void whenExistsByUsername_thenReturnBoolean() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        assertTrue(userRepository.existsByUsername(user.getUsername()));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void whenExistsByEmail_thenReturnBoolean() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        assertTrue(userRepository.existsByEmail(user.getEmail()));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }
}