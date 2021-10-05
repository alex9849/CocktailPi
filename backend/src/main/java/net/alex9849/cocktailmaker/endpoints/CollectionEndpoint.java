package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.Collection;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.collection.CollectionDto;
import net.alex9849.cocktailmaker.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/collection")
public class CollectionEndpoint {

    @Autowired
    private CollectionService collectionService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createCollection(@Valid @RequestBody CollectionDto collectionDto, UriComponentsBuilder uriBuilder) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection collection = collectionService.fromDto(collectionDto);
        collection.setOwner(principal);
        collection = collectionService.createCollection(collection);
        UriComponents uriComponents = uriBuilder.path("/api/collection/{id}").buildAndExpand(collection.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getCollection(@PathVariable(value = "id") long id) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection collection = collectionService.getCollectionById(id);
        if(collection == null) {
            return ResponseEntity.notFound().build();
        }
        if(collection.getOwner().getId() != principal.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(collection);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getCollections(@RequestParam(value = "ownerid", required = true) long ownerId) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(ownerId != principal.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(collectionService.getCollectionsByOwner(ownerId));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCollection(@PathVariable(value = "id") long id,
                                              @Valid @RequestBody CollectionDto collectionDto) {
        collectionDto.setId(id);
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection existingCollection = collectionService.getCollectionById(id);
        if(existingCollection == null) {
            return ResponseEntity.notFound().build();
        }
        if (existingCollection.getOwner().getId() != principal.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        collectionService.updateCollection(collectionService.fromDto(collectionDto));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCollection(@PathVariable(value = "id") long id) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection existingCollection = collectionService.getCollectionById(id);
        if(existingCollection == null) {
            return ResponseEntity.notFound().build();
        }
        if (existingCollection.getOwner().getId() != principal.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        collectionService.deleteCollection(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "{id}/add", method = RequestMethod.POST)
    public ResponseEntity<?> addRecipeToCollection(@PathVariable(value = "id") long collectionId,
                                                   @RequestBody(required = true) long recipeId) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection existingCollection = collectionService.getCollectionById(collectionId);
        if(existingCollection == null) {
            return ResponseEntity.notFound().build();
        }
        if (existingCollection.getOwner().getId() != principal.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        collectionService.addRecipe(recipeId, collectionId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "{id}/remove", method = RequestMethod.POST)
    public ResponseEntity<?> removeRecipeFromCollection(@PathVariable(value = "id") long collectionId,
                                                   @RequestBody(required = true) long recipeId) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection existingCollection = collectionService.getCollectionById(collectionId);
        if(existingCollection == null) {
            return ResponseEntity.notFound().build();
        }
        if (existingCollection.getOwner().getId() != principal.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        collectionService.removeRecipe(recipeId, collectionId);
        return ResponseEntity.ok().build();
    }

}
