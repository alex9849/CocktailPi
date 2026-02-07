package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.IngredientGroup;
import net.alex9849.cocktailpi.model.recipe.ingredient.ManualIngredient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IngredientRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    void createUpdateAndDeleteIngredients() {
        ManualIngredient manual = new ManualIngredient();
        manual.setName("RepoManual");
        manual.setUnit(Ingredient.Unit.MILLILITER);
        manual.setAlcoholContent(0);
        manual.setInBar(false);
        Ingredient createdManual = ingredientRepository.create(manual);
        assertNotNull(createdManual);

        AutomatedIngredient automated = new AutomatedIngredient();
        automated.setName("RepoAuto");
        automated.setPumpTimeMultiplier(1.0);
        automated.setBottleSize(1000);
        automated.setAlcoholContent(10);
        automated.setInBar(false);
        Ingredient createdAuto = ingredientRepository.create(automated);
        assertNotNull(createdAuto);

        IngredientGroup group = new IngredientGroup();
        group.setName("RepoGroup");
        Ingredient createdGroup = ingredientRepository.create(group);
        assertNotNull(createdGroup);

        createdManual.setParentGroupId(createdGroup.getId());
        assertTrue(ingredientRepository.update(createdManual));

        Optional<Ingredient> found = ingredientRepository.findById(createdManual.getId());
        assertTrue(found.isPresent());
        assertEquals(createdGroup.getId(), found.get().getParentGroupId());

        byte[] image = "img".getBytes(StandardCharsets.UTF_8);
        ingredientRepository.setImage(createdAuto.getId(), image);
        assertTrue(ingredientRepository.getImage(createdAuto.getId()).isPresent());
    }

    @Test
    void queryHelpersReturnResults() {
        Set<Long> ids = ingredientRepository.findAllIds();
        assertTrue(ids.size() > 0);

        Set<Long> auto = ingredientRepository.findIdsNotManual();
        Set<Long> manual = ingredientRepository.findIdsNotAutomatic();
        Set<Long> groups = ingredientRepository.findIdsNotGroup();
        assertNotNull(auto);
        assertNotNull(manual);
        assertNotNull(groups);

        Set<Long> noParents = ingredientRepository.findIdsWithoutParents();
        assertNotNull(noParents);
    }

    @Test
    void autocompleteAndNameLookup() {
        ManualIngredient manual = new ManualIngredient();
        manual.setName("RepoAutoComplete");
        manual.setUnit(Ingredient.Unit.MILLILITER);
        manual.setAlcoholContent(0);
        manual.setInBar(true);
        AddableIngredient created = (AddableIngredient) ingredientRepository.create(manual);

        Set<Long> found = ingredientRepository.findIdsAutocompleteName("RepoAuto");
        assertTrue(found.contains(created.getId()));

        Set<Long> byName = ingredientRepository.findIdsByNameIgnoringCase("repoautocomplete");
        assertTrue(byName.contains(created.getId()));
    }
}
