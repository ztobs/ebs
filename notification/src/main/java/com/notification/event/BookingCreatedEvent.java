package com.notification.event;

public class BookingCreatedEvent extends BookingEvent {
    public BookingCreatedEvent(Long bookingId, Long userId, Long eventId, int numberOfTickets) {
        super(bookingId, userId, eventId, numberOfTickets);
    }
}