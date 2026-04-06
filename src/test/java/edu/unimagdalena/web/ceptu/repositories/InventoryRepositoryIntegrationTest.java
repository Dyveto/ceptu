package edu.unimagdalena.web.ceptu.repositories;

import edu.unimagdalena.web.ceptu.entities.Category;
import edu.unimagdalena.web.ceptu.entities.Inventory;
import edu.unimagdalena.web.ceptu.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InventoryRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Product productWithLowStock;
    private Product productWithGoodStock;

    @BeforeEach
    void setUp() {
        Category category = Category.builder().name("Accesorios").build();
        entityManager.persist(category);

        // Producto 1: Con stock por debajo del mínimo
        productWithLowStock = Product.builder()
                .category(category)
                .name("Cuaderno Institucional")
                .sku("CUAD-001")
                .price(new BigDecimal("15000.00"))
                .active(true)
                .createdAt(Instant.now())
                .build();
        entityManager.persist(productWithLowStock);

        Inventory lowInventory = Inventory.builder()
                .product(productWithLowStock)
                .availableStock(5) // Stock actual
                .minimumStock(10)  // Stock mínimo (está por debajo)
                .updatedAt(Instant.now())
                .build();
        entityManager.persist(lowInventory);
        productWithLowStock.setInventory(lowInventory);
        entityManager.merge(productWithLowStock);

        // Producto 2: Con stock suficiente
        productWithGoodStock = Product.builder()
                .category(category)
                .name("Gorra Universitaria")
                .sku("GORR-001")
                .price(new BigDecimal("25000.00"))
                .active(true)
                .createdAt(Instant.now())
                .build();
        entityManager.persist(productWithGoodStock);

        Inventory goodInventory = Inventory.builder()
                .product(productWithGoodStock)
                .availableStock(50) // Stock actual
                .minimumStock(10)   // Stock mínimo (está bien)
                .updatedAt(Instant.now())
                .build();
        entityManager.persist(goodInventory);
        productWithGoodStock.setInventory(goodInventory);
        entityManager.merge(productWithGoodStock);

        entityManager.flush();
    }

    @Test
    @DisplayName("Debe encontrar el inventario de un producto por el ID del producto")
    void findByProductId_ShouldReturnInventory() {
        Optional<Inventory> foundInventory = inventoryRepository.findByProductId(productWithLowStock.getId());

        assertTrue(foundInventory.isPresent());
        assertEquals(5, foundInventory.get().getAvailableStock());
    }

    @Test
    @DisplayName("Debe retornar únicamente los inventarios cuyo stock disponible es menor al mínimo")
    void findStockBelowMinimum_ShouldReturnOnlyLowStockInventories() {
        List<Inventory> lowStockInventories = inventoryRepository.findStockBelowMinimum();

        assertFalse(lowStockInventories.isEmpty());
        assertEquals(1, lowStockInventories.size(), "Solo debe traer 1 inventario (el del cuaderno)");
        assertEquals(productWithLowStock.getId(), lowStockInventories.get(0).getProduct().getId());
    }
}