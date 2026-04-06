package edu.unimagdalena.web.ceptu.services.impl;

import edu.unimagdalena.web.ceptu.dto.request.UpdateInventoryRequest;
import edu.unimagdalena.web.ceptu.dto.response.InventoryResponse;
import edu.unimagdalena.web.ceptu.entities.Inventory;
import edu.unimagdalena.web.ceptu.mappers.InventoryMapper;
import edu.unimagdalena.web.ceptu.repositories.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock 
    private InventoryRepository inventoryRepository;
    
    @Mock 
    private InventoryMapper inventoryMapper;
    
    @InjectMocks 
    private InventoryServiceImpl inventoryService;

    @Test
    void getInventoryByProductId_ShouldReturnInventoryResponse() {
        UUID productId = UUID.randomUUID();
        Inventory inventory = new Inventory();
        InventoryResponse expectedResponse = new InventoryResponse(UUID.randomUUID(), 100, 10, Instant.now());

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));
        when(inventoryMapper.toResponse(inventory)).thenReturn(expectedResponse);

        InventoryResponse result = inventoryService.getInventoryByProductId(productId);
        
        assertNotNull(result);
        assertEquals(100, result.availableStock());
    }

    @Test
    void updateInventory_ShouldUpdateAndReturnResponse() {
        UUID inventoryId = UUID.randomUUID();
        UpdateInventoryRequest request = new UpdateInventoryRequest(150, 20);
        Inventory existingInventory = new Inventory();
        Inventory savedInventory = new Inventory();
        InventoryResponse expectedResponse = new InventoryResponse(inventoryId, 150, 20, Instant.now());

        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(existingInventory)).thenReturn(savedInventory);
        when(inventoryMapper.toResponse(savedInventory)).thenReturn(expectedResponse);

        InventoryResponse result = inventoryService.updateInventory(inventoryId, request);
        
        assertNotNull(result);
        verify(inventoryMapper).updateEntityFromRequest(request, existingInventory);
        verify(inventoryRepository).save(existingInventory);
    }
}