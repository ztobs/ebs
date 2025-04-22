package com.notification.event;

import java.time.LocalDateTime;

public abstract class BookingEvent {
    private Long bookingId;
    private Long userId;
    private Long eventId;
    private int numberOfTickets;
    private LocalDateTime timestamp;

    // Default constructor needed for Jackson deserialization
    protected BookingEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public BookingEvent(Long bookingId, Long userId, Long eventId, int numberOfTickets) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.eventId = eventId;
        this.numberOfTickets = numberOfTickets;
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
    
    // Setters needed for Jackson deserialization
    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}