package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.BestSellingProductDTO;
import edu.unimagdalena.web.ceptu.dto.MonthlyIncomeDTO;
import edu.unimagdalena.web.ceptu.dto.TopCustomerDTO;
import edu.unimagdalena.web.ceptu.dto.response.ProductResponse;
import edu.unimagdalena.web.ceptu.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/best-selling-products")
    public ResponseEntity<List<BestSellingProductDTO>> getBestSellingProducts(
            @RequestParam Instant startDate,
            @RequestParam Instant endDate,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reportService.getBestSellingProducts(startDate, endDate, limit));
    }

    @GetMapping("/monthly-income")
    public ResponseEntity<List<MonthlyIncomeDTO>> getMonthlyIncome() {
        return ResponseEntity.ok(reportService.getMonthlyIncome());
    }

    @GetMapping("/top-customers")
    public ResponseEntity<List<TopCustomerDTO>> getTopCustomers(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reportService.getTopCustomers(limit));
    }

    @GetMapping("/low-stock-products")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts() {
        return ResponseEntity.ok(reportService.getLowStockProducts());
    }
}