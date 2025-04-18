package com.inventory.integration;

import com.inventory.config.TestConfig;
import com.inventory.event.BookingCancelledEvent;
import com.inventory.event.BookingCreatedEvent;
import com.inventory.model.Inventory;
import com.inventory.repository.InventoryRepository;
import com.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean; // Re-add SpyBean import
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.Commit; // Import Commit
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional; // Import Transactional
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import java.time.Duration; // Import Duration
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
 
import static org.awaitility.Awaitility.await; // Import Awaitility
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq; // Re-add eq import
import static org.mockito.Mockito.*; // Re-add Mockito import

@SpringBootTest
@Import(TestConfig.class)
@DirtiesContext
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"booking-created", "booking-cancelled"})
public class BookingEventIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(BookingEventIntegrationTest.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @SpyBean // Reinstate SpyBean
    private InventoryService inventoryService;
 
    @Autowired
    private InventoryRepository inventoryRepository;

    private static final String BOOKING_CREATED_TOPIC = "booking-created";
    private static final String BOOKING_CANCELLED_TOPIC = "booking-cancelled";
    private static final Long EVENT_ID = 200L;
    private static final int NUMBER_OF_TICKETS = 3;
    private static final int INITIAL_SEATS = 100;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test - cleaning up previous state");
        // Reset mocks before each test
        reset(inventoryService);
        
        // Clean up any existing inventory records for this event
        inventoryRepository.findByEventId(EVENT_ID).ifPresent(inventory -> {
            logger.info("Deleting existing inventory for event {}", EVENT_ID);
            inventoryRepository.delete(inventory);
        });
        
        // Create a fresh test inventory record
        Inventory inventory = new Inventory();
        inventory.setEventId(EVENT_ID);
        inventory.setAvailableSeats(INITIAL_SEATS);
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
        logger.info("Creating new inventory with {} seats for event {}", INITIAL_SEATS, EVENT_ID);
        inventoryRepository.save(inventory);
    }

    @Test
    void whenBookingCreatedEventReceived_thenInventoryIsUpdated() throws Exception {
        // Arrange
        BookingCreatedEvent event = new BookingCreatedEvent();
        event.setBookingId(1L);
        event.setUserId(1L);
        event.setEventId(EVENT_ID);
        event.setNumberOfTickets(NUMBER_OF_TICKETS);
        event.setTimestamp(LocalDateTime.now());

        // Act
        kafkaTemplate.send(BOOKING_CREATED_TOPIC, event);

        // Assert
        // Wait for the event to be processed
        // Verify service called with positive number to decrease seats
        verify(inventoryService, timeout(5000)).updateAvailableSeats(eq(EVENT_ID), eq(NUMBER_OF_TICKETS));
        
        // Add a delay to allow Kafka listener to process and DB to update
        TimeUnit.MILLISECONDS.sleep(500);
        
        // Verify the inventory was updated
        Optional<Inventory> updatedInventory = inventoryRepository.findByEventId(EVENT_ID);
        assertTrue(updatedInventory.isPresent());
        // The inventory service should have reduced the available seats by NUMBER_OF_TICKETS
        assertEquals(INITIAL_SEATS - NUMBER_OF_TICKETS, updatedInventory.get().getAvailableSeats());
    }

    @Test
    @Transactional // Ensure test runs in a transaction
    @Commit // Force commit for this test to see async listener changes
    void whenBookingCancelledEventReceived_thenSeatsAreReleased() throws Exception {
        // Arrange
        // First reduce the available seats
        Inventory inventory = inventoryRepository.findByEventId(EVENT_ID).orElseThrow();
        inventory.setAvailableSeats(INITIAL_SEATS - NUMBER_OF_TICKETS);
        inventoryRepository.save(inventory);
        logger.info("Initial inventory setup: {} seats", inventory.getAvailableSeats());

        BookingCancelledEvent event = new BookingCancelledEvent();
        event.setBookingId(1L);
        event.setUserId(1L);
        event.setEventId(EVENT_ID);
        event.setNumberOfTickets(NUMBER_OF_TICKETS);
        event.setTimestamp(LocalDateTime.now());

        // Act
        logger.info("Sending cancellation event for {} tickets", NUMBER_OF_TICKETS);
        kafkaTemplate.send(BOOKING_CANCELLED_TOPIC, event).get(); // Wait for send to complete

        // Assert
        // Use Awaitility to poll until the assertion passes or timeout occurs
        await().atMost(Duration.ofSeconds(30))
            .pollInterval(Duration.ofMillis(500))
            .untilAsserted(() -> {
                Optional<Inventory> updatedInventory = inventoryRepository.findByEventId(EVENT_ID);
                assertTrue(updatedInventory.isPresent(), "Inventory should exist for event ID: " + EVENT_ID);
                logger.info("Current inventory state: {} seats", updatedInventory.get().getAvailableSeats());
                // The inventory service should have added the NUMBER_OF_TICKETS back to available seats
                assertEquals(INITIAL_SEATS, updatedInventory.get().getAvailableSeats(),
                    "Inventory seats should be back to initial value after cancellation. Current: " +
                    updatedInventory.get().getAvailableSeats());
            });
    }

    @Test
    void whenEventForNonExistentInventory_thenHandleGracefully() throws Exception {
        // Arrange
        Long nonExistentEventId = 999L;
        BookingCreatedEvent event = new BookingCreatedEvent();
        event.setBookingId(1L);
        event.setUserId(1L);
        event.setEventId(nonExistentEventId);
        event.setNumberOfTickets(NUMBER_OF_TICKETS);
        event.setTimestamp(LocalDateTime.now());

        // Act
        kafkaTemplate.send(BOOKING_CREATED_TOPIC, event);

        // Assert
        // Verify service called with positive number (even though it will throw exception)
        verify(inventoryService, timeout(5000)).updateAvailableSeats(eq(nonExistentEventId), eq(NUMBER_OF_TICKETS));
        
        // Add a delay to allow Kafka listener to process (and handle exception internally)
        TimeUnit.MILLISECONDS.sleep(500);
        
        // Verify no exception was thrown OUTSIDE the consumer and the test completes
    }
}