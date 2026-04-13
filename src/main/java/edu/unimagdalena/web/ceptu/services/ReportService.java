package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.BestSellingProductDTO;
import edu.unimagdalena.web.ceptu.dto.MonthlyIncomeDTO;
import edu.unimagdalena.web.ceptu.dto.TopCustomerDTO;
import edu.unimagdalena.web.ceptu.dto.response.*;
import java.time.Instant;
import java.util.List;

public interface ReportService {
    List<BestSellingProductDTO> getBestSellingProducts(Instant start, Instant end, int limit);
    List<MonthlyIncomeDTO> getMonthlyIncome();
    List<TopCustomerDTO> getTopCustomers(int limit);
    List<ProductResponse> getLowStockProducts();
}