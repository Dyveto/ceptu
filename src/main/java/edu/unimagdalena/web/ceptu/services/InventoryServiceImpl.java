package edu.unimagdalena.web.ceptu.services.impl;

import edu.unimagdalena.web.ceptu.dto.request.UpdateInventoryRequest;
import edu.unimagdalena.web.ceptu.dto.response.InventoryResponse;
import edu.unimagdalena.web.ceptu.entities.Inventory;
import edu.unimagdalena.web.ceptu.mappers.InventoryMapper;
import edu.unimagdalena.web.ceptu.repositories.InventoryRepository;
import edu.unimagdalena.web.ceptu.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(UUID id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado con ID: " + id));
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(UUID productId) {
        // Asumiendo que has definido Optional<Inventory> findByProductId(UUID productId); en InventoryRepository
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para el Producto con ID: " + productId));
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(UUID id, UpdateInventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado con ID: " + id));

        // Validación de negocio adicional (opcional pero recomendada)
        if (request.minimumStock() > request.availableStock()) {
            // Podrías lanzar un warning o permitirlo dependiendo de tus reglas, 
            // pero normalmente el stock mínimo no debería ser un problema al actualizar, 
            // solo es un indicador para reabastecer.
        }

        // MapStruct aplica los cambios del request directamente a nuestro objeto "inventory"
        inventoryMapper.updateEntityFromRequest(request, inventory);

        // Guardamos y retornamos
        Inventory savedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(savedInventory);
    }
}