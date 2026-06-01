package edu.unimagdalena.web.ceptu.repositories;

import edu.unimagdalena.web.ceptu.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    // Busca el inventario de un producto por su ID
    Optional<Inventory> findByProductId(UUID productId);

    // Trae todos los inventarios en donde el stock dispnible es menor al minimo requerido
    @Query("""
            SELECT i FROM Inventory i
            WHERE i.availableStock < i.minimumStock
            """)
    List<Inventory> findStockBelowMinimum();
}
