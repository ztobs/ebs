package com.booking.event;

import com.booking.model.Booking;

public class BookingCancelledEvent extends BookingEvent {
    public BookingCancelledEvent(Booking booking) {
        super(booking);
    }
}