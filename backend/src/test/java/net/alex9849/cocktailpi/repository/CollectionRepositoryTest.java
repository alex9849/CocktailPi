package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.Collection;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private CollectionRepository collectionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    void createUpdateAndImageRoundTrip() {
        User owner = userRepository.findAll().get(0);
        Collection collection = new Collection();
        collection.setName("RepoCollection");
        collection.setDescription("Repo collection");
        collection.setOwner(owner);
        Collection created = collectionRepository.create(collection);
        assertNotNull(created);

        created.setDescription("Updated");
        assertTrue(collectionRepository.update(created));

        byte[] image = "image".getBytes(StandardCharsets.UTF_8);
        collectionRepository.setImage(created.getId(), image);
        assertTrue(collectionRepository.getImage(created.getId()).isPresent());
    }

    @Test
    void addAndRemoveRecipe() {
        User owner = userRepository.findAll().get(0);
        Collection collection = new Collection();
        collection.setName("RepoCollectionRecipe");
        collection.setDescription("Repo collection");
        collection.setOwner(owner);
        Collection created = collectionRepository.create(collection);

        Recipe recipe = recipeRepository.findAll().get(0);
        assertTrue(collectionRepository.addRecipe(created.getId(), recipe.getId()));
        assertTrue(collectionRepository.removeRecipe(created.getId(), recipe.getId()));

        Set<Long> ids = collectionRepository.findIdsByName(created.getName());
        assertTrue(ids.contains(created.getId()));
    }

    @Test
    void findByIdsAndOwnedByUser() {
        User owner = userRepository.findAll().get(0);
        Collection collection = new Collection();
        collection.setName("RepoCollectionOwner");
        collection.setDescription("Repo collection");
        collection.setOwner(owner);
        Collection created = collectionRepository.create(collection);

        List<Collection> found = collectionRepository.findByIds(created.getId());
        assertEquals(1, found.size());

        Set<Long> ownedIds = collectionRepository.findIdsOwnedByUser(owner.getId());
        assertTrue(ownedIds.contains(created.getId()));
    }
}
