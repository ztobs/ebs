package com.booking.repository;

import com.booking.model.Booking;
import com.booking.model.Event;
import com.booking.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void whenFindById_thenReturnBooking() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        entityManager.persist(user);

        Event event = new Event("Concert", "Music event", LocalDateTime.now(), "Venue", 100);
        entityManager.persist(event);

        Booking booking = new Booking(user, event, 2);
        entityManager.persist(booking);
        entityManager.flush();

        Booking found = bookingRepository.findById(booking.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(booking.getId(), found.getId());
        assertEquals(user.getId(), found.getUser().getId());
        assertEquals(event.getId(), found.getEvent().getId());
    }

    @Test
    void whenSaveBooking_thenPersistCorrectly() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        entityManager.persist(user);

        Event event = new Event("Concert", "Music event", LocalDateTime.now(), "Venue", 100);
        entityManager.persist(event);

        Booking booking = new Booking(user, event, 2);
        Booking saved = bookingRepository.save(booking);

        assertNotNull(saved.getId());
        assertEquals(2, saved.getNumberOfTickets());
        assertEquals(Booking.BookingStatus.CONFIRMED, saved.getStatus());
    }

    @Test
    void whenUpdateBooking_thenChangesPersisted() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        entityManager.persist(user);

        Event event = new Event("Concert", "Music event", LocalDateTime.now(), "Venue", 100);
        entityManager.persist(event);

        Booking booking = new Booking(user, event, 2);
        entityManager.persist(booking);
        entityManager.flush();

        booking.setNumberOfTickets(3);
        bookingRepository.save(booking);

        Booking updated = bookingRepository.findById(booking.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(3, updated.getNumberOfTickets());
    }
}