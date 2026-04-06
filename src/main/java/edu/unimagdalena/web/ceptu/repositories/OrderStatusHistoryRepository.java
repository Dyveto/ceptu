package edu.unimagdalena.web.ceptu.repositories;

import edu.unimagdalena.web.ceptu.entities.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistoryRepository, UUID> {

    // Historial de cambios de un pedido
    //List<OrderStatusHistory> findByOrderIdOrderByChangedAtAsc(UUID orderId);
    @Query("""
            SELECT h FROM OrderStatusHistory h
            WHERE h.order.id = :orderId
            ORDER BY h.changedAt ASC
            """)
    List<OrderStatusHistory> findOrderHistory(@Param("orderId") UUID orderId);
}
