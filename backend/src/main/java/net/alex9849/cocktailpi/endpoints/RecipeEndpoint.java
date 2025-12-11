package net.alex9849.cocktailpi.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailpi.model.recipe.IngredientRecipe;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.recipe.IngredientRecipeDto;
import net.alex9849.cocktailpi.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailpi.service.*;
import net.alex9849.cocktailpi.utils.ImageUtils;
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
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipe/")
public class RecipeEndpoint {

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserService userService;

    @Autowired
    IngredientService ingredientService;

    @Autowired
    SystemService systemService;


    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseEntity<?> getRecipesByFilter(@RequestParam(value = "ownerId", required = false) Long ownerId,
                                         @RequestParam(value = "inCollection", required = false) Long inCollectionId,
                                         @RequestParam(value = "fabricable", defaultValue = "all") String fabricable,
                                         @RequestParam(value = "containsIngredients", required = false) Long[] containsIngredients,
                                         @RequestParam(value = "searchName", required = false) String searchName,
                                         @RequestParam(value = "inCategory", required = false) Long inCategory,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {
        final int pageSize = systemService.getAppearance().getRecipePageSize();
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
                sort = Sort.by(Sort.Direction.DESC, "lower(name)");
                break;
            default:
                sort = Sort.by(Sort.Direction.ASC, "lower(name)");
                break;
        }
        RecipeService.FabricableFilter fabricableFilter = switch (fabricable) {
            case "manual" -> RecipeService.FabricableFilter.IN_BAR;
            case "auto" -> RecipeService.FabricableFilter.AUTOMATICALLY;
            default -> RecipeService.FabricableFilter.ALL;
        };
        Page<Recipe> recipePage = recipeService.getRecipesByFilter(ownerId,
                inCollectionId, inCategory, containsIngredients, searchName, fabricableFilter,
                page, pageSize, sort);
        List<RecipeDto.Response.SearchResult> recipeDtos = recipePage.stream().map(RecipeDto.Response.SearchResult::toDto).collect(Collectors.toList());
        return ResponseEntity.ok().body(new PageImpl<>(recipeDtos, recipePage.getPageable(), recipePage.getTotalElements()));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getRecipe(@PathVariable("id") long id,
                                @RequestParam(value = "isIngredient", defaultValue = "false") boolean isIngredient) {
        Recipe recipe;
        if(isIngredient) {
            recipe = recipeService.getIngredientRecipe(id);
        } else {
            recipe = recipeService.getById(id);
        }
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(RecipeDto.Response.Detailed.toDto(recipe));
    }

    @RequestMapping(path = "ingredient/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getIngredientRecipe(@PathVariable("id") long id) {
        IngredientRecipe recipe = recipeService.getIngredientRecipe(id);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(RecipeDto.Response.Detailed.toDto(recipe));
    }

    @RequestMapping(path = "ingredient", method = RequestMethod.GET)
    public ResponseEntity<?> getIngredientRecipes() {
        List<IngredientRecipe> recipes = recipeService.getCurrentIngredientRecipes();
        return ResponseEntity.ok(recipes.stream().map(IngredientRecipeDto.Response.SearchResult::toDto).toList());
    }

    @PreAuthorize("hasAuthority('RECIPE_CREATOR')")
    @RequestMapping(path = "", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRecipe(@Valid @RequestPart("recipe") RecipeDto.Request.Create recipeDto,
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
            image = ImageUtils.resizeImage(image, 2000, 16d / 9);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", out);
            recipeService.setImage(recipe.getId(), out.toByteArray());
        }
        UriComponents uriComponents = uriBuilder.path("/api/recipe/{id}").buildAndExpand(recipe.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(RecipeDto.Response.Detailed.toDto(recipe));
    }

    @PreAuthorize("hasAuthority('RECIPE_CREATOR')")
    @RequestMapping(path = "{id}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRecipe(@Valid @RequestPart("recipe") RecipeDto.Request.Create recipeDto,
                                   @RequestPart(value = "image", required = false) MultipartFile file,
                                   @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
                                   @PathVariable("id") long id) throws IOException {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.fromDto(recipeDto);
        recipe.setId(id);
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
            image = ImageUtils.resizeImage(image, 2000, 16d / 9);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", out);
            recipeService.setImage(recipe.getId(), out.toByteArray());
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "{id}/image", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getRecipeImage(@PathVariable("id") long id,
                                     @RequestParam(value = "isIngredient", defaultValue = "false") boolean isIngredient,
                                     @RequestParam(value = "width", defaultValue = "-1") int width) throws IOException {
        byte[] image;
        if(isIngredient) {
            image = ingredientService.getImage(id);
        } else {
            image = recipeService.getImage(id);
        }
        if(image == null) {
            return ResponseEntity.notFound().build();
        }
        if (width > 0) {
            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(image));
            bImage = ImageUtils.resizeImage(bImage, width, 16d / 9);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", out);
            image = out.toByteArray();
        }
        return ResponseEntity.ok(image);
    }

    @PreAuthorize("hasAnyRole('RECIPE_CREATOR')")
    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteRecipe(@PathVariable("id") long id) {
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
