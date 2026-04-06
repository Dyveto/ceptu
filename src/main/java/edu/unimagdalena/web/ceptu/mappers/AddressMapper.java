package edu.unimagdalena.web.ceptu.mappers;

import edu.unimagdalena.web.ceptu.dto.request.CreateAddressRequest;
import edu.unimagdalena.web.ceptu.dto.response.AddressResponse;
import edu.unimagdalena.web.ceptu.entities.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Address toEntity(CreateAddressRequest request);

    AddressResponse toResponse(Address address);
}