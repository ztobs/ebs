package com.inventory.controller;

import com.inventory.model.Inventory;
import com.inventory.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<Inventory> getInventoryForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(inventoryService.getInventoryForEvent(eventId));
    }

    @PostMapping("/event/{eventId}/reserve")
    public ResponseEntity<?> reserveSeats(
            @PathVariable Long eventId,
            @RequestParam Integer seats) {
        
        if (seats <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status_code", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Validation failed");
            response.put("errors", Map.of("seats", "must be greater than 0"));
            response.put("timestamp", Instant.now().toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        return ResponseEntity.ok(inventoryService.updateAvailableSeats(eventId, seats));
    }

    @PostMapping("/event/{eventId}/cancel")
    public ResponseEntity<?> cancelReservation(
            @PathVariable Long eventId,
            @RequestParam Integer seats) {
        
        if (seats <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status_code", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Validation failed");
            response.put("errors", Map.of("seats", "must be greater than 0"));
            response.put("timestamp", Instant.now().toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        return ResponseEntity.ok(inventoryService.cancelReservation(eventId, seats));
    }
}