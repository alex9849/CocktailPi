package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.Collection;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.payload.dto.collection.CollectionDto;
import net.alex9849.cocktailmaker.repository.CollectionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CollectionService {
    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserService userService;

    public Collection createCollection(Collection collection) {
        return collectionRepository.create(collection);
    }

    public Collection updateCollection(Collection collection, BufferedImage image, boolean removeImage) throws IOException {
        if(collectionRepository.findByIds(collection.getId()).isEmpty()) {
            throw new IllegalArgumentException("Collection doesn't exist!");
        }
        collectionRepository.update(collection);
        if(removeImage) {
            collectionRepository.setImage(collection.getId(), null);
        }
        if(image != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", out);
            collectionRepository.setImage(collection.getId(), out.toByteArray());
        }

        return collectionRepository.findByIds(collection.getId()).get(0);
    }

    public boolean deleteCollection(long id) {
        return collectionRepository.delete(id);
    }

    public boolean addRecipe(long recipeId, long collectionId) {
        List<Collection> foundCollections = collectionRepository.findByIds(collectionId);
        if(foundCollections.isEmpty()) {
            throw new IllegalArgumentException("Collection not found!");
        }
        Collection collection = foundCollections.get(0);
        Recipe foundRecipe = recipeService.getById(recipeId);
        if(foundRecipe == null) {
            throw new IllegalArgumentException("Recipe not found!");
        }
        collectionRepository.removeRecipe(collectionId, recipeId);
        return collectionRepository.addRecipe(collectionId, recipeId);
    }

    public boolean removeRecipe(long recipeId, long collectionId) {
        return collectionRepository.removeRecipe(collectionId, recipeId);
    }

    public Collection getCollectionById(long id) {
        List<Collection> collections = collectionRepository.findByIds(id);
        if(collections.isEmpty()) {
            return null;
        }
        return collections.get(0);
    }

    public List<Collection> getCollectionsByOwner(long userId) {
        Set<Long> ids = collectionRepository.findIdsOwnedByUser(userId);
        if(ids.isEmpty()) {
            return new ArrayList<>();
        }
        return collectionRepository.findByIds(ids.toArray(new Long[1]));
    }

    public Collection fromDto(CollectionDto.Request.Create collectionDto, long ownerId) {
        if(collectionDto == null) {
            return null;
        }
        Collection collection = new Collection();
        BeanUtils.copyProperties(collectionDto, collection);
        collection.setOwnerId(ownerId);
        return collection;
    }

    public byte[] getImage(long collectionId) {
        return collectionRepository.getImage(collectionId).orElse(null);
    }
}
