package edu.unimagdalena.web.ceptu.mappers;

import edu.unimagdalena.web.ceptu.dto.request.CreateCustomerRequest;
import edu.unimagdalena.web.ceptu.dto.response.CustomerResponse;
import edu.unimagdalena.web.ceptu.entities.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {java.time.Instant.class, edu.unimagdalena.web.ceptu.entities.enums.CustomerStatus.class})
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "status", expression = "java(CustomerStatus.ACTIVE)")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    Customer toEntity(CreateCustomerRequest request);

    CustomerResponse toResponse(Customer customer);
}