package net.alex9849.cocktailpi.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailpi.model.Collection;
import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.collection.CollectionDto;
import net.alex9849.cocktailpi.service.CollectionService;
import net.alex9849.cocktailpi.utils.ImageUtils;
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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/collection/")
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
        return ResponseEntity.created(uriComponents.toUri()).body(new CollectionDto.Response.Detailed(collection));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getCollection(@PathVariable(value = "id") long id) {
        Collection collection = collectionService.getCollectionById(id);
        if(collection == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new CollectionDto.Response.Detailed(collection));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getCollections(@RequestParam(value = "ownerId", required = false) Long ownerId) {
        List<Collection> collectionList;
        if(ownerId != null) {
            collectionList = collectionService.getCollectionsByOwner(ownerId);
        } else {
            collectionList = collectionService.getAll();
        }
        return ResponseEntity.ok(collectionList.stream().map(CollectionDto.Response.Detailed::new));
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
                                              @PathVariable("id") long id) throws IOException {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection existingCollection = collectionService.getCollectionById(id);
        if(existingCollection == null) {
            return ResponseEntity.notFound().build();
        }
        if (existingCollection.getOwner().getId() != principal.getId()
                && principal.getAuthority().getLevel() < ERole.ROLE_ADMIN.getLevel()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Collection updateCollection = collectionService.fromDto(collectionDto, existingCollection.getOwnerId());
        updateCollection.setId(id);
        byte[] image = null;
        if(file != null) {
            try {
                BufferedImage bImage = ImageIO.read(file.getInputStream());
                bImage = ImageUtils.resizeImage(bImage, 2000, 16d / 9);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(bImage, "jpg", out);
                image = out.toByteArray();
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid image format!");
            }
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
        if (existingCollection.getOwner().getId() != principal.getId()
                && principal.getAuthority().getLevel() < ERole.ROLE_ADMIN.getLevel()) {
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
        if (existingCollection.getOwner().getId() != principal.getId()
                && principal.getAuthority().getLevel() < ERole.ROLE_ADMIN.getLevel()) {
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
        if (existingCollection.getOwner().getId() != principal.getId()
                && principal.getAuthority().getLevel() < ERole.ROLE_ADMIN.getLevel()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        collectionService.removeRecipe(recipeId, collectionId);
        return ResponseEntity.ok().build();
    }

}
