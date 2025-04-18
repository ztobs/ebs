package com.inventory.service;

import com.inventory.model.Inventory;
import com.inventory.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);
    
    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Inventory getInventoryForEvent(Long eventId) {
        Optional<Inventory> inventory = inventoryRepository.findByEventId(eventId);
        if (inventory.isEmpty()) {
            logger.error("Inventory not found for event: {}", eventId);
            throw new IllegalStateException("Inventory not found for event: " + eventId);
        }
        return inventory.get();
    }

    @Override
    public Inventory updateAvailableSeats(Long eventId, Integer seatChange) {
        if (seatChange == null) {
            throw new IllegalArgumentException("Seat change cannot be null");
        }
        if (seatChange == 0) {
            throw new IllegalArgumentException("Seat change cannot be zero");
        }
        
        // Try to find inventory, create a new one if not found
        Optional<Inventory> inventoryOpt = inventoryRepository.findByEventId(eventId);
        Inventory inventory;
        
        if (inventoryOpt.isEmpty()) {
            logger.info("Inventory not found for event: {}. Creating new inventory record.", eventId);
            inventory = new Inventory();
            inventory.setEventId(eventId);
            inventory.setAvailableSeats(0); // Start with 0 available seats
            inventory.setCreatedAt(LocalDateTime.now());
        } else {
            inventory = inventoryOpt.get();
        }
        
        int currentSeats = inventory.getAvailableSeats();
        int newSeats;
        
        if (seatChange > 0) {
            // Booking - subtract seats
            newSeats = currentSeats - seatChange;
        } else {
            // Cancellation - add back seats (seatChange is negative)
            newSeats = currentSeats + Math.abs(seatChange);
        }
        
        if (newSeats < 0) {
            logger.error("Invalid seat operation for event: {}. Current seats: {}, change: {}",
                eventId, currentSeats, seatChange);
            throw new IllegalArgumentException("Operation would result in negative seats for event: " + eventId);
        }
        
        logger.info("Updating seats for event {}: {} -> {} (change: {})",
            eventId, currentSeats, newSeats, seatChange);
        inventory.setAvailableSeats(newSeats);
        inventory.setUpdatedAt(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory cancelReservation(Long eventId, Integer seatsToRelease) {
        if (seatsToRelease <= 0) {
            throw new IllegalArgumentException("Seats to release must be positive");
        }
        logger.info("Releasing {} seats for event {}", seatsToRelease, eventId);
        return updateAvailableSeats(eventId, -seatsToRelease);
    }
}