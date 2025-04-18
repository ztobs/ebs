package com.inventory.service;

import com.inventory.model.Inventory;
import com.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setEventId(100L);
        testInventory.setAvailableSeats(50);
    }

    @Test
    void getInventoryForEvent_Success() {
        when(inventoryRepository.findByEventId(100L)).thenReturn(Optional.of(testInventory));
        
        Inventory result = inventoryService.getInventoryForEvent(100L);
        
        assertEquals(testInventory, result);
        verify(inventoryRepository).findByEventId(100L);
    }

    @Test
    void updateAvailableSeats_Success() {
        when(inventoryRepository.findByEventId(100L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Inventory result = inventoryService.updateAvailableSeats(100L, 10);
        
        assertNotNull(result);
        assertEquals(40, result.getAvailableSeats());
        verify(inventoryRepository).save(testInventory);
    }

    @Test
    void cancelReservation_Success() {
        when(inventoryRepository.findByEventId(100L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Inventory result = inventoryService.cancelReservation(100L, 10);
        
        assertNotNull(result);
        assertEquals(60, result.getAvailableSeats());
        verify(inventoryRepository).save(testInventory);
    }

    @Test
    void updateAvailableSeats_InvalidSeatChange_ThrowsException() {
        // Test case 1: Seat change is zero
        assertThrows(IllegalArgumentException.class,
            () -> inventoryService.updateAvailableSeats(100L, 0),
            "Should throw exception for zero seat change");

        // Test case 2: Seat change results in negative available seats
        when(inventoryRepository.findByEventId(100L)).thenReturn(Optional.of(testInventory)); // Mock repo call
        assertThrows(IllegalArgumentException.class,
            () -> inventoryService.updateAvailableSeats(100L, 60), // 50 initial seats - 60 change = -10
            "Should throw exception when change results in negative seats");
    }

    @Test
    void cancelReservation_InvalidSeatsToRelease_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> inventoryService.cancelReservation(100L, 0));
            
        assertThrows(IllegalArgumentException.class,
            () -> inventoryService.cancelReservation(100L, -3));
    }
}