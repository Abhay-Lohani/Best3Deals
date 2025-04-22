package com.group5.best3deals.category.service;

import com.group5.best3deals.category.entity.Category;
import com.group5.best3deals.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with " + id));
    }
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
