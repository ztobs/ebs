package com.booking.service;

import com.booking.model.Booking;
import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking);
    Booking getBookingById(Long id);
    List<Booking> getAllBookings();
    Booking updateBooking(Long id, Booking bookingDetails);
    Booking cancelBooking(Long id);
    List<Booking> getUserBookings(Long userId);
    List<Booking> getEventBookings(Long eventId);
}