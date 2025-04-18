package com.inventory.listener;

import com.inventory.event.BookingCancelledEvent;
import com.inventory.event.BookingCreatedEvent;
import com.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!test") // Disable in test profile
public class BookingEventListener {
    private static final Logger logger = LoggerFactory.getLogger(BookingEventListener.class);
    private final InventoryService inventoryService;

    public BookingEventListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "${inventory.topics.booking-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleBookingCreatedEvent(BookingCreatedEvent event) {
        logger.info("Received booking created event: bookingId={}, eventId={}, tickets={}", 
                event.getBookingId(), event.getEventId(), event.getNumberOfTickets());
        
        try {
            // Reduce available seats when a booking is created
            // Pass negative number to reduce available seats
            inventoryService.updateAvailableSeats(event.getEventId(), -event.getNumberOfTickets());
            logger.info("Successfully updated inventory for event {} after booking creation", event.getEventId());
        } catch (Exception e) {
            logger.error("Failed to update inventory for event {} after booking creation: {}", 
                    event.getEventId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${inventory.topics.booking-cancelled}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleBookingCancelledEvent(BookingCancelledEvent event) {
        logger.info("Received booking cancelled event: bookingId={}, eventId={}, tickets={}",
                event.getBookingId(), event.getEventId(), event.getNumberOfTickets());
        
        try {
            // Release seats back to inventory when a booking is cancelled
            // Pass positive number to add seats back
            inventoryService.updateAvailableSeats(event.getEventId(), event.getNumberOfTickets());
            logger.info("Successfully updated inventory for event {} after booking cancellation", event.getEventId());
        } catch (Exception e) {
            logger.error("Failed to update inventory for event {} after booking cancellation: {}",
                    event.getEventId(), e.getMessage(), e);
        }
    }
}