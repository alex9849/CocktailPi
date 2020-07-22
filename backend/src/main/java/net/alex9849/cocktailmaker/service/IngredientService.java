package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    public Ingredient getIngredient(long id) {
        return ingredientRepository.findById(id).orElse(null);
    }

    public List<Ingredient> getIngredientByFilter(String nameStartsWith) {
        Specification<Ingredient> spec = new Ingredient.IngredientFilterNoFilter();
        if(nameStartsWith != null) {
            spec = spec.and(new Ingredient.IngredientFilterStartsWith(nameStartsWith, true));
        }
        return ingredientRepository.findAll(Specification.where(spec));
    }

}
