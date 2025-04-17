package com.booking.controller;

import com.booking.model.Booking;
import com.booking.model.Event;
import com.booking.model.User;
import com.booking.service.BookingService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Event testEvent;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");

        testEvent = new Event("Test Event", "Description", LocalDateTime.now().plusDays(7), "Test Venue", 100);
        ReflectionTestUtils.setField(testEvent, "id", 1L);
        testEvent.setAvailableSeats(100);

        testBooking = new Booking(testUser, testEvent, 2);
        ReflectionTestUtils.setField(testBooking, "id", 1L);
    }

    // Create simplified DTOs for testing to avoid serialization issues
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BookingDto {
        private Long id;
        private int numberOfTickets;
        private String status;
        
        public BookingDto(Booking booking) {
            this.id = booking.getId();
            this.numberOfTickets = booking.getNumberOfTickets();
            this.status = booking.getStatus().name();
        }
        
        public Long getId() { return id; }
        public int getNumberOfTickets() { return numberOfTickets; }
        public String getStatus() { return status; }
    }
    
    @Test
    @WithMockUser
    void testCreateBooking() throws Exception {
        when(bookingService.createBooking(any(Booking.class))).thenReturn(testBooking);

        // Create a map with just the necessary booking data
        String bookingJson = "{ \"numberOfTickets\": 2, \"event\": { \"id\": 1 }, \"user\": { \"id\": 1 } }";

        mockMvc.perform(post("/api/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.numberOfTickets", is(2)));
    }

    @Test
    @WithMockUser
    void testGetBookingById() throws Exception {
        when(bookingService.getBookingById(1L)).thenReturn(testBooking);

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.numberOfTickets", is(2)));
    }

    @Test
    @WithMockUser
    void testGetAllBookings() throws Exception {
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].numberOfTickets", is(2)));
    }

    @Test
    @WithMockUser
    void testUpdateBooking() throws Exception {
        Booking updatedBooking = new Booking(testUser, testEvent, 3);
        ReflectionTestUtils.setField(updatedBooking, "id", 1L);

        when(bookingService.updateBooking(eq(1L), any(Booking.class))).thenReturn(updatedBooking);

        // Create a map with just the necessary booking data
        String bookingJson = "{ \"numberOfTickets\": 3 }";

        mockMvc.perform(put("/api/bookings/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.numberOfTickets", is(3)));
    }

    @Test
    @WithMockUser
    void testCancelBooking() throws Exception {
        Booking cancelledBooking = new Booking(testUser, testEvent, 2);
        ReflectionTestUtils.setField(cancelledBooking, "id", 1L);
        cancelledBooking.setStatus(Booking.BookingStatus.CANCELLED);

        when(bookingService.cancelBooking(1L)).thenReturn(cancelledBooking);

        mockMvc.perform(put("/api/bookings/1/cancel")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    @WithMockUser
    void testGetUserBookings() throws Exception {
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getUserBookings(1L)).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    @WithMockUser
    void testGetEventBookings() throws Exception {
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getEventBookings(1L)).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings/event/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }
}