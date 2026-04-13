package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.BestSellingProductDTO;
import edu.unimagdalena.web.ceptu.entities.Product;
import edu.unimagdalena.web.ceptu.services.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest; // Import actualizado
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Import actualizado
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Anotación actualizada para Spring Boot 4
    private ReportService reportService;

    @Test
    void getBestSellingProducts_ShouldReturn200OkAndList() throws Exception {
        Product mockProduct = new Product();
        mockProduct.setName("Libro de Programación");

        BestSellingProductDTO dto = new BestSellingProductDTO() {
            @Override
            public Product getProduct() { return mockProduct; }
            @Override
            public Long getTotalSold() { return 150L; }
        };

        when(reportService.getBestSellingProducts(any(Instant.class), any(Instant.class), anyInt()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/reports/best-selling-products")
                        .param("startDate", "2026-01-01T00:00:00Z")
                        .param("endDate", "2026-12-31T23:59:59Z")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].totalSold").value(150));
    }
}