package edu.unimagdalena.web.ceptu.mappers;

import edu.unimagdalena.web.ceptu.dto.request.CreateProductRequest;
import edu.unimagdalena.web.ceptu.dto.response.ProductResponse;
import edu.unimagdalena.web.ceptu.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, InventoryMapper.class}, imports = {java.time.Instant.class})
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    Product toEntity(CreateProductRequest request);

    ProductResponse toResponse(Product product);
}