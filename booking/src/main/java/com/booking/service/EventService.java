package com.booking.service;

import com.booking.model.Event;
import java.util.List;

public interface EventService {
    Event createEvent(Event event);
    Event getEventById(Long id);
    List<Event> getAllEvents();
    Event updateEvent(Long id, Event eventDetails);
    void deleteEvent(Long id);
    List<Event> getAvailableEvents();
}