package com.booking.event;

import com.booking.model.Booking;
import java.time.LocalDateTime;

public abstract class BookingEvent {
    private Long bookingId;
    private Long userId;
    private Long eventId;
    private int numberOfTickets;
    private LocalDateTime timestamp;

    public BookingEvent(Booking booking) {
        this.bookingId = booking.getId();
        this.userId = booking.getUser().getId();
        this.eventId = booking.getEvent().getId();
        this.numberOfTickets = booking.getNumberOfTickets();
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public Long getBookingId() {
        return bookingId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}