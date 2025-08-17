package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.payload.dto.category.CategoryDto;
import net.alex9849.cocktailpi.repository.CategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategory(long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category createCategory(Category category) {
        if(categoryRepository.findByNameIgnoringCase(category.getName()).isPresent()) {
            throw new IllegalArgumentException("Category already exists!");
        }
        return categoryRepository.create(category);
    }

    public Category updateCategory(Category category) {
        if(!categoryRepository.findById(category.getId()).isPresent()) {
            throw new IllegalArgumentException("Category doesn't exist!");
        }
        Optional<Category> categoryOptional = categoryRepository.findByNameIgnoringCase(category.getName());
        if(categoryOptional.isPresent()
                && !(categoryOptional.get().getId() == category.getId())
                && categoryOptional.get().getName().equalsIgnoreCase(category.getName())) {
            throw new IllegalArgumentException("Category with this name already exists!");
        }
        categoryRepository.update(category);
        return category;
    }

    public void deleteCategory(long categoryId) {
        categoryRepository.delete(categoryId);
    }

    public Category fromDto(CategoryDto.Request.Create categoryDto) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDto, category);
        return category;
    }

    public List<Category> getByRecipeId(long recipeId) {
        return categoryRepository.findByRecipeId(recipeId);
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoringCase(name).orElse(null);
    }
}
