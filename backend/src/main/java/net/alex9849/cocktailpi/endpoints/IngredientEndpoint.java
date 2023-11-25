package net.alex9849.cocktailpi.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.IngredientDto;
import net.alex9849.cocktailpi.service.IngredientService;
import net.alex9849.cocktailpi.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ingredient/")
public class IngredientEndpoint {

    @Autowired
    IngredientService ingredientService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    ResponseEntity<?> getIngredients(@RequestParam(value = "autocomplete", required = false) String autocomplete,
                                     @RequestParam(value = "filterManualIngredients", defaultValue = "false") boolean filterManualIngredients,
                                     @RequestParam(value = "filterAutomaticIngredients", defaultValue = "false") boolean filterAutomaticIngredients,
                                     @RequestParam(value = "filterIngredientGroups", defaultValue = "false") boolean filterIngredientGroups,
                                     @RequestParam(value = "groupChildrenGroupId", required = false) Long groupChildrenGroupId,
                                     @RequestParam(value = "inBar", defaultValue = "false") boolean inBar,
                                     @RequestParam(value = "onPump", defaultValue = "false") boolean onPump,
                                     @RequestParam(value = "inBarOrOnPump", defaultValue = "false") boolean inBarOrOnPump) {
        boolean skipAutoMinAutocompleteCheck = false;
        skipAutoMinAutocompleteCheck |= inBarOrOnPump;
        skipAutoMinAutocompleteCheck |= inBar;
        skipAutoMinAutocompleteCheck |= onPump;

        if(!skipAutoMinAutocompleteCheck) {
            if(autocomplete == null){
                if(!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(ERole.ROLE_ADMIN)) {
                    ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            } else {
                if(autocomplete.length() < 2) {
                    throw new IllegalArgumentException("Autocomplete too short");
                }
            }
        }
        return ResponseEntity.ok(ingredientService.getIngredientByFilter(autocomplete, filterManualIngredients,
                filterAutomaticIngredients, filterIngredientGroups, groupChildrenGroupId, inBar, onPump,
                        inBarOrOnPump, false)
                .stream().map(IngredientDto.Response.Detailed::toDto).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<?> createIngredient(@Valid @RequestPart("ingredient") IngredientDto.Request.Create ingredientDto,
                                       @RequestPart(value = "image", required = false) MultipartFile file,
                                       UriComponentsBuilder uriBuilder) throws IOException {
        Ingredient ingredient = ingredientService.fromDto(ingredientDto);
        ingredient = ingredientService.createIngredient(ingredient);
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
            ingredientService.setImage(ingredient.getId(), out.toByteArray());
        }
        UriComponents uriComponents = uriBuilder.path("/api/ingredient/{id}").buildAndExpand(ingredient.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(IngredientDto.Response.Detailed.toDto(ingredient));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    ResponseEntity<?> updateIngredient(@PathVariable("id") long id,
                                       @Valid @RequestPart("ingredient") IngredientDto.Request.Create ingredientDto,
                                       @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
                                       @RequestPart(value = "image", required = false) MultipartFile file) throws IOException {
        if(ingredientService.getIngredient(id) == null) {
            return ResponseEntity.notFound().build();
        }
        Ingredient ingredient = ingredientService.fromDto(ingredientDto);
        ingredient.setId(id);
        ingredientService.updateIngredient(ingredient);
        if (removeImage) {
            ingredientService.setImage(ingredient.getId(), null);
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
            ingredientService.setImage(ingredient.getId(), out.toByteArray());
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "{id}/image", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<?> getRecipeImage(@PathVariable("id") long id) {
        byte[] image = ingredientService.getImage(id);
        if(image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(image);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    ResponseEntity<?> deleteIngredient(@PathVariable("id") long id) {
        if(!ingredientService.deleteIngredient(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "export", method = RequestMethod.GET)
    ResponseEntity<?> getIngredientExport() {
        List<Ingredient> ingredients = ingredientService.getIngredientsInExportOrder();
        return ResponseEntity.ok(ingredients.stream().map(IngredientDto.Response.Detailed::toDto)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "{id}/bar", method = RequestMethod.PUT)
    ResponseEntity<?> addToBar(@PathVariable("id") long id) {
        ingredientService.setInBar(id, true);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "{id}/bar", method = RequestMethod.DELETE)
    ResponseEntity<?> removeFromBar(@PathVariable("id") long id) {
        ingredientService.setInBar(id, false);
        return ResponseEntity.ok().build();
    }

}
