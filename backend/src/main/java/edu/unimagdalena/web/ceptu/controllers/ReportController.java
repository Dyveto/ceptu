package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.response.BestSellingProductResponse;
import edu.unimagdalena.web.ceptu.dto.response.LowStockProductResponse;
import edu.unimagdalena.web.ceptu.dto.response.MonthlyIncomeResponse;
import edu.unimagdalena.web.ceptu.dto.response.TopCustomerResponse;
import edu.unimagdalena.web.ceptu.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/best-selling-products")
    public ResponseEntity<List<BestSellingProductResponse>> getBestSellingProducts(
            @RequestParam Instant startDate,
            @RequestParam Instant endDate,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reportService.getBestSellingProducts(startDate, endDate, limit));
    }

    @GetMapping("/monthly-income")
    public ResponseEntity<List<MonthlyIncomeResponse>> getMonthlyIncome() {
        return ResponseEntity.ok(reportService.getMonthlyIncome());
    }

    @GetMapping("/top-customers")
    public ResponseEntity<List<TopCustomerResponse>> getTopCustomers(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reportService.getTopCustomers(limit));
    }

    @GetMapping("/low-stock-products")
    public ResponseEntity<List<LowStockProductResponse>> getLowStockProducts() {
        return ResponseEntity.ok(reportService.getLowStockProducts());
    }
}