package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.Collection;
import net.alex9849.cocktailmaker.payload.dto.collection.CollectionDto;
import net.alex9849.cocktailmaker.repository.CollectionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public boolean updateCollection(Collection collection) {
        if(collectionRepository.findByIds(collection.getId()).isEmpty()) {
            throw new IllegalArgumentException("Collection doesn't exist!");
        }
        return collectionRepository.update(collection);
    }

    public boolean deleteCollection(long id) {
        return collectionRepository.delete(id);
    }

    public boolean addRecipe(long recipeId, long collectionId) {
        if(recipeService.getById(recipeId) == null) {
            throw new IllegalArgumentException("Recipe not found!");
        }
        return collectionRepository.addRecipe(recipeId, collectionId);
    }

    public boolean removeRecipe(long recipeId, long collectionId) {
        return collectionRepository.removeRecipe(recipeId, collectionId);
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

    public Collection fromDto(CollectionDto collectionDto) {
        if(collectionDto == null) {
            return null;
        }
        Collection collection = new Collection();
        BeanUtils.copyProperties(collectionDto, collection);
        collection.setOwner(userService.getUser(collectionDto.getOwner().getId()));
        if(collection.getOwner() != null) {
            collection.setOwnerId(collection.getOwner().getId());
        }
        return collection;
    }
}
