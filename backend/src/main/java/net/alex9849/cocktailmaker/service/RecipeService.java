package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredientId;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeIngredientDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecipeService {

    @Autowired
    UserService userService;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    IngredientService ingredientService;

    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    CategoryService categoryService;

    @Autowired
    EntityManager entityManager;

    public Recipe createRecipe(Recipe recipe) {
        if(userService.getUser(recipe.getOwner().getId()) == null) {
            throw new IllegalArgumentException("User doesn't exist!");
        }
        for(RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            recipeIngredient.getId().setRecipeId(null);
            if(!Objects.equals(recipeIngredient.getId().getIngredientId(), recipeIngredient.getIngredient().getId())) {
                throw new IllegalArgumentException("Malformed RecipeIngredient!");
            }
            recipeIngredient.setIngredient(entityManager.find(Ingredient.class, recipeIngredient.getIngredient().getId()));
        }

        return recipeRepository.save(recipe);
    }

    public Page<Recipe> getRecipesByFilter(Long ownerId, Boolean inPublic, Long inCategory, Long[] containsIngredients, String searchName, boolean isFabricable, boolean isInBar, Integer startNumber, Integer pageSize, Sort sort) {
        Specification<Recipe> spec = new Recipe.RecipeFilterNoFilter();

        if(inPublic != null) {
            spec = spec.and(new Recipe.RecipeFilterPublic(inPublic));
        }
        if(inCategory != null) {
            spec = spec.and(new Recipe.RecipeFilterCategory(inCategory));
        }
        if(ownerId != null) {
            spec = spec.and(new Recipe.RecipeFilterOwnerId(ownerId));
        }
        if(containsIngredients != null) {
            spec = spec.and(new Recipe.RecipeFilterContainsIngredients(containsIngredients));
        }
        if(searchName != null) {
            spec = spec.and(new Recipe.RecipeFilterNameContain(searchName));
        }
        if(isFabricable) {
            spec = spec.and(new Recipe.RecipeFilterCurrentlyMakeable());
        }
        if(isInBar) {
            spec = spec.and(new Recipe.RecipeFilterInBar());
        }
        if(startNumber != null && pageSize != null) {
            return recipeRepository.findAll(spec, PageRequest.of(startNumber, pageSize, sort));
        }
        return new PageImpl<>(recipeRepository.findAll(spec, sort));
    }

    public Recipe getById(long id) {
        return recipeRepository.findById(id).orElse(null);
    }

    public Recipe updateRecipe(Recipe recipe) {
        if(!recipeRepository.findById(recipe.getId()).isPresent()) {
            throw new IllegalArgumentException("Recipe doesn't exist!");
        }
        if(recipe.getOwner() != null) {
            recipe.setOwner(userService.getUser(recipe.getOwner().getId()));
        }
        for(RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            if(!Objects.equals(recipeIngredient.getId().getIngredientId(), recipeIngredient.getIngredient().getId())) {
                throw new IllegalArgumentException("Malformed RecipeIngredient!");
            }
            recipeIngredient.setIngredient(entityManager.find(Ingredient.class, recipeIngredient.getIngredient().getId()));
        }
        recipeIngredientRepository.deleteAllByRecipeId(recipe.getId());
        for(RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            entityManager.merge(recipeIngredient);
        }
        return recipeRepository.save(recipe);
    }

    public void delete(long recipeId) {
        recipeRepository.deleteById(recipeId);
    }

    public Recipe fromDto(RecipeDto recipeDto) {
        if(recipeDto == null) {
            return null;
        }
        Recipe recipe = new Recipe();
        BeanUtils.copyProperties(recipeDto, recipe);
        recipe.setOwner(userService.fromDto(recipeDto.getOwner()));
        AtomicInteger index = new AtomicInteger();
        recipe.setRecipeIngredients(recipeDto
                .getRecipeIngredients().stream()
                        .flatMap(x -> {
                            index.incrementAndGet();
                            return x.stream().map(y -> fromDto(y, recipe, index.get()));
                        }).collect(Collectors.toList()));
        recipe.setCategories(recipeDto.getCategories()
                .stream().map(x -> categoryService.fromDto(x))
                .collect(Collectors.toList()));
        return recipe;
    }

    public RecipeIngredient fromDto(RecipeIngredientDto recipeIngredientDto, Recipe recipe, int productionStep) {
        if(recipeIngredientDto == null) {
            return null;
        }
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        BeanUtils.copyProperties(recipeIngredientDto, recipeIngredient);
        recipeIngredient.setIngredient(ingredientService.fromDto(recipeIngredientDto.getIngredient()));
        recipeIngredient.setRecipe(recipe);
        RecipeIngredientId recipeIngredientId = new RecipeIngredientId();
        recipeIngredientId.setIngredientId(recipeIngredientDto.getIngredient().getId());
        recipeIngredientId.setRecipeId(recipe.getId());
        recipeIngredientId.setProductionStep(productionStep);
        recipeIngredient.setId(recipeIngredientId);
        return recipeIngredient;
    }
}
