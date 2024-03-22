package org.garden.com.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.garden.com.entity.Category;
import org.garden.com.exceptions.CategoryNotFoundException;
import org.garden.com.exceptions.InvalidCategoryArgumentException;
import org.garden.com.exceptions.ProductInvalidArgumentException;
import org.garden.com.repository.CategoryJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryJpaRepository repository;

    @Autowired
    private Validator validator;

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    public Category createCategory(Category category) {
        log.info("Creating category: {}", category);
        validateCategory(category);
        Category createdCategory = repository.save(category);
        log.info("Category created: {}", createdCategory);
        return createdCategory;
    }

    @Override
    public List<Category> getAllCategories() {
        log.info("Fetching list of categories");
        List<Category> categoryList = repository.findAll();
        log.info("Found {} categories", categoryList.size());
        return categoryList;
    }

    @Override
    public Category editCategory(long id, Category category) {
        log.info("Editing category with ID {}: {}", id, category);
        validateCategory(category);
        Category newCategory = repository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category with id " + id + " not found"));
        newCategory.setName(category.getName());
        Category savedCategory = repository.save(newCategory);
        log.info("Category updated: {}", savedCategory);
        return savedCategory;
    }

    @Override
    public ResponseEntity<Void> deleteCategoryById(long id) {
        log.info("Deleting category with ID: {}", id);
        if (repository.existsById(id)){
            repository.deleteById(id);
            log.info("Category with ID {} deleted", id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }else {
            log.warn("Category not deleted: {}", id);
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }

    }

    private void validateCategory(Category category) {
        log.info("Validating category: {}", category);
        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        if (!violations.isEmpty()) {
            log.warn("Validation exception: {}", category);
            throw new InvalidCategoryArgumentException(violations.iterator().next().getMessage());
        }
    }
}
