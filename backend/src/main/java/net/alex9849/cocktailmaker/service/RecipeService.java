package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeIngredientDto;
import net.alex9849.cocktailmaker.repository.RecipeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
    CategoryService categoryService;

    public Recipe createRecipe(Recipe recipe) {
        if(userService.getUser(recipe.getOwner().getId()) == null) {
            throw new IllegalArgumentException("User doesn't exist!");
        }
        return recipeRepository.create(recipe);
    }

    public Page<Recipe> getRecipesByFilter(Long ownerId, Boolean inPublic, Long inCategory, Long[] containsIngredients, String searchName, boolean isFabricable, Long isInBarUserId, int startNumber, int pageSize, Sort sort) {
        List<Set<Long>> idsToFindSetList = new ArrayList<>();

        if(inPublic != null) {
            idsToFindSetList.add(recipeRepository.getIdsInPublic());
        }
        if(inCategory != null) {
            idsToFindSetList.add(recipeRepository.getIdsInCategory(inCategory));
        }
        if(ownerId != null) {
            idsToFindSetList.add(recipeRepository.getIdsByOwnerId(ownerId));
        }
        if(containsIngredients != null) {
            idsToFindSetList.add(recipeRepository.getIdsWithIngredients(containsIngredients));
        }
        if(searchName != null) {
            idsToFindSetList.add(recipeRepository.getIdsContainingName(searchName));
        }
        if(isFabricable) {
            idsToFindSetList.add(recipeRepository.getIdsOfFullyAutomaticallyFabricableRecipes());
        }
        if(isInBarUserId != null) {
            idsToFindSetList.add(recipeRepository.getIdsOfRecipesWithAllIngredientsOwnedOrOnPumps(isInBarUserId));
        }
        Pageable pageable = PageRequest.of((startNumber / pageSize) + 1, pageSize);
        if(idsToFindSetList.isEmpty()) {
            return new PageImpl<>(recipeRepository.findAll(startNumber, pageSize, sort), pageable, recipeRepository.count());
        }
        Set<Long> retained = null;
        for(Set<Long> current : idsToFindSetList) {
            if(retained == null) {
                retained = current;
                continue;
            }
            retained.retainAll(current);
        }
        if(retained.isEmpty()) {
            return new PageImpl<>(Collections.emptyList());
        }
        return new PageImpl<>(recipeRepository.findByIds(startNumber, pageSize, sort, retained.toArray(new Long[1])), pageable, recipeRepository.count());
    }

    public Recipe getById(long id) {
        return recipeRepository.findById(id).orElse(null);
    }

    public byte[] getImage(long recipeId) {
        return recipeRepository.getImage(recipeId).orElse(null);
    }

    public void setImage(long recipeId, byte[] image) {
        recipeRepository.setImage(recipeId, image);
    }

    public Recipe updateRecipe(Recipe recipe) {
        if(!recipeRepository.findById(recipe.getId()).isPresent()) {
            throw new IllegalArgumentException("Recipe doesn't exist!");
        }
        recipeRepository.update(recipe);
        return recipe;
    }

    public void delete(long recipeId) {
        recipeRepository.delete(recipeId);
    }

    public Recipe fromDto(RecipeDto recipeDto) {
        if(recipeDto == null) {
            return null;
        }
        Recipe recipe = new Recipe();
        BeanUtils.copyProperties(recipeDto, recipe);
        recipe.setOwner(userService.fromDto(recipeDto.getOwner()));
        if(recipe.getOwner() != null) {
            recipe.setOwnerId(recipe.getOwner().getId());
        }
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
        recipeIngredient.setIngredientId(recipeIngredientDto.getIngredient().getId());
        recipeIngredient.setRecipeId(recipe.getId());
        recipeIngredient.setProductionStep(productionStep);
        return recipeIngredient;
    }
}
