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

class ProductRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Configura datos base antes de cada test
        testCategory = Category.builder().name("Ropa Universitaria").build();
        entityManager.persist(testCategory);

        testProduct = Product.builder()
                .category(testCategory)
                .name("Sudadera Oficial")
                .sku("SUD-001")
                .price(new BigDecimal("85000.00"))
                .active(true)
                .createdAt(Instant.now())
                .build();
        entityManager.persist(testProduct);

        Inventory inventory = Inventory.builder()
                .product(testProduct)
                .availableStock(5)
                .minimumStock(10) // Configura un stock bajo a proposito
                .updatedAt(Instant.now())
                .build();
        entityManager.persist(inventory);

        testProduct.setInventory(inventory);
        entityManager.merge(testProduct);
        entityManager.flush(); // Obliga a sincronizar con la BD real
    }

    @Test
    @DisplayName("Debe encontrar un producto por su SKU")
    void findBySku_ShouldReturnProduct() {
        Optional<Product> foundProduct = productRepository.findBySku("SUD-001");

        assertTrue(foundProduct.isPresent());
        assertEquals("Sudadera Oficial", foundProduct.get().getName());
    }

    @Test
    @DisplayName("Debe encontrar productos cuyo stock disponible sea menor al mínimo requerido")
    void findProductsWithLowStock_ShouldReturnLowStockProducts() {
        List<Product> lowStockProducts = productRepository.findProductsWithLowStock();

        assertFalse(lowStockProducts.isEmpty());
        assertEquals(1, lowStockProducts.size());
        assertEquals("SUD-001", lowStockProducts.get(0).getSku());
    }

    @Test
    @DisplayName("Debe retornar los productos activos pertenecientes a una categoría")
    void findByCategoryIdAndActiveTrue_ShouldReturnProducts() {
        List<Product> products = productRepository.findByCategoryIdAndActiveTrue(testCategory.getId());

        assertFalse(products.isEmpty());
        assertTrue(products.get(0).isActive());
        assertEquals(testCategory.getId(), products.get(0).getCategory().getId());
    }
}