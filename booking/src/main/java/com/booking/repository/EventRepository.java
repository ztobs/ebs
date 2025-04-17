package com.booking.repository;

import com.booking.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // Custom query methods can be added here
    // For example:
    // List<Event> findByLocation(String location);
    // List<Event> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
}