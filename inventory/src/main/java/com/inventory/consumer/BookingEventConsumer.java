package com.inventory.consumer;

import com.inventory.event.BookingCreatedEvent;
import com.inventory.event.BookingCancelledEvent;
import com.inventory.model.Inventory;
import com.inventory.repository.InventoryRepository;
import com.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventConsumer {

    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;

    @KafkaListener(topics = "${inventory.topics.booking-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleBookingCreated(BookingCreatedEvent event) {
        log.info("Received booking created event: {}", event);
        try {
            // Decrease available seats by the number of tickets booked
            inventoryService.updateAvailableSeats(event.getEventId(), event.getNumberOfTickets());
        } catch (Exception e) {
            log.error("Error processing booking created event", e);
        }
    }

    @KafkaListener(topics = "${inventory.topics.booking-cancelled}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleBookingCancelled(BookingCancelledEvent event) {
        log.info("Received booking cancelled event: {}", event);
        try {
            // Use cancelReservation which handles releasing seats correctly
            inventoryService.cancelReservation(event.getEventId(), event.getNumberOfTickets());
        } catch (Exception e) {
            log.error("Error processing booking cancelled event", e);
        }
    }
}