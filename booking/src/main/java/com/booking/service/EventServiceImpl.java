package com.booking.service;

import com.booking.event.EventCreatedEvent;
import com.booking.model.Event;
import com.booking.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${booking.topics.event-created}")
    private String eventCreatedTopic;

    public EventServiceImpl(EventRepository eventRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.eventRepository = eventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Event createEvent(Event event) {
        Event savedEvent = eventRepository.save(event);
        
        // Publish event created event
        EventCreatedEvent createdEvent = new EventCreatedEvent(savedEvent);
        log.info("Publishing event created event: {}", createdEvent);
        log.info("Event details - ID: {}, Name: {}, Total Seats: {}, Available Seats: {}",
                createdEvent.getEventId(), createdEvent.getName(),
                createdEvent.getTotalSeats(), createdEvent.getAvailableSeats());
        
        kafkaTemplate.send(MessageBuilder
                .withPayload(createdEvent)
                .setHeader(KafkaHeaders.TOPIC, eventCreatedTopic)
                .setHeader("__TypeId__", "eventCreatedEvent")
                .build());
        
        return savedEvent;
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event updateEvent(Long id, Event eventDetails) {
        Event event = getEventById(id);
        event.setName(eventDetails.getName());
        event.setDescription(eventDetails.getDescription());
        event.setDateTime(eventDetails.getDateTime());
        event.setLocation(eventDetails.getLocation());
        event.setTotalSeats(eventDetails.getTotalSeats());
        event.setAvailableSeats(eventDetails.getAvailableSeats());
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        eventRepository.delete(event);
    }

    @Override
    public List<Event> getAvailableEvents() {
        return eventRepository.findAll().stream()
                .filter(event -> event.getAvailableSeats() > 0)
                .toList();
    }
}