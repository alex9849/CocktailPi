package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.Glass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlassRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private GlassRepository glassRepository;

    @Test
    void createUpdateAndDeleteGlass() {
        Glass glass = new Glass();
        glass.setName("RepoGlass");
        glass.setSize(200);
        glass.setDefault(true);
        glass.setUseForSingleIngredients(true);
        Glass created = glassRepository.create(glass);
        assertNotNull(created);

        Optional<Glass> found = glassRepository.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals(created.getId(), glassRepository.getDefaultGlassId());
        assertEquals(created.getId(), glassRepository.getSingleIngredientGlassId());

        created.setDefault(false);
        created.setUseForSingleIngredients(false);
        assertTrue(glassRepository.update(created));

        glassRepository.delete(created.getId());
    }

    @Test
    void findIdsByName() {
        Glass glass = new Glass();
        glass.setName("RepoGlassName");
        glass.setSize(150);
        Glass created = glassRepository.create(glass);

        Set<Long> ids = glassRepository.findIdsByName("RepoGlassName");
        assertTrue(ids.contains(created.getId()));
    }
}
