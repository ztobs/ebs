package com.inventory.event;

import java.time.LocalDateTime;

public abstract class BookingEvent {
    private Long bookingId;
    private Long userId;
    private Long eventId;
    private int numberOfTickets;
    private LocalDateTime timestamp;

    // Default constructor for deserialization
    public BookingEvent() {
    }

    public BookingEvent(Long bookingId, Long userId, Long eventId, int numberOfTickets, LocalDateTime timestamp) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.eventId = eventId;
        this.numberOfTickets = numberOfTickets;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}