package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.request.CreateCustomerRequest;
import edu.unimagdalena.web.ceptu.dto.response.CustomerResponse;
import edu.unimagdalena.web.ceptu.entities.enums.CustomerStatus;
import edu.unimagdalena.web.ceptu.security.jwt.JwtService;
import edu.unimagdalena.web.ceptu.services.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
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
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private CustomerService customerService;

    @Test
    @DisplayName("Debería crear un cliente exitosamente y retornar 201 Created")
    void createCustomer_WhenValidRequest_ShouldReturn201Created() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest("Laura", "Pérez", "laura@test.com", "3001234567");
        CustomerResponse expectedResponse = new CustomerResponse(
                UUID.randomUUID(), "Laura", "Pérez", "laura@test.com", "3001234567", CustomerStatus.ACTIVE, Instant.now()
        );

        when(customerService.createCustomer(any(CreateCustomerRequest.class))).thenReturn(expectedResponse);

        // 🏆 CORREGIDO: Ruta alineada al Hito 1 (/api/customers)
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("laura@test.com"))
                .andExpect(jsonPath("$.firstName").value("Laura"));
    }

    @Test
    @DisplayName("Debería rechazar la creación por formato de email inválido y retornar 400 Bad Request")
    void createCustomer_WhenInvalidEmail_ShouldReturn400BadRequest() throws Exception {
        CreateCustomerRequest invalidRequest = new CreateCustomerRequest("Laura", "Pérez", "correo-invalido", "3001234567");

        // 🏆 CORREGIDO: Ruta alineada al Hito 1
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}