package com.notification.event;

public class BookingCancelledEvent extends BookingEvent {
    // Default constructor needed for Jackson deserialization
    public BookingCancelledEvent() {
        // Default constructor calls parent's default constructor
    }
    
    public BookingCancelledEvent(Long bookingId, Long userId, Long eventId, int numberOfTickets) {
        super(bookingId, userId, eventId, numberOfTickets);
    }
}