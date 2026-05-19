package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.response.BestSellingProductResponse;
import edu.unimagdalena.web.ceptu.dto.response.LowStockProductResponse;
import edu.unimagdalena.web.ceptu.dto.response.MonthlyIncomeResponse;
import edu.unimagdalena.web.ceptu.dto.response.TopCustomerResponse;
import edu.unimagdalena.web.ceptu.repositories.OrderRepository;
import edu.unimagdalena.web.ceptu.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<BestSellingProductResponse> getBestSellingProducts(Instant start, Instant end, int limit) {
        // Compila perfectamente porque el repositorio ya retorna BestSellingProductResponse
        return productRepository.findBestSellingProducts(start, end, PageRequest.of(0, limit));
    }

    @Override
    public List<MonthlyIncomeResponse> getMonthlyIncome() {
        return orderRepository.findMonthlyIncome();
    }

    @Override
    public List<TopCustomerResponse> getTopCustomers(int limit) {
        return orderRepository.findTopCustomersByBilling(PageRequest.of(0, limit));
    }

    @Override
    public List<LowStockProductResponse> getLowStockProducts() {
        // Compila perfectamente porque el repositorio ya retorna LowStockProductResponse
        return productRepository.findProductsWithLowStock();
    }
}