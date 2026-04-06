package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.request.CreateCategoryRequest;
import edu.unimagdalena.web.ceptu.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(CreateCategoryRequest request);
    CategoryResponse getCategoryById(UUID id);
    List<CategoryResponse> getAllCategories();
    CategoryResponse updateCategory(UUID id, CreateCategoryRequest request);
    void deleteCategory(UUID id);
}