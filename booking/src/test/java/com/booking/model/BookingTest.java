package com.booking.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void testBookingCreation() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        
        Event event = new Event("Concert", "Music event", LocalDateTime.now(), "Venue", 100);
        Booking booking = new Booking(user, event, 2);

        assertNotNull(booking);
        assertEquals(user, booking.getUser());
        assertEquals(event, booking.getEvent());
        assertEquals(2, booking.getNumberOfTickets());
        assertEquals(Booking.BookingStatus.CONFIRMED, booking.getStatus());
        assertNotNull(booking.getBookingTime());
    }

    @Test
    void testStatusUpdate() {
        User user = new User();
        user.setEmail("test@example.com");
        
        Event event = new Event();
        event.setName("Concert");
        event.setTotalSeats(100);
        event.setAvailableSeats(100);
        
        Booking booking = new Booking(user, event, 2);
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        
        assertEquals(Booking.BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    void testTicketCountUpdate() {
        User user = new User();
        Event event = new Event("Concert", "Music event", LocalDateTime.now(), "Venue", 100);
        Booking booking = new Booking(user, event, 2);

        booking.setNumberOfTickets(3);
        assertEquals(3, booking.getNumberOfTickets());
    }
}