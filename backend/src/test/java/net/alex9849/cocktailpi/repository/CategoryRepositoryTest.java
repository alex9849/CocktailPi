package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void createFindUpdateDeleteCategory() {
        Category category = new Category();
        category.setName("RepoCategory");
        Category created = categoryRepository.create(category);
        assertNotNull(created);

        Optional<Category> byId = categoryRepository.findById(created.getId());
        assertTrue(byId.isPresent());
        assertEquals("RepoCategory", byId.get().getName());

        Optional<Category> byName = categoryRepository.findByNameIgnoringCase("repocategory");
        assertTrue(byName.isPresent());

        created.setName("RepoCategoryUpdated");
        assertTrue(categoryRepository.update(created));

        List<Category> all = categoryRepository.findAll();
        assertTrue(all.stream().anyMatch(c -> c.getId() == created.getId()));

        assertTrue(categoryRepository.delete(created.getId()));
        assertFalse(categoryRepository.findById(created.getId()).isPresent());
    }
}
