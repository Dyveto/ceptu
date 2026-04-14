package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.TestcontainersConfiguration;
import edu.unimagdalena.web.ceptu.dto.request.CreateOrderItemRequest;
import edu.unimagdalena.web.ceptu.dto.request.CreateOrderRequest;
import edu.unimagdalena.web.ceptu.dto.response.OrderResponse;
import edu.unimagdalena.web.ceptu.entities.*;
import edu.unimagdalena.web.ceptu.entities.enums.CustomerStatus;
import edu.unimagdalena.web.ceptu.entities.enums.OrderStatus;
import edu.unimagdalena.web.ceptu.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired private CustomerRepository customerRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private OrderRepository orderRepository;

    private Customer testCustomer;
    private Address testAddress;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan." + UUID.randomUUID() + "@test.com")
                .status(CustomerStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();
        customerRepository.save(testCustomer);

        testAddress = Address.builder()
                .customer(testCustomer)
                .street("Calle Falsa 123")
                .city("Bogotá")
                .country("Colombia")
                .build();
        addressRepository.save(testAddress);

        Category category = Category.builder()
                .name("Electrónica")
                .build();
        categoryRepository.save(category);

        testProduct = Product.builder()
                .category(category)
                .name("Laptop")
                .price(new BigDecimal("1000.00"))
                .sku("SKU-" + UUID.randomUUID()) 
                .active(true)
                .createdAt(Instant.now())
                .build();
        productRepository.save(testProduct);

        Inventory inventory = Inventory.builder()
                .product(testProduct)
                .availableStock(10)
                .minimumStock(2)
                .updatedAt(Instant.now())
                .build();
        inventoryRepository.save(inventory);

        testProduct.setInventory(inventory);
    }

    @Test
    @DisplayName("Debe crear pedido exitosamente y descontar stock del inventario")
    void createOrder_ShouldPersistOrderAndDeductInventory() {
        CreateOrderItemRequest itemReq = new CreateOrderItemRequest(testProduct.getId(), 2);
        CreateOrderRequest request = new CreateOrderRequest(testCustomer.getId(), testAddress.getId(), List.of(itemReq));

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response.id());
        assertEquals(new BigDecimal("2000.00"), response.total());
        assertEquals(OrderStatus.CREATED, response.status());

        Order savedOrder = orderRepository.findById(response.id()).orElseThrow();
        assertEquals(1, savedOrder.getOrderItems().size());
        assertEquals(1, savedOrder.getOrderStatusHistories().size());

        Inventory updatedInventory = inventoryRepository.findByProductId(testProduct.getId()).orElseThrow();
        assertEquals(8, updatedInventory.getAvailableStock(), "El stock debe ser 10 - 2 = 8");
    }

    @Test
    @DisplayName("Debe fallar y hacer rollback cuando el stock es insuficiente")
    void createOrder_WhenInsufficientStock_ShouldThrowExceptionAndRollback() {
        CreateOrderItemRequest itemReq = new CreateOrderItemRequest(testProduct.getId(), 20);
        CreateOrderRequest request = new CreateOrderRequest(testCustomer.getId(), testAddress.getId(), List.of(itemReq));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(request);
        });
        
        assertTrue(exception.getMessage().toLowerCase().contains("stock"));

        assertEquals(0, orderRepository.count(), "No debe haber órdenes creadas tras el fallo");
    }
}