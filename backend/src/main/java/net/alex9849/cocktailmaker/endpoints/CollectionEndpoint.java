package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.Collection;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.collection.CollectionDto;
import net.alex9849.cocktailmaker.service.CollectionService;
import net.alex9849.cocktailmaker.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/api/collection")
public class CollectionEndpoint {

    @Autowired
    private CollectionService collectionService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createCollection(@Valid @RequestBody CollectionDto.Request.Create collectionDto, UriComponentsBuilder uriBuilder) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection collection = collectionService.fromDto(collectionDto, principal.getId());
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
        return ResponseEntity.ok(new CollectionDto.Response.Detailed(collection));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getCollections(@RequestParam(value = "ownerId", required = true) long ownerId) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(ownerId != principal.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(collectionService.getCollectionsByOwner(ownerId)
                .stream().map(CollectionDto.Response.Detailed::new));
    }

    @RequestMapping(path = "{id}/image", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<?> getRecipeImage(@PathVariable("id") long id) {
        byte[] image = collectionService.getImage(id);
        if(image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(image);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCollection(@Valid @RequestPart("collection") CollectionDto.Request.Create collectionDto,
                                              @RequestPart(value = "image", required = false) MultipartFile file,
                                              @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
                                              @PathVariable("id") long id, HttpServletRequest request) throws IOException {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection existingCollection = collectionService.getCollectionById(id);
        if(existingCollection == null) {
            return ResponseEntity.notFound().build();
        }
        if (existingCollection.getOwner().getId() != principal.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Collection updateCollection = collectionService.fromDto(collectionDto, existingCollection.getOwnerId());
        updateCollection.setId(id);
        BufferedImage image = null;
        if(file != null) {
            try {
                image = ImageIO.read(file.getInputStream());
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid image format!");
            }
            image = ImageUtils.resizeImage(image, 1000, 16d / 9);
        }
        collectionService.updateCollection(updateCollection, image, removeImage);
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

    @RequestMapping(value = "{id}/{recipeId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeRecipeFromCollection(@PathVariable(value = "id") long collectionId,
                                                        @PathVariable(value = "recipeId") long recipeId) {
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
