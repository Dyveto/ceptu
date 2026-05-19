package edu.unimagdalena.web.ceptu.repositories;

import edu.unimagdalena.web.ceptu.dto.response.MonthlyIncomeResponse;
import edu.unimagdalena.web.ceptu.dto.response.TopCustomerResponse;
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

    List<Order> findByCustomerId(UUID customerId);

    @Query("""
            SELECT o FROM Order o
            WHERE o.customer.id = COALESCE(:customerId, o.customer.id)
              AND o.status = COALESCE(:status, o.status)
              AND o.createdAt >= COALESCE(:startDate, o.createdAt)
              AND o.createdAt <= COALESCE(:endDate, o.createdAt)
              AND o.total >= COALESCE(:minTotal, o.total)
              AND o.total <= COALESCE(:maxTotal, o.total)
            """)
    Page<Order> findByFilters(
            @Param("customerId") UUID customerId,
            @Param("status") OrderStatus status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("minTotal") BigDecimal minTotal,
            @Param("maxTotal") BigDecimal maxTotal,
            Pageable pageable
    );

    @Query("""
            SELECT new edu.unimagdalena.web.ceptu.dto.response.TopCustomerResponse(
                c.id,
                CONCAT(c.firstName, ' ', c.lastName),
                c.email,
                SUM(o.total)
            )
            FROM Order o
            JOIN o.customer c
            WHERE o.status IN (
                edu.unimagdalena.web.ceptu.entities.enums.OrderStatus.PAID, 
                edu.unimagdalena.web.ceptu.entities.enums.OrderStatus.SHIPPED, 
                edu.unimagdalena.web.ceptu.entities.enums.OrderStatus.DELIVERED
            )
            GROUP BY c.id, c.firstName, c.lastName, c.email
            ORDER BY SUM(o.total) DESC
            """)
    List<TopCustomerResponse> findTopCustomersByBilling(Pageable pageable);

    @Query("""
            SELECT new edu.unimagdalena.web.ceptu.dto.response.MonthlyIncomeResponse(
                YEAR(o.createdAt),
                MONTH(o.createdAt),
                SUM(o.total)
            )
            FROM Order o
            WHERE o.status IN (
                edu.unimagdalena.web.ceptu.entities.enums.OrderStatus.PAID, 
                edu.unimagdalena.web.ceptu.entities.enums.OrderStatus.SHIPPED, 
                edu.unimagdalena.web.ceptu.entities.enums.OrderStatus.DELIVERED
            )
            GROUP BY YEAR(o.createdAt), MONTH(o.createdAt)
            ORDER BY YEAR(o.createdAt) DESC, MONTH(o.createdAt) DESC
            """)
    List<MonthlyIncomeResponse> findMonthlyIncome();
}