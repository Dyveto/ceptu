package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.request.CreateProductRequest;
import edu.unimagdalena.web.ceptu.dto.response.CategoryResponse;
import edu.unimagdalena.web.ceptu.dto.response.InventoryResponse;
import edu.unimagdalena.web.ceptu.dto.response.ProductResponse;
import edu.unimagdalena.web.ceptu.entities.Category;
import edu.unimagdalena.web.ceptu.entities.Product;
import edu.unimagdalena.web.ceptu.mappers.ProductMapper;
import edu.unimagdalena.web.ceptu.repositories.CategoryRepository;
import edu.unimagdalena.web.ceptu.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductMapper productMapper;
    @InjectMocks private ProductServiceImpl productService;

    @Test
    void createProduct_ShouldAssignInventoryAndSave() {
        UUID categoryId = UUID.randomUUID();
        CreateProductRequest request = new CreateProductRequest(categoryId, "Teclado", "SKU-123", new BigDecimal("150.00"), 50, 10);
        
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Electrónica");

        Product productToSave = new Product();
        Product savedProduct = new Product();
        
        CategoryResponse catResponse = new CategoryResponse(categoryId, "Electrónica", "Desc");
        InventoryResponse invResponse = new InventoryResponse(UUID.randomUUID(), 50, 10, Instant.now());
        
        ProductResponse expectedResponse = new ProductResponse(UUID.randomUUID(), "Teclado", "SKU-123", new BigDecimal("150.00"), true, catResponse, invResponse, Instant.now());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(request)).thenReturn(productToSave);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toResponse(savedProduct)).thenReturn(expectedResponse);

        productService.createProduct(request);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertNotNull(productCaptor.getValue().getInventory());
    }

    @Test
    void createProduct_WhenCategoryNotFound_ShouldThrowException() {
        UUID categoryId = UUID.randomUUID();
        CreateProductRequest request = new CreateProductRequest(categoryId, "Teclado", "SKU-123", new BigDecimal("150.00"), 50, 10);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.createProduct(request));
    }
}