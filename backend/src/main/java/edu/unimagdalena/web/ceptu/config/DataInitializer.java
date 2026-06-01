package edu.unimagdalena.web.ceptu.config;

import edu.unimagdalena.web.ceptu.entities.*;
import edu.unimagdalena.web.ceptu.entities.enums.CustomerStatus;
import edu.unimagdalena.web.ceptu.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    @Override
    public void run(String... args) throws Exception {
        // 🚀 Si no hay productos, poblamos la base de datos con datos de prueba comerciales
        if (productRepository.count() == 0) {
            
            // 1. Crear Categorías
            Category electro = Category.builder().name("Electrónica").description("Laptops, pantallas y gadgets").build();
            Category hogar = Category.builder().name("Hogar").description("Artículos para casa").build();
            categoryRepository.save(electro);
            categoryRepository.save(hogar);

            // 2. Crear Productos e Inventarios asociados (Relación 1:1)
            Product laptop = Product.builder()
                    .name("Laptop Asus ZenBook")
                    .price(new BigDecimal("3500000.00"))
                    .sku("SKU-ASUS-001")
                    .active(true)
                    .category(electro)
                    .createdAt(Instant.now())
                    .build();
            productRepository.save(laptop);

            Inventory invLaptop = Inventory.builder()
                    .product(laptop)
                    .availableStock(12) // 👈 Aquí ya tendrás stock inicial para ver en el front
                    .minimumStock(3)
                    .updatedAt(Instant.now())
                    .build();
            inventoryRepository.save(invLaptop);
            laptop.setInventory(invLaptop);

            Product monitor = Product.builder()
                    .name("Monitor Gamer 24''")
                    .price(new BigDecimal("850000.00"))
                    .sku("SKU-LG-992")
                    .active(true)
                    .category(electro)
                    .createdAt(Instant.now())
                    .build();
            productRepository.save(monitor);

            Inventory invMonitor = Inventory.builder()
                    .product(monitor)
                    .availableStock(5)
                    .minimumStock(2)
                    .updatedAt(Instant.now())
                    .build();
            inventoryRepository.save(invMonitor);
            monitor.setInventory(invMonitor);

            // 3. Crear un Cliente y Dirección por defecto para poder probar las compras
            Customer clienteMock = Customer.builder()
                    .firstName("Carlos")
                    .lastName("Vives")
                    .email("carlos@test.com")
                    .status(CustomerStatus.ACTIVE)
                    .createdAt(Instant.now())
                    .build();
            customerRepository.save(clienteMock);

            Address direccionMock = Address.builder()
                    .customer(clienteMock)
                    .street("Avenida del Libertador #14-23")
                    .city("Santa Marta")
                    .country("Colombia")
                    .state("Magdalena")
                    .zipCode("470001")
                    .build();
            addressRepository.save(direccionMock);

            System.out.println("🔥 [DataInitializer] Base de datos poblada con éxito para pruebas locales.");
        }
    }
}
