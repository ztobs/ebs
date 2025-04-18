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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
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
    void getInventoryForEvent_ShouldReturnInventory_WhenExists() {
        when(inventoryRepository.findByEventId(100L)).thenReturn(Optional.of(testInventory));
        
        Inventory result = inventoryService.getInventoryForEvent(100L);
        
        assertEquals(testInventory, result);
        verify(inventoryRepository).findByEventId(100L);
    }

    @Test
    void getInventoryForEvent_ShouldThrow_WhenNotExists() {
        when(inventoryRepository.findByEventId(100L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> inventoryService.getInventoryForEvent(100L));
        verify(inventoryRepository).findByEventId(100L);
    }

    @Test
    void updateAvailableSeats_ShouldDecreaseSeats() {
        when(inventoryRepository.findByEventId(100L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);
        
        Inventory result = inventoryService.updateAvailableSeats(100L, 5);
        
        assertEquals(45, testInventory.getAvailableSeats());
        verify(inventoryRepository).save(testInventory);
    }

    @Test
    void updateAvailableSeats_ShouldThrow_WhenInsufficientSeats() {
        when(inventoryRepository.findByEventId(100L)).thenReturn(Optional.of(testInventory));
        
        assertThrows(RuntimeException.class, () -> inventoryService.updateAvailableSeats(100L, 55));
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void cancelReservation_ShouldIncreaseSeats() {
        when(inventoryRepository.findByEventId(100L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);
        
        Inventory result = inventoryService.cancelReservation(100L, 5);
        
        assertEquals(55, testInventory.getAvailableSeats());
        verify(inventoryRepository).save(testInventory);
    }

    @Test
    void cancelReservation_ShouldThrow_WhenNegativeSeats() {
        assertThrows(IllegalArgumentException.class, () -> inventoryService.cancelReservation(100L, -5));
        verify(inventoryRepository, never()).findByEventId(anyLong());
    }
}