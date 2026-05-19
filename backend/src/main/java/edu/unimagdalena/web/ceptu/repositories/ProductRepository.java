package edu.unimagdalena.web.ceptu.repositories;

import edu.unimagdalena.web.ceptu.dto.response.BestSellingProductResponse;
import edu.unimagdalena.web.ceptu.dto.response.LowStockProductResponse;
import edu.unimagdalena.web.ceptu.entities.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySku(String sku);

    List<Product> findByCategoryIdAndActiveTrue(UUID categoryId);

    // 🏆 CORREGIDO: Mapeo exacto hacia LowStockProductResponse
    @Query("""
            SELECT new edu.unimagdalena.web.ceptu.dto.response.LowStockProductResponse(
                p.id, 
                p.name, 
                p.sku, 
                i.availableStock, 
                i.minimumStock
            )
            FROM Product p
            JOIN p.inventory i
            WHERE i.availableStock < i.minimumStock
            """)
    List<LowStockProductResponse> findProductsWithLowStock();

    @Query("""
            SELECT p FROM Product p
            JOIN p.inventory i
            WHERE i.availableStock < i.minimumStock
            AND p.active = true
            """)
    List<Product> findActiveProductsWithLowStock();

    // 🏆 CORREGIDO: Mapeo exacto hacia BestSellingProductResponse cruzando fechas
    @Query("""
            SELECT new edu.unimagdalena.web.ceptu.dto.response.BestSellingProductResponse(
                p.id, 
                p.name, 
                p.sku, 
                SUM(oi.quantity)
            )
            FROM OrderItem oi
            JOIN oi.product p
            JOIN oi.order o
            WHERE o.status != edu.unimagdalena.web.ceptu.entities.enums.OrderStatus.CANCELLED
            AND o.createdAt BETWEEN :startDate AND :endDate
            GROUP BY p.id, p.name, p.sku
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<BestSellingProductResponse> findBestSellingProducts(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable);

    @Query("""
            SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
            FROM Order o
            JOIN o.orderItems oi
            WHERE oi.product.id = :productId
            AND o.status NOT IN (edu.unimagdalena.web.ceptu.entities.enums.OrderStatus.DELIVERED, edu.unimagdalena.web.ceptu.entities.enums.OrderStatus.CANCELLED)
            """)
    boolean hasActiveOrders(@Param("productId") UUID productId);
}