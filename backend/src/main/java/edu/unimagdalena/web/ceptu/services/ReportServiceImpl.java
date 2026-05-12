package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.BestSellingProductDTO;
import edu.unimagdalena.web.ceptu.dto.MonthlyIncomeDTO;
import edu.unimagdalena.web.ceptu.dto.TopCustomerDTO;
import edu.unimagdalena.web.ceptu.dto.response.ProductResponse;
import edu.unimagdalena.web.ceptu.mappers.ProductMapper;
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
    private final ProductMapper productMapper;

    @Override
    public List<BestSellingProductDTO> getBestSellingProducts(Instant start, Instant end, int limit) {
        return productRepository.findBestSellingProducts(start, end, PageRequest.of(0, limit));
    }

    @Override
    public List<MonthlyIncomeDTO> getMonthlyIncome() {
        return orderRepository.findMonthlyIncome();
    }

    @Override
    public List<TopCustomerDTO> getTopCustomers(int limit) {
        return orderRepository.findTopCustomersByBilling(PageRequest.of(0, limit));
    }

    @Override
    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findProductsWithLowStock()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
}