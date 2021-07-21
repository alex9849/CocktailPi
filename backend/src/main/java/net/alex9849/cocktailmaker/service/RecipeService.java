package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeIngredientDto;
import net.alex9849.cocktailmaker.repository.RecipeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
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

    public Page<Recipe> getRecipesByFilter(Long ownerId, Boolean inPublic, Long inCategory, Long[] containsIngredients, String searchName, boolean isFabricable, boolean isInBar, int startNumber, int pageSize, Sort sort) {
        List<Set<Long>> idsToFind = new ArrayList<>();

        if(inPublic != null) {
            idsToFind.add(recipeRepository.getIdsInPublic());
        }
        if(inCategory != null) {
            idsToFind.add(recipeRepository.getIdsInCategory(inCategory));
        }
        if(ownerId != null) {
            idsToFind.add(recipeRepository.getIdsByOwnerId(ownerId));
        }
        if(containsIngredients != null) {
            idsToFind.add(recipeRepository.getIdsWithIngredients(containsIngredients));
        }
        if(searchName != null) {
            idsToFind.add(recipeRepository.getIdsContainingName(searchName));
        }
        if(isFabricable) {
            //TODO
            //spec = spec.and(new Recipe.RecipeFilterCurrentlyMakeable());
        }
        if(isInBar) {
            //TODO
            //spec = spec.and(new Recipe.RecipeFilterInBar());
        }
        Set<Long> retained = null;
        for(Set<Long> current : idsToFind) {
            if(retained == null) {
                retained = current;
                continue;
            }
            retained.retainAll(current);
        }
        if(retained.isEmpty()) {
            return new PageImpl<>(Collections.emptyList());
        }
        return new PageImpl<>(recipeRepository.findByIds(startNumber, pageSize, sort, retained.toArray(new Long[1])));
    }

    public Recipe getById(long id) {
        return recipeRepository.findById(id).orElse(null);
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
        recipeIngredient.setAmount(productionStep);
        return recipeIngredient;
    }
}
