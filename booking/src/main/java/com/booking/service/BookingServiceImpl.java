package com.booking.service;

import com.booking.event.*;
import com.booking.model.*;
import com.booking.repository.BookingRepository;
import com.booking.repository.EventRepository;
import com.booking.repository.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BookingServiceImpl(BookingRepository bookingRepository,
                            EventRepository eventRepository,
                            UserRepository userRepository,
                            KafkaTemplate<String, Object> kafkaTemplate) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Booking createBooking(Booking booking) {
        Event event = eventRepository.findById(booking.getEvent().getId())
                .orElseThrow(() -> new NoSuchElementException("Event not found"));
        User user = userRepository.findById(booking.getUser().getId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (event.getAvailableSeats() < booking.getNumberOfTickets()) {
            throw new IllegalArgumentException("Not enough available seats");
        }

        event.setAvailableSeats(event.getAvailableSeats() - booking.getNumberOfTickets());
        eventRepository.save(event);

        booking.setUser(user);
        booking.setEvent(event);
        Booking savedBooking = bookingRepository.save(booking);
        
        // Publish booking created event
        kafkaTemplate.send("booking.created", new BookingCreatedEvent(savedBooking));
        
        return savedBooking;
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Booking not found with id: " + id));
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking updateBooking(Long id, Booking bookingDetails) {
        Booking booking = getBookingById(id);
        booking.setNumberOfTickets(bookingDetails.getNumberOfTickets());
        return bookingRepository.save(booking);
    }

    @Override
    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            return booking;
        }

        Event event = booking.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + booking.getNumberOfTickets());
        eventRepository.save(event);

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);
        
        // Publish booking cancelled event
        kafkaTemplate.send("booking.cancelled", new BookingCancelledEvent(cancelledBooking));
        
        return cancelledBooking;
    }

    @Override
    public List<Booking> getUserBookings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        return bookingRepository.findByUser(user);
    }

    @Override
    public List<Booking> getEventBookings(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + eventId));
        return bookingRepository.findByEvent(event);
    }
}