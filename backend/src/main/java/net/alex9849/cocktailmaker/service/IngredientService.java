package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.payload.dto.recipe.IngredientDto;
import net.alex9849.cocktailmaker.repository.IngredientRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
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

    public Ingredient fromDto(IngredientDto ingredientDto) {
        if(ingredientDto == null) {
            return null;
        }
        Ingredient ingredient = new Ingredient();
        BeanUtils.copyProperties(ingredientDto, ingredient);
        return ingredient;
    }

    public Ingredient updateIngredient(Ingredient ingredient) {
        if(!ingredientRepository.findById(ingredient.getId()).isPresent()) {
            throw new IllegalArgumentException("Ingredient doesn't exist!");
        }
        Optional<Ingredient> optionalIngredient = ingredientRepository.findByName(ingredient.getName());
        if(optionalIngredient.isPresent() && !Objects.equals(optionalIngredient.get().getId(), ingredient.getId())) {
            throw new IllegalArgumentException("An ingredient with this name already exists!");
        }
        return ingredientRepository.save(ingredient);
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        ingredient.setId(null);
        Optional<Ingredient> optionalIngredient = ingredientRepository.findByName(ingredient.getName());
        if(optionalIngredient.isPresent()) {
            throw new IllegalArgumentException("An ingredient with this name already exists!");
        }
        return ingredientRepository.save(ingredient);
    }

    public void deleteIngredient(Ingredient ingredient) {
        ingredientRepository.deleteById(ingredient.getId());
    }
}
