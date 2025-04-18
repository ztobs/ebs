package com.inventory.event;

import java.time.LocalDateTime;

public class BookingCreatedEvent extends BookingEvent {
    // Default constructor for deserialization
    public BookingCreatedEvent() {
        super();
    }

    public BookingCreatedEvent(Long bookingId, Long userId, Long eventId, int numberOfTickets, LocalDateTime timestamp) {
        super(bookingId, userId, eventId, numberOfTickets, timestamp);
    }
}