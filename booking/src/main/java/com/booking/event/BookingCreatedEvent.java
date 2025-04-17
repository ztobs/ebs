package com.booking.event;

import com.booking.model.Booking;

public class BookingCreatedEvent extends BookingEvent {
    public BookingCreatedEvent(Booking booking) {
        super(booking);
    }
}