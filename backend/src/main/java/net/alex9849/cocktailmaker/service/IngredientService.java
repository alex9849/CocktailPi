package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.recipe.*;
import net.alex9849.cocktailmaker.payload.dto.recipe.AutomatedIngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.IngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.ManualIngredientDto;
import net.alex9849.cocktailmaker.repository.IngredientRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    public Ingredient getIngredient(long id) {
        return ingredientRepository.findById(id).orElse(null);
    }

    public List<Ingredient> getIngredientByFilter(String nameStartsWith, boolean filterManualIngredients, boolean inBar) {
        List<Set<Long>> idsToFindSetList = new ArrayList<>();

        if(nameStartsWith != null) {
            idsToFindSetList.add(ingredientRepository.findIdsAutocompleteName(nameStartsWith));
        }
        if(filterManualIngredients) {
            idsToFindSetList.add(ingredientRepository.findIdsNotManual());
        }
        if(inBar) {
            idsToFindSetList.add(ingredientRepository.findAddableIngredientsIdsInBar());
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

    public List<Ingredient> getIngredientsInBar(long userId) {
        return ingredientRepository.findByIds(ingredientRepository
                .findAddableIngredientsIdsInBar().toArray(new Long[1]));
    }

    public static Ingredient fromDto(IngredientDto ingredientDto) {
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

    public static AutomatedIngredient fromDto(AutomatedIngredientDto ingredientDto) {
        if(ingredientDto == null) {
            return null;
        }
        AutomatedIngredient ingredient = new AutomatedIngredient();
        BeanUtils.copyProperties(ingredientDto, ingredient);
        return ingredient;
    }

    public static ManualIngredient fromDto(ManualIngredientDto ingredientDto) {
        if(ingredientDto == null) {
            return null;
        }
        ManualIngredient ingredient = new ManualIngredient();
        BeanUtils.copyProperties(ingredientDto, ingredient);
        return ingredient;
    }

    public void setInBar(long id, boolean inBar) {
        Ingredient ingredient = ingredientRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient doesn't exist!"));

        if(!(ingredient instanceof AddableIngredient)) {
            throw new IllegalArgumentException("Ingredient needs to be an ManualIngredient or AutomatedIngredient!");
        }
        AddableIngredient aIngredient = (AddableIngredient) ingredient;
        aIngredient.setInBar(inBar);
        ingredientRepository.update(aIngredient);
    }

    public Ingredient updateIngredient(Ingredient ingredient) {
        if(ingredientRepository.findById(ingredient.getId()).isEmpty()) {
            throw new IllegalArgumentException("Ingredient doesn't exist!");
        }
        Optional<Ingredient> optionalIngredient = ingredientRepository.findByNameIgnoringCase(ingredient.getName());
        if(optionalIngredient.isPresent()) {
            if(!Objects.equals(optionalIngredient.get().getId(), ingredient.getId())) {
                throw new IllegalArgumentException("An ingredient with this name already exists!");
            }
            if(ingredient instanceof AddableIngredient) {
                ((AddableIngredient) ingredient).setInBar(optionalIngredient.get().isInBar());
            }
        }
        ingredientRepository.update(ingredient);
        return ingredient;
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        Optional<Ingredient> optionalIngredient = ingredientRepository.findByNameIgnoringCase(ingredient.getName());
        if(optionalIngredient.isPresent()) {
            throw new IllegalArgumentException("An ingredient with this name already exists!");
        }
        if(ingredient instanceof AddableIngredient) {
            ((AddableIngredient) ingredient).setInBar(false);
        }
        return ingredientRepository.create(ingredient);
    }

    public boolean deleteIngredient(long id) {
        return ingredientRepository.delete(id);
    }

    public Set<Ingredient> getGroupChildren(long id) {
        return ingredientRepository.findGroupChildren(id);
    }
}
