package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.request.CreateProductRequest;
import edu.unimagdalena.web.ceptu.dto.request.UpdateProductRequest;
import edu.unimagdalena.web.ceptu.dto.request.UpdateInventoryRequest;
import edu.unimagdalena.web.ceptu.dto.response.ProductResponse;
import edu.unimagdalena.web.ceptu.dto.response.InventoryResponse;
import edu.unimagdalena.web.ceptu.services.ProductService;
import edu.unimagdalena.web.ceptu.services.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/inventory")
    public ResponseEntity<InventoryResponse> updateProductInventory(@PathVariable UUID id,
            @Valid @RequestBody UpdateInventoryRequest request) {
        InventoryResponse currentInventory = inventoryService.getInventoryByProductId(id);
        InventoryResponse updated = inventoryService.updateInventory(currentInventory.id(), request);
        return ResponseEntity.ok(updated);
    }
}