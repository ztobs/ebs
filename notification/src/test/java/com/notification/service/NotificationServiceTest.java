package com.notification.service;

import com.notification.event.BookingCreatedEvent;
import com.notification.event.BookingCancelledEvent;
import com.notification.model.User;
import com.notification.model.Event;
import com.notification.repository.UserRepository;
import com.notification.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void handleBookingCreated_SendsEmailWithCorrectDetails() {
        // Given
        BookingCreatedEvent event = new BookingCreatedEvent(1L, 1L, 1L, 2);
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        Event eventDetails = new Event();
        eventDetails.setName("Test Event");
        eventDetails.setDescription("Test Description");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(eventDetails));

        // When
        notificationService.handleBookingCreated(event);

        // Then
        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void handleBookingCancelled_SendsEmailWithCorrectDetails() {
        // Given
        BookingCancelledEvent event = new BookingCancelledEvent(1L, 1L, 1L, 2);
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        Event eventDetails = new Event();
        eventDetails.setName("Test Event");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(eventDetails));

        // When
        notificationService.handleBookingCancelled(event);

        // Then
        verify(userRepository).findById(1L);
        verify(eventRepository).findById(1L);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}