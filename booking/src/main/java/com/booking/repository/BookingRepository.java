package com.booking.repository;

import com.booking.model.Booking;
import com.booking.model.User;
import com.booking.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByEvent(Event event);
    List<Booking> findByStatus(Booking.BookingStatus status);
    List<Booking> findByUserAndStatus(User user, Booking.BookingStatus status);
}