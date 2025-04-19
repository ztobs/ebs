package com.booking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Schema(description = "Represents an event that can be booked")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the event", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Name of the event", example = "Tech Conference 2023")
    private String name;

    @Column(nullable = false)
    @Schema(description = "Detailed description of the event", example = "Annual technology conference featuring top speakers")
    private String description;

    @Column(nullable = false)
    @Schema(description = "Date and time when the event occurs", example = "2023-11-15T09:00:00")
    private LocalDateTime dateTime;

    @Column(nullable = false)
    @Schema(description = "Physical location of the event", example = "Convention Center, Hall A")
    private String location;

    @Column(nullable = false)
    @Schema(description = "Total number of seats available for booking", example = "500")
    private int totalSeats;

    @Column(nullable = false)
    @Schema(description = "Number of seats currently available", example = "250")
    private int availableSeats;

    // Constructors
    public Event() {}

    public Event(String name, String description, LocalDateTime dateTime, 
                String location, int totalSeats) {
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}