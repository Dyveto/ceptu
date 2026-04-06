package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.request.CreateProductRequest;
import edu.unimagdalena.web.ceptu.dto.request.UpdateProductRequest;
import edu.unimagdalena.web.ceptu.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse getProductById(UUID id);
    List<ProductResponse> getAllProducts();
    ProductResponse updateProduct(UUID id, UpdateProductRequest request);
    void deleteProduct(UUID id);