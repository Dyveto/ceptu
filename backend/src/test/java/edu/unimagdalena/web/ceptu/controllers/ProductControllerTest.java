package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.request.CreateProductRequest;
import edu.unimagdalena.web.ceptu.dto.request.UpdateInventoryRequest;
import edu.unimagdalena.web.ceptu.dto.response.InventoryResponse;
import edu.unimagdalena.web.ceptu.dto.response.ProductResponse;
import edu.unimagdalena.web.ceptu.security.jwt.JwtService;
import edu.unimagdalena.web.ceptu.services.InventoryService;
import edu.unimagdalena.web.ceptu.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private InventoryService inventoryService;

    @Test
    void createProduct_WhenValidRequest_ShouldReturn201Created() throws Exception {
        CreateProductRequest request = new CreateProductRequest(UUID.randomUUID(), "Sudadera", "SUD-123", new BigDecimal("85000.00"), 50, 10);
        ProductResponse expectedResponse = new ProductResponse(UUID.randomUUID(), "Sudadera", "SUD-123", new BigDecimal("85000.00"), true, null, null, Instant.now());

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))) // Usamos jsonMapper
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sudadera"));
    }

    @Test
    void createProduct_WhenPriceIsNegative_ShouldReturn400BadRequest() throws Exception {
        // Violamos la validación de precio negativo
        CreateProductRequest invalidRequest = new CreateProductRequest(UUID.randomUUID(), "Sudadera", "SUD-123", new BigDecimal("-10.00"), 50, 10);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalidRequest))) // Usamos jsonMapper
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProductInventory_ShouldReturn200Ok() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();
        UpdateInventoryRequest request = new UpdateInventoryRequest(100, 20);

        InventoryResponse currentInventory = new InventoryResponse(inventoryId, 50, 10, Instant.now());
        InventoryResponse updatedInventory = new InventoryResponse(inventoryId, 100, 20, Instant.now());

        when(inventoryService.getInventoryByProductId(productId)).thenReturn(currentInventory);
        when(inventoryService.updateInventory(inventoryId, request)).thenReturn(updatedInventory);

        mockMvc.perform(put("/api/v1/products/{id}/inventory", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))) // Usamos jsonMapper
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableStock").value(100));
    }
}