package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.request.CreateCustomerRequest;
import edu.unimagdalena.web.ceptu.dto.response.CustomerResponse;
import edu.unimagdalena.web.ceptu.entities.enums.CustomerStatus;
import edu.unimagdalena.web.ceptu.services.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper; // Reemplaza al viejo ObjectMapper

    @MockitoBean
    private CustomerService customerService; // Reemplaza al viejo @MockBean

    @Test
    void createCustomer_WhenValidRequest_ShouldReturn201Created() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest("Laura", "Pérez", "laura@test.com", "3001234567");
        CustomerResponse expectedResponse = new CustomerResponse(
                UUID.randomUUID(), "Laura", "Pérez", "laura@test.com", "3001234567", CustomerStatus.ACTIVE, Instant.now()
        );

        when(customerService.createCustomer(any(CreateCustomerRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))) // Usamos jsonMapper
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("laura@test.com"))
                .andExpect(jsonPath("$.firstName").value("Laura"));
    }

    @Test
    void createCustomer_WhenInvalidEmail_ShouldReturn400BadRequest() throws Exception {
        CreateCustomerRequest invalidRequest = new CreateCustomerRequest("Laura", "Pérez", "correo-invalido", "3001234567");

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}