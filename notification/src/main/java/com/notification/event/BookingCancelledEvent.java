package com.notification.event;

public class BookingCancelledEvent extends BookingEvent {
    public BookingCancelledEvent(Long bookingId, Long userId, Long eventId, int numberOfTickets) {
        super(bookingId, userId, eventId, numberOfTickets);
    }
}