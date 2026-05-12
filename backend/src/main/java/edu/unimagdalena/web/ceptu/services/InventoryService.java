package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.request.UpdateInventoryRequest;
import edu.unimagdalena.web.ceptu.dto.response.InventoryResponse;

import java.util.UUID;

public interface InventoryService {
    InventoryResponse getInventoryById(UUID id);
    
    InventoryResponse getInventoryByProductId(UUID productId); 
    
    InventoryResponse updateInventory(UUID id, UpdateInventoryRequest request);
}