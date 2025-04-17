package com.booking.service;

import com.booking.event.BookingCreatedEvent;
import com.booking.event.BookingCancelledEvent;
import com.booking.model.*;
import com.booking.repository.BookingRepository;
import com.booking.repository.EventRepository;
import com.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private BookingServiceImpl bookingService;
    
    @BeforeEach
    void setUp() {
        // Initialize service with mocks
        bookingService = new BookingServiceImpl(
            bookingRepository,
            eventRepository,
            userRepository,
            kafkaTemplate
        );
    }

    @Test
    void testCreateBookingSuccess() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        ReflectionTestUtils.setField(user, "id", 1L);
        
        Event event = new Event("Test", "Desc", LocalDateTime.now(), "Venue", 10);
        event.setAvailableSeats(10);
        ReflectionTestUtils.setField(event, "id", 1L);
        
        Booking booking = new Booking(user, event, 2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking result = bookingService.createBooking(booking);
        assertNotNull(result);
        verify(kafkaTemplate).send(eq("booking.created"), any(BookingCreatedEvent.class));
    }

    @Test
    void testCreateBookingNotEnoughSeats() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        ReflectionTestUtils.setField(user, "id", 1L);
        
        Event event = new Event("Test", "Desc", LocalDateTime.now(), "Venue", 1);
        event.setAvailableSeats(1);
        ReflectionTestUtils.setField(event, "id", 1L);
        
        Booking booking = new Booking(user, event, 2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(booking));
    }

    @Test
    void testCancelBookingSuccess() {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        
        Event event = new Event("Test", "Desc", LocalDateTime.now(), "Venue", 10);
        event.setAvailableSeats(8);
        ReflectionTestUtils.setField(event, "id", 1L);
        
        Booking booking = new Booking(user, event, 2);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        ReflectionTestUtils.setField(booking, "id", 1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking result = bookingService.cancelBooking(1L);
        assertEquals(Booking.BookingStatus.CANCELLED, result.getStatus());
        verify(kafkaTemplate).send(eq("booking.cancelled"), any(BookingCancelledEvent.class));
    }

    @Test
    void testCancelBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> bookingService.cancelBooking(1L));
    }
}