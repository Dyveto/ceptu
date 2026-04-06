package edu.unimagdalena.web.ceptu.services.impl;

import edu.unimagdalena.web.ceptu.dto.request.CreateCategoryRequest;
import edu.unimagdalena.web.ceptu.dto.response.CategoryResponse;
import edu.unimagdalena.web.ceptu.entities.Category;
import edu.unimagdalena.web.ceptu.mappers.CategoryMapper;
import edu.unimagdalena.web.ceptu.repositories.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository; // Simulamos la base de datos

    @Mock
    private CategoryMapper categoryMapper; // Simulamos el mapper

    @InjectMocks
    private CategoryServiceImpl categoryService; // Inyectamos los mocks en el servicio real

    @Test
    void createCategory_ShouldReturnCategoryResponse() {
        // Arrange (Preparar los datos y el comportamiento de los mocks)
        CreateCategoryRequest request = new CreateCategoryRequest("Electrónica", "Dispositivos");
        Category category = new Category();
        Category savedCategory = new Category();
        CategoryResponse expectedResponse = new CategoryResponse(UUID.randomUUID(), "Electrónica", "Dispositivos");

        when(categoryMapper.toEntity(request)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toResponse(savedCategory)).thenReturn(expectedResponse);

        // Act (Ejecutar el método del servicio)
        CategoryResponse result = categoryService.createCategory(request);

        // Assert (Verificar que los resultados son los esperados)
        assertNotNull(result);
        assertEquals("Electrónica", result.name());
        verify(categoryRepository, times(1)).save(category); // Verificamos que se llamó al repositorio
    }

    @Test
    void getCategoryById_WhenNotFound_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty()); // Simulamos que no existe

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(id);
        });

        assertEquals("Categoría no encontrada con ID: " + id, exception.getMessage());
        verify(categoryRepository, times(1)).findById(id);
    }
}