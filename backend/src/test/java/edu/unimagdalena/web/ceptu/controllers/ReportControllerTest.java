package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.response.BestSellingProductResponse;
import edu.unimagdalena.web.ceptu.security.jwt.JwtService;
import edu.unimagdalena.web.ceptu.services.ReportService;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Test
    @DisplayName("Debería retornar 200 OK y la lista de productos más vendidos usando el Record oficial")
    void getBestSellingProducts_ShouldReturn200OkAndList() throws Exception {
        BestSellingProductResponse mockResponse = new BestSellingProductResponse(
                UUID.randomUUID(),
                "Libro de Programación",
                "LIB-001",
                150L
        );

        when(reportService.getBestSellingProducts(any(Instant.class), any(Instant.class), anyInt()))
                .thenReturn(List.of(mockResponse));

        mockMvc.perform(get("/api/reports/best-selling-products")
                        .param("startDate", "2026-01-01T00:00:00Z")
                        .param("endDate", "2026-12-31T23:59:59Z")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].productName").value("Libro de Programación"))
                .andExpect(jsonPath("$[0].sku").value("LIB-001"))
                .andExpect(jsonPath("$[0].totalSold").value(150));
    }
}