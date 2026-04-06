package edu.unimagdalena.web.ceptu.repositories;

import edu.unimagdalena.web.ceptu.dto.BestSellingProductDTO;
import edu.unimagdalena.web.ceptu.dto.TopCategoryDTO;
import edu.unimagdalena.web.ceptu.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    //Buscar producto por SKU
    Optional<Product> findBySku(String sku);

    //Buscar productos activos por categoria
    List<Product> findByCategoryIdAndActiveTrue(UUID categoryId);

    //Buscar productos con stock insuficiente con respecto al minimo
    @Query("""
            SELECT p FROM Product p
            JOIN p.inventory i
            WHERE i.availableStock < i.minimumStock
            """)
    List<Product> findProductsWithLowStock();

    //Buscar productos con bajo stock que esten activos
    @Query("""
            SELECT p FROM Product p
            JOIN p.inventory i
            WHERE i.availableStock < i.minimumStock
            AND p.active = true
            """)
    List<Product> findActiveProductsWithLowStock();

    //Buscar productos mas vendidos por periodo
    @Query("""
            SELECT p AS product, SUM(oi.quantity) AS totalSold
            FROM OrderItem oi
            JOIN oi.product p
            JOIN oi.order o
            WHERE o.status != 'CANCELLED'
            AND o.createdAt BETWEEN :startDate AND :endDate
            GROUP BY p
            ORDER BY totalSold DESC
            """)
    List<BestSellingProductDTO> findBestSellingProducts(
            Instant startDate,
            Instant endDate,
            Pageable pageable);

    //Buscar Top de categorías por volumen de ventas
    @Query("""
            SELECT c.name AS name, SUM(oi.quantity) AS totalSold
            FROM OrderItem oi
            JOIN oi.product p
            JOIN p.category c
            JOIN oi.order o
            WHERE o.status != 'CANCELLED'
            GROUP BY c.name
            ORDER BY totalSold DESC
            """)
    List<TopCategoryDTO> findTopCategoriesBySalesVolume(Pageable pageable);

    // Verificar si un producto tiene pedidos activos
    @Query("""
            SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
            FROM Order o
            JOIN o.orderItems oi
            WHERE oi.product.id = :productId
            AND o.status NOT IN ('DELIVERED', 'CANCELLED')
            """)
    boolean hasActiveOrders(UUID productId);
}
