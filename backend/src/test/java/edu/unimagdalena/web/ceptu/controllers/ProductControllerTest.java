package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.request.CreateProductRequest;
import edu.unimagdalena.web.ceptu.dto.request.UpdateInventoryRequest;
import edu.unimagdalena.web.ceptu.dto.response.InventoryResponse;
import edu.unimagdalena.web.ceptu.dto.response.ProductResponse;
import edu.unimagdalena.web.ceptu.security.jwt.JwtService;
import edu.unimagdalena.web.ceptu.services.InventoryService;
import edu.unimagdalena.web.ceptu.services.ProductService;
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
    @DisplayName("Debería registrar un producto en el catálogo y retornar 201 Created")
    void createProduct_WhenValidRequest_ShouldReturn201Created() throws Exception {
        CreateProductRequest request = new CreateProductRequest(UUID.randomUUID(), "Sudadera", "SUD-123", new BigDecimal("85000.00"), 50, 10);
        ProductResponse expectedResponse = new ProductResponse(UUID.randomUUID(), "Sudadera", "SUD-123", new BigDecimal("85000.00"), true, null, null, Instant.now());

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(expectedResponse);

        // 🏆 CORREGIDO: Ruta sin /v1 (/api/products)
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sudadera"));
    }

    @Test
    @DisplayName("Debería interceptar un precio negativo y retornar 400 Bad Request")
    void createProduct_WhenPriceIsNegative_ShouldReturn400BadRequest() throws Exception {
        CreateProductRequest invalidRequest = new CreateProductRequest(UUID.randomUUID(), "Sudadera", "SUD-123", new BigDecimal("-10.00"), 50, 10);

        // 🏆 CORREGIDO: Ruta sin /v1
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debería actualizar de forma aislada las existencias físicas y retornar 200 Ok (HU-03)")
    void updateProductInventory_ShouldReturn200Ok() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();
        UpdateInventoryRequest request = new UpdateInventoryRequest(100, 20);

        InventoryResponse currentInventory = new InventoryResponse(inventoryId, 50, 10, Instant.now());
        InventoryResponse updatedInventory = new InventoryResponse(inventoryId, 100, 20, Instant.now());

        when(inventoryService.getInventoryByProductId(productId)).thenReturn(currentInventory);
        when(inventoryService.updateInventory(inventoryId, request)).thenReturn(updatedInventory);

        // 🏆 CORREGIDO: Endpoint del motor de stock sin /v1
        mockMvc.perform(put("/api/products/{id}/inventory", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableStock").value(100));
    }
}