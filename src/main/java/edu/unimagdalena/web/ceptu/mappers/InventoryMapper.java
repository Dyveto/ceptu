package edu.unimagdalena.web.ceptu.mappers;

import edu.unimagdalena.web.ceptu.dto.request.UpdateInventoryRequest;
import edu.unimagdalena.web.ceptu.dto.response.InventoryResponse;
import edu.unimagdalena.web.ceptu.entities.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", imports = {java.time.Instant.class})
public interface InventoryMapper {

    InventoryResponse toResponse(Inventory inventory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    void updateEntityFromRequest(UpdateInventoryRequest request, @MappingTarget Inventory inventory);
}