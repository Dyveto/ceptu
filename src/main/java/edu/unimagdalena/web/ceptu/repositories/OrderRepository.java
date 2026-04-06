package edu.unimagdalena.web.ceptu.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.unimagdalena.web.ceptu.dto.MonthlyIncomeDTO;
import edu.unimagdalena.web.ceptu.dto.TopCustomerDTO;
import edu.unimagdalena.web.ceptu.entities.Order;
import edu.unimagdalena.web.ceptu.entities.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Buscar pedidos por cliente
    List<Order> findByCustomerId(UUID customerId);

    // Busca pedidos por filtros combinados (usa solo los que el usuario envie)
    @Query("""
            SELECT o FROM Order o
            WHERE (:customerId IS NULL OR o.customer.id = :customerId)
            AND (:status IS NULL OR o.status = :status)
            AND (:startDate IS NULL OR o.createdAt >= :startDate)
            AND (:endDate IS NULL OR o.createdAt <= :endDate)
            AND (:minTotal IS NULL OR o.total >= :minTotal)
            AND (:maxTotal IS NULL OR o.total <= :maxTotal)
            ORDER BY o.createdAt DESC
            """)
    Page<Order> findByFilters(
            @Param("customerId") UUID customerId,
            @Param("status") OrderStatus status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("minTotal") BigDecimal minTotal,
            @Param("maxTotal") BigDecimal maxTotal,
            Pageable pageable);

    // Ingresos mensuales agrupados
    @Query("""
            SELECT FUNCTION('YEAR', o.createdAt) AS year,
                   FUNCTION('MONTH', o.createdAt) AS month,
                   SUM(o.total) AS total
            FROM Order o
            WHERE o.status NOT IN ('CANCELLED')
            GROUP BY FUNCTION('YEAR', o.createdAt), FUNCTION('MONTH', o.createdAt)
            ORDER BY FUNCTION('YEAR', o.createdAt), FUNCTION('MONTH', o.createdAt)
            """)
    List<MonthlyIncomeDTO> findMonthlyIncome();

    // Clientes con mayor facturacion
    @Query("""
            SELECT o.customer AS customer, SUM(o.total) AS totalSpent
            FROM Order o
            WHERE o.status NOT IN ('CANCELLED')
            GROUP BY o.customer
            ORDER BY totalSpent DESC
            """)
    List<TopCustomerDTO> findTopCustomersByBilling(Pageable pageable);
}
