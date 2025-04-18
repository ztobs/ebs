package com.inventory.service;

import com.inventory.model.Inventory;

public interface InventoryService {
    Inventory getInventoryForEvent(Long eventId);
    Inventory updateAvailableSeats(Long eventId, Integer seatChange);
    Inventory cancelReservation(Long eventId, Integer seatsToRelease);
}