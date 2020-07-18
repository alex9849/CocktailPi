package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    Ingredient getIngredient(long id) {
        return ingredientRepository.findById(id).orElse(null);
    }

}
