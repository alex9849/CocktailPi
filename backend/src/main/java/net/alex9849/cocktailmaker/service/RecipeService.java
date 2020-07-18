package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.recipe.*;
import net.alex9849.cocktailmaker.payload.dto.recipe.IngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeIngredientDto;
import net.alex9849.cocktailmaker.repository.RecipeRepository;
import net.alex9849.cocktailmaker.repository.TagRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
    TagRepository tagRepository;

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
            /*Ingredient ingredient = ingredientService.getIngredient(recipeIngredient.getIngredient().getId());
            if(ingredient  == null) {
                throw new IllegalArgumentException("Ingredient doesn't exist!");
            }
            recipeIngredient.setIngredient(ingredient);*/
            recipeIngredient.setIngredient(entityManager.find(Ingredient.class, recipeIngredient.getIngredient()));
        }

        return recipeRepository.save(recipe);
    }

    public List<Recipe> getAll() {
        return recipeRepository.findAll();
    }

    public List<Recipe> getByOwner(long ownerId) {
        return recipeRepository.findAllByOwnerId(ownerId);
    }

    public Recipe getById(long id) {
        return recipeRepository.findById(id).orElse(null);
    }

    public void delete(long recipeId) {
        recipeRepository.deleteById(recipeId);
    }

    public Set<Tag> toTags(Set<String> stringTags) {
        Set<Tag> tags = new HashSet<>();
        stringTags.forEach(x -> {
            Tag tag = tagRepository.findByName(x)
                    .orElseThrow(() -> new RuntimeException("Error: Tag is not found."));
            tags.add(tag);
        });
        return tags;
    }

    public Recipe fromDto(RecipeDto recipeDto) {
        if(recipeDto == null) {
            return null;
        }
        Recipe recipe = new Recipe();
        BeanUtils.copyProperties(recipeDto, recipe);
        recipe.setOwner(userService.fromDto(recipeDto.getOwner()));
        recipe.setRecipeIngredients(recipeDto
                .getRecipeIngredients().stream()
                .map(x -> fromDto(x, recipe)).collect(Collectors.toSet()));
        recipe.setTags(toTags(recipeDto.getTags()));
        return recipe;
    }

    public RecipeIngredient fromDto(RecipeIngredientDto recipeIngredientDto, Recipe recipe) {
        if(recipeIngredientDto == null) {
            return null;
        }
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        BeanUtils.copyProperties(recipeIngredientDto, recipeIngredient);
        recipeIngredient.setIngredient(fromDto(recipeIngredientDto.getIngredient()));
        recipeIngredient.setRecipe(recipe);
        RecipeIngredientId recipeIngredientId = new RecipeIngredientId();
        recipeIngredientId.setIngredientId(recipeIngredientDto.getIngredient().getId());
        recipeIngredientId.setRecipeId(recipe.getId());
        recipeIngredient.setId(recipeIngredientId);
        return recipeIngredient;
    }

    public Ingredient fromDto(IngredientDto ingredientDto) {
        if(ingredientDto == null) {
            return null;
        }
        Ingredient ingredient = new Ingredient();
        BeanUtils.copyProperties(ingredientDto, ingredient);
        return ingredient;
    }
}
