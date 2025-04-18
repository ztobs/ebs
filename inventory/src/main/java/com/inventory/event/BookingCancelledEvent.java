package com.inventory.event;

import java.time.LocalDateTime;

public class BookingCancelledEvent extends BookingEvent {
    // Default constructor for deserialization
    public BookingCancelledEvent() {
        super();
    }

    public BookingCancelledEvent(Long bookingId, Long userId, Long eventId, int numberOfTickets, LocalDateTime timestamp) {
        super(bookingId, userId, eventId, numberOfTickets, timestamp);
    }
}