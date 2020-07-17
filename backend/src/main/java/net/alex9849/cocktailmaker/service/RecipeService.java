package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.repository.IngredientRepository;
import net.alex9849.cocktailmaker.repository.RecipeRepository;
import net.alex9849.cocktailmaker.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    TagRepository tagRepository;

    public List<Recipe> getAll() {
        return recipeRepository.findAll();
    }
}
