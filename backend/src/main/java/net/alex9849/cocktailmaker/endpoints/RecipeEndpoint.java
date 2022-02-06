package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.Collection;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.service.CollectionService;
import net.alex9849.cocktailmaker.service.RecipeService;
import net.alex9849.cocktailmaker.service.UserService;
import net.alex9849.cocktailmaker.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipe")
public class RecipeEndpoint {

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserService userService;

    @Autowired
    CollectionService collectionService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    ResponseEntity<?> getRecipesByFilter(@RequestParam(value = "ownerId", required = false) Long ownerId,
                                         @RequestParam(value = "inCollection", required = false) Long inCollectionId,
                                         @RequestParam(value = "fabricable", defaultValue = "false") boolean isFabricable,
                                         @RequestParam(value = "inBar", defaultValue = "false") boolean isInBar,
                                         @RequestParam(value = "containsIngredients", required = false) Long[] containsIngredients,
                                         @RequestParam(value = "searchName", required = false) String searchName,
                                         @RequestParam(value = "inCategory", required = false) Long inCategory,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(inCollectionId != null) {
            Collection collection = collectionService.getCollectionById(inCollectionId);
            if(collection == null) {
                inCollectionId = null;
            } else {
                if (!Objects.equals(principal.getId(), collection.getOwner().getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }
        final int pageSize = 10;
        page = Math.max(page, 0);
        Sort sort;
        switch (orderBy) {
            case "lastUpdateDesc":
                sort = Sort.by(Sort.Direction.DESC, "last_update");
                break;
            case "lastUpdateAsc":
                sort = Sort.by(Sort.Direction.ASC, "last_update");
                break;
            case "nameDesc":
                sort = Sort.by(Sort.Direction.DESC, "name");
                break;
            default:
                sort = Sort.by(Sort.Direction.ASC, "name");
                break;
        }
        Page<Recipe> recipePage = recipeService.getRecipesByFilter(ownerId,
                inCollectionId, inCategory, containsIngredients, searchName, isFabricable,
                isInBar, page, pageSize, sort);
        List<RecipeDto> recipeDtos = recipePage.stream().map(RecipeDto::new).collect(Collectors.toList());
        return ResponseEntity.ok().body(new PageImpl<>(recipeDtos, recipePage.getPageable(), recipePage.getTotalElements()));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    ResponseEntity<?> getRecipe(@PathVariable("id") long id, HttpServletRequest request) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.getById(id);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new RecipeDto(recipe));
    }

    @PreAuthorize("hasAnyRole('RECIPE_CREATOR', 'ADMIN', 'PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(path = "", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> createRecipe(@Valid @RequestPart("recipe") RecipeDto recipeDto,
                                   @RequestPart(value = "image", required = false) MultipartFile file, UriComponentsBuilder uriBuilder) throws IOException {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.fromDto(recipeDto);
        recipe.setOwner(userService.getUser(principal.getId()));
        recipe = recipeService.createRecipe(recipe);
        if (file != null) {
            BufferedImage image;
            try {
                image = ImageIO.read(file.getInputStream());
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid image format!");
            }
            image = ImageUtils.resizeImage(image, 1000, 16d / 9);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", out);
            recipeService.setImage(recipe.getId(), out.toByteArray());
        }
        UriComponents uriComponents = uriBuilder.path("/api/recipe/{id}").buildAndExpand(recipe.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(new RecipeDto(recipe));
    }

    @PreAuthorize("hasAnyRole('RECIPE_CREATOR', 'ADMIN', 'PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(path = "{id}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> updateRecipe(@Valid @RequestPart("recipe") RecipeDto recipeDto,
                                   @RequestPart(value = "image", required = false) MultipartFile file,
                                   @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
                                   @PathVariable("id") long id, HttpServletRequest request) throws IOException {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        recipeDto.setId(id);
        Recipe recipe = recipeService.fromDto(recipeDto);
        Recipe oldRecipe = recipeService.getById(id);
        if (oldRecipe == null) {
            return ResponseEntity.notFound().build();
        }
        recipe.setOwner(oldRecipe.getOwner());
        if (recipe.getOwner().getId() != principal.getId() && !principal.getAuthorities().contains(ERole.ROLE_ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        recipeService.updateRecipe(recipe);
        if (removeImage) {
            recipeService.setImage(recipe.getId(), null);
        } else if (file != null) {
            BufferedImage image;
            try {
                image = ImageIO.read(file.getInputStream());
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid image format!");
            }
            image = ImageUtils.resizeImage(image, 1000, 16d / 9);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", out);
            recipeService.setImage(recipe.getId(), out.toByteArray());
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "{id}/image", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<?> getRecipeImage(@PathVariable("id") long id) {
        byte[] image = recipeService.getImage(id);
        if(image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(image);
    }

    @PreAuthorize("hasAnyRole('RECIPE_CREATOR', 'ADMIN', 'PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    ResponseEntity<?> deleteRecipe(@PathVariable("id") long id, HttpServletRequest request) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.getById(id);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        if (recipe.getOwner().getId() != principal.getId() && !principal.getAuthorities().contains(ERole.ROLE_ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        recipeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
