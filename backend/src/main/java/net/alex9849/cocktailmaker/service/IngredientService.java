package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ManualIngredient;
import net.alex9849.cocktailmaker.payload.dto.recipe.AutomatedIngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.IngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.ManualIngredientDto;
import net.alex9849.cocktailmaker.repository.IngredientRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;

@Service
@Transactional
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private PumpService pumpService;

    @Autowired
    private EntityManager entityManager;

    public Ingredient getIngredient(long id) {
        return ingredientRepository.findById(id).orElse(null);
    }

    public List<Ingredient> getIngredientByFilter(String nameStartsWith) {
        List<Set<Long>> idsToFindSetList = new ArrayList<>();

        if(nameStartsWith != null) {
            idsToFindSetList.add(ingredientRepository.findIdsAutocompleteName(nameStartsWith));
        }

        if(idsToFindSetList.isEmpty()) {
            return ingredientRepository.findAll();
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
            return Collections.emptyList();
        }
        return ingredientRepository.findByIds(retained.toArray(new Long[1]));
    }

    public List<Ingredient> getIngredientsOwnedByUSer(long userId) {
        return ingredientRepository.findByIds(ingredientRepository
                .findIdsOwnedByUser(userId).toArray(new Long[1]));
    }

    public Ingredient fromDto(IngredientDto ingredientDto) {
        if(ingredientDto == null) {
            return null;
        }
        if(ingredientDto instanceof ManualIngredientDto) {
            return fromDto((ManualIngredientDto) ingredientDto);
        }
        if(ingredientDto instanceof AutomatedIngredientDto) {
            return fromDto((AutomatedIngredientDto) ingredientDto);
        }
        throw new IllegalStateException("IngredientType not supported yet!");
    }

    public AutomatedIngredient fromDto(AutomatedIngredientDto ingredientDto) {
        AutomatedIngredient ingredient = new AutomatedIngredient();
        BeanUtils.copyProperties(ingredientDto, ingredient);
        return ingredient;
    }

    public ManualIngredient fromDto(ManualIngredientDto ingredientDto) {
        ManualIngredient ingredient = new ManualIngredient();
        BeanUtils.copyProperties(ingredientDto, ingredient);
        return ingredient;
    }

    public Ingredient updateIngredient(Ingredient ingredient) {
        if(!ingredientRepository.findById(ingredient.getId()).isPresent()) {
            throw new IllegalArgumentException("Ingredient doesn't exist!");
        }
        Optional<Ingredient> optionalIngredient = ingredientRepository.findByNameIgnoringCase(ingredient.getName());
        if(optionalIngredient.isPresent()
                && !Objects.equals(optionalIngredient.get().getId(), ingredient.getId())) {
            throw new IllegalArgumentException("An ingredient with this name already exists!");
        }
        ingredientRepository.update(ingredient);
        return ingredient;
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        Optional<Ingredient> optionalIngredient = ingredientRepository.findByNameIgnoringCase(ingredient.getName());
        if(optionalIngredient.isPresent()) {
            throw new IllegalArgumentException("An ingredient with this name already exists!");
        }
        return ingredientRepository.create(ingredient);
    }

    public boolean deleteIngredient(long id) {
        return ingredientRepository.delete(id);
    }
}
