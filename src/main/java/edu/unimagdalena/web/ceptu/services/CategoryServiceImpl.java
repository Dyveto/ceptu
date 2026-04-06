package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.request.CreateCategoryRequest;
import edu.unimagdalena.web.ceptu.dto.response.CategoryResponse;
import edu.unimagdalena.web.ceptu.entities.Category;
import edu.unimagdalena.web.ceptu.mappers.CategoryMapper;
import edu.unimagdalena.web.ceptu.repositories.CategoryRepository;
import edu.unimagdalena.web.ceptu.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CreateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        
        category.setName(request.name());
        category.setDescription(request.description());
        
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        
        // No permitir borrar si tiene productos
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene productos asociados.");
        }
        
        categoryRepository.deleteById(id);
    }
}