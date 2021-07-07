package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ManualIngredient;
import net.alex9849.cocktailmaker.payload.dto.recipe.IngredientDto;
import net.alex9849.cocktailmaker.repository.IngredientRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private PumpService pumpService;

    public Ingredient getIngredient(long id) {
        return ingredientRepository.findById(id).orElse(null);
    }

    public List<Ingredient> getIngredientByFilter(String nameStartsWith) {
        Specification<Ingredient> spec = new Ingredient.IngredientFilterNoFilter();
        if(nameStartsWith != null) {
            spec = spec.and(new Ingredient.IngredientFilterNameIncludes(nameStartsWith, true));
        }
        return ingredientRepository.findAll(Specification.where(spec));
    }

    public Ingredient fromDto(IngredientDto ingredientDto) {
        if(ingredientDto == null) {
            return null;
        }
        Ingredient ingredient = new AutomatedIngredient();
        BeanUtils.copyProperties(ingredientDto, ingredient);
        return ingredient;
    }

    public Ingredient updateIngredient(Ingredient ingredient) {
        if(!ingredientRepository.findById(ingredient.getId()).isPresent()) {
            throw new IllegalArgumentException("Ingredient doesn't exist!");
        }
        Optional<Ingredient> optionalIngredient = ingredientRepository.findByNameIgnoringCase(ingredient.getName());
        if(optionalIngredient.isPresent() && !Objects.equals(optionalIngredient.get().getId(), ingredient.getId())) {
            throw new IllegalArgumentException("An ingredient with this name already exists!");
        }
        return ingredientRepository.save(ingredient);
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        ingredient.setId(null);
        Optional<Ingredient> optionalIngredient = ingredientRepository.findByNameIgnoringCase(ingredient.getName());
        if(optionalIngredient.isPresent()) {
            throw new IllegalArgumentException("An ingredient with this name already exists!");
        }
        return ingredientRepository.save(ingredient);
    }

    public void deleteIngredient(Ingredient ingredient) {
        List<Pump> pumpsWithIngredient = pumpService.getPumpsWithIngredient(Arrays.asList(ingredient.getId()));
        pumpsWithIngredient.forEach(x -> {
            x.setCurrentIngredient(null);
            pumpService.updatePump(x);
        });
        ingredientRepository.deleteById(ingredient.getId());
    }
}
