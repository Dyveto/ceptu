package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.response.BestSellingProductResponse;
import edu.unimagdalena.web.ceptu.dto.response.LowStockProductResponse;
import edu.unimagdalena.web.ceptu.dto.response.MonthlyIncomeResponse;
import edu.unimagdalena.web.ceptu.dto.response.TopCustomerResponse;

import java.time.Instant;
import java.util.List;

public interface ReportService {

    List<BestSellingProductResponse> getBestSellingProducts(Instant startDate, Instant endDate, int limit);
    List<MonthlyIncomeResponse> getMonthlyIncome();
    List<TopCustomerResponse> getTopCustomers(int limit);
    List<LowStockProductResponse> getLowStockProducts();
}