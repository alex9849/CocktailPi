package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.security.services.UserDetailsImpl;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipe")
public class RecipeEndpoint {

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserService userService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    ResponseEntity<?> getRecipesByFilter(@RequestParam(value = "ownerId", required = false) Long ownerId,
            @RequestParam(value = "inPublic", required = false) Boolean inPublic,
            @RequestParam(value = "searchName", required = false) String searchName,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!userDetails.getId().equals(ownerId) && (inPublic == null || !inPublic)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final int pageSize = 10;
        page = Math.max(--page, 0);
        Sort sort;
        switch (orderBy){
            default:
                sort = Sort.by(Sort.Direction.ASC, "name");
                break;
        }
        Page<Recipe> recipePage = recipeService.getRecipesByFilter(ownerId, inPublic, searchName, page, pageSize, sort);
        List<RecipeDto> recipeDtos = recipePage.stream().map(RecipeDto::new).collect(Collectors.toList());
        return ResponseEntity.ok().body(new PageImpl<>(recipeDtos, recipePage.getPageable(), recipePage.getTotalElements()));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    ResponseEntity<?> getRecipe(@PathVariable("id") long id, HttpServletRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.getById(id);
        if(recipe == null) {
            return ResponseEntity.notFound().build();
        }
        if(!recipe.getOwner().getId().equals(userDetails.getId()) && !recipe.isInPublic()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new RecipeDto(recipe));
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    ResponseEntity<?> createRecipe(@Valid @RequestBody RecipeDto recipeDto, UriComponentsBuilder uriBuilder) {
        recipeDto.setId(null);
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.fromDto(recipeDto);
        recipe.setOwner(userService.getUser(userDetails.getId()));
        recipe = recipeService.createRecipe(recipe);
        UriComponents uriComponents = uriBuilder.path("/api/recipe/{id}").buildAndExpand(recipe.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(new RecipeDto(recipe));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> updateRecipe(@Valid @RequestPart("recipe") RecipeDto recipeDto, @RequestPart(value = "image", required = false) MultipartFile file, @PathVariable("id") long id, HttpServletRequest request) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        recipeDto.setId(id);
        Recipe recipe = recipeService.fromDto(recipeDto);
        Recipe oldRecipe = recipeService.getById(id);
        if(oldRecipe == null) {
            return ResponseEntity.notFound().build();
        }
        recipe.setOwner(oldRecipe.getOwner());
        if(!recipe.getOwner().getId().equals(userDetails.getId()) && !request.isUserInRole("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(file != null) {
            BufferedImage image;
            try {
                image = ImageIO.read(file.getInputStream());
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid image format!");
            }
            image = ImageUtils.resizeImage(image, 500, 281);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", out);
            recipe.setImage(out.toByteArray());
        } else {
            recipe.setImage(oldRecipe.getImage());
        }

        recipeService.updateRecipe(recipe);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "{id}/image", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<?> getRecipeImage(@PathVariable("id") long id) {
        Recipe recipe = recipeService.getById(id);
        if(recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe.getImage());
    }

    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    ResponseEntity<?> deleteRecipe(@PathVariable("id") long id, HttpServletRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeService.getById(id);
        if(recipe == null) {
            return ResponseEntity.notFound().build();
        }
        if(!recipe.getOwner().getId().equals(userDetails.getId()) && !request.isUserInRole("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        recipeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
