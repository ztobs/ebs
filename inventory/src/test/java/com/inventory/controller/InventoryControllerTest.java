package com.inventory.controller;

import com.inventory.model.Inventory;
import com.inventory.service.InventoryService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {
    private MockMvc mockMvc;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        // Create validator for parameter validation
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        
        mockMvc = MockMvcBuilders
                .standaloneSetup(inventoryController)
                .setControllerAdvice(new com.inventory.exception.GlobalExceptionHandler())
                .setValidator(validator)
                .build();
        
        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setEventId(100L);
        testInventory.setAvailableSeats(50);
    }

    @Test
    void getInventoryForEvent_ShouldReturnInventory() throws Exception {
        when(inventoryService.getInventoryForEvent(100L)).thenReturn(testInventory);
        
        mockMvc.perform(get("/api/inventory/event/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(100))
                .andExpect(jsonPath("$.availableSeats").value(50));
    }

    @Test
    void reserveSeats_ShouldReturnUpdatedInventory() throws Exception {
        when(inventoryService.updateAvailableSeats(100L, 2)).thenReturn(testInventory);
        
        mockMvc.perform(post("/api/inventory/event/100/reserve")
                .param("seats", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSeats").value(50));
    }

    @Test
    void cancelReservation_ShouldReturnUpdatedInventory() throws Exception {
        when(inventoryService.cancelReservation(100L, 2)).thenReturn(testInventory);
        
        mockMvc.perform(post("/api/inventory/event/100/cancel")
                .param("seats", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSeats").value(50));
    }

    @Test
    void reserveSeats_ShouldReturnBadRequest_WhenNegativeSeats() throws Exception {
        mockMvc.perform(post("/api/inventory/event/100/reserve")
                .param("seats", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelReservation_ShouldReturnBadRequest_WhenNegativeSeats() throws Exception {
        mockMvc.perform(post("/api/inventory/event/100/cancel")
                .param("seats", "-1"))
                .andExpect(status().isBadRequest());
    }
}