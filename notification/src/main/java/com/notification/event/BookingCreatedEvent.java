package com.notification.event;

public class BookingCreatedEvent extends BookingEvent {
    // Default constructor needed for Jackson deserialization
    public BookingCreatedEvent() {
        // Default constructor calls parent's default constructor
    }
    
    public BookingCreatedEvent(Long bookingId, Long userId, Long eventId, int numberOfTickets) {
        super(bookingId, userId, eventId, numberOfTickets);
    }
}