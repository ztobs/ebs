package com.inventory.event;

import java.time.LocalDateTime;

public class EventCreatedEvent {
    private Long eventId;
    private String name;
    private String description;
    private LocalDateTime dateTime;
    private String location;
    private int totalSeats;
    private int availableSeats;
    private LocalDateTime timestamp;

    // Default constructor for deserialization
    public EventCreatedEvent() {
    }

    public EventCreatedEvent(Long eventId, String name, String description, LocalDateTime dateTime,
                            String location, int totalSeats, int availableSeats, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "EventCreatedEvent{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dateTime=" + dateTime +
                ", location='" + location + '\'' +
                ", totalSeats=" + totalSeats +
                ", availableSeats=" + availableSeats +
                ", timestamp=" + timestamp +
                '}';
    }
}