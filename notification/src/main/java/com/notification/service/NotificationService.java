package com.notification.service;

import com.notification.event.BookingCreatedEvent;
import com.notification.event.BookingCancelledEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import com.notification.repository.UserRepository;
import com.notification.repository.EventRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public NotificationService(JavaMailSender mailSender,
                             UserRepository userRepository,
                             EventRepository eventRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @KafkaListener(topics = "${notification.topics.booking-created}")
    public void handleBookingCreated(BookingCreatedEvent event) {
        var user = userRepository.findById(event.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        var eventDetails = eventRepository.findById(event.getEventId())
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        sendEmail(
            user.getEmail(),
            "Booking Confirmation",
            String.format(
                "Dear %s,\n\n" +
                "Your booking #%d for %d tickets has been confirmed.\n" +
                "Event: %s\n" +
                "Details: %s\n\n" +
                "Thank you for your booking!",
                user.getUsername(),
                event.getBookingId(),
                event.getNumberOfTickets(),
                eventDetails.getName(),
                eventDetails.getDescription()
            )
        );
    }

    @KafkaListener(topics = "${notification.topics.booking-cancelled}")
    public void handleBookingCancelled(BookingCancelledEvent event) {
        var user = userRepository.findById(event.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        var eventDetails = eventRepository.findById(event.getEventId())
            .orElseThrow(() -> new RuntimeException("Event not found"));

        sendEmail(
            user.getEmail(),
            "Booking Cancellation",
            String.format(
                "Dear %s,\n\n" +
                "Your booking #%d for %d tickets to %s has been cancelled.\n" +
                "%d tickets have been released.\n\n" +
                "We hope to see you again soon!",
                user.getUsername(),
                event.getBookingId(),
                event.getNumberOfTickets(),
                eventDetails.getName(),
                event.getNumberOfTickets()
            )
        );
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}