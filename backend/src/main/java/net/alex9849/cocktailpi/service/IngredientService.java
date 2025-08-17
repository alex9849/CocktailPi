package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.recipe.ingredient.*;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.AutomatedIngredientDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.IngredientDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.IngredientGroupDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.ManualIngredientDto;
import net.alex9849.cocktailpi.repository.IngredientRepository;
import net.alex9849.cocktailpi.service.pumps.PumpDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private PumpDataService pumpService;

    @Autowired
    private WebSocketService webSocketService;

    public Ingredient getIngredient(long id) {
        return ingredientRepository.findById(id).orElse(null);
    }

    public Ingredient fromDto(IngredientDto.Request.Create ingredientDto) {
        if(ingredientDto == null) {
            return null;
        }
        Long parentGroupId =  ingredientDto.getParentGroupId();
        IngredientGroup parentGroup = null;

        if(parentGroupId != null) {
            Ingredient parentIngredient = getIngredient(parentGroupId);
            if(!(parentIngredient instanceof IngredientGroup)) {
                throw new IllegalArgumentException("parentGroup has to be a IngredientGroup");
            }
            parentGroup = (IngredientGroup) parentIngredient;
        }

        Ingredient ingredient;
        if(ingredientDto instanceof ManualIngredientDto.Request.Create) {
            ingredient = new ManualIngredient();

        } else if(ingredientDto instanceof AutomatedIngredientDto.Request.Create) {
            ingredient = new AutomatedIngredient();

        } else if(ingredientDto instanceof IngredientGroupDto.Request.Create) {
            ingredient = new IngredientGroup();

        } else {
            throw new IllegalStateException("IngredientType not supported yet!");
        }
        BeanUtils.copyProperties(ingredientDto, ingredient);
        ingredient.setParentGroup(parentGroup);
        return ingredient;
    }

    public List<Ingredient> getIngredientByFilter(String nameStartsWith, boolean filterManualIngredients,
                                                  boolean filterAutomaticIngredients, boolean filterIngredientGroups,
                                                  Long groupChildrenGroupId, boolean inBar, boolean onPump,
                                                  boolean inBarOrOnPump, boolean filterIngredientsWithParents) {
        List<Set<Long>> idsToFindSetList = new ArrayList<>();

        if(nameStartsWith != null) {
            idsToFindSetList.add(ingredientRepository.findIdsAutocompleteName(nameStartsWith));
        }
        if(filterManualIngredients) {
            idsToFindSetList.add(ingredientRepository.findIdsNotManual());
        }
        if(filterAutomaticIngredients) {
            idsToFindSetList.add(ingredientRepository.findIdsNotAutomatic());
        }
        if(filterIngredientGroups) {
            idsToFindSetList.add(ingredientRepository.findIdsNotGroup());
        }
        if(groupChildrenGroupId != null) {
            idsToFindSetList.add(ingredientRepository.findAllGroupChildrenIds(groupChildrenGroupId));
        }
        if(onPump) {
            idsToFindSetList.add(pumpService.findIngredientIdsOnPump());
        }
        if(inBar) {
            idsToFindSetList.add(ingredientRepository.findAddableIngredientsIdsInBar());
        }
        if(inBarOrOnPump) {
            Set<Long> ingIds = ingredientRepository.findAddableIngredientsIdsInBar();
            ingIds.addAll(pumpService.findIngredientIdsOnPump());
            idsToFindSetList.add(ingIds);
        }
        if(filterIngredientsWithParents) {
            idsToFindSetList.add(ingredientRepository.findIdsWithoutParents());
        }

        if(idsToFindSetList.isEmpty()) {
            return ingredientRepository.findByIds(ingredientRepository.findAllIds().toArray(new Long[0]));
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
        return ingredientRepository.findByIds(retained.toArray(new Long[0]));
    }

    public List<Ingredient> getIngredientsInExportOrder() {
        List<Ingredient> list = getIngredientByFilter(null, false,
                false, false, null, false, false,
                false, true);
        for (int i = 0; i < list.size(); i++) {
            Ingredient current = list.get(i);
            if(!(current instanceof IngredientGroup)) {
                continue;
            }
            IngredientGroup currentGroup = (IngredientGroup) current;
            list.addAll(currentGroup.getChildren());
        }
        return list;
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
        webSocketService.invalidateRecipeScrollCaches();
    }

    public void setImage(long ingredientId, byte[] image) {
        ingredientRepository.setImage(ingredientId, image);
    }

    public Ingredient updateIngredient(Ingredient ingredient) {
        if(ingredientRepository.findById(ingredient.getId()).isEmpty()) {
            throw new IllegalArgumentException("Ingredient doesn't exist!");
        }
        List<Ingredient> withSameName = ingredientRepository.findByIds(ingredientRepository.findIdsByNameIgnoringCase(ingredient.getName()).toArray(new Long[0]));
        if(!withSameName.isEmpty()) {
            Ingredient iWithSameName = withSameName.get(0);
            if(!Objects.equals(iWithSameName.getId(), ingredient.getId())) {
                throw new IllegalArgumentException("An ingredient with this name already exists!");
            }
            if(ingredient instanceof AddableIngredient) {
                ((AddableIngredient) ingredient).setInBar(iWithSameName.isInBar());
            }
        }
        try {
            ingredientRepository.update(ingredient);
        } catch (UncategorizedSQLException e) {
            if(e.getMessage().contains("Illegal cycle detected")) {
                throw new IllegalArgumentException("Illegal cycle detected");
            }
            throw e;
        }
        return ingredient;
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        Set<Long> foundWithName = ingredientRepository.findIdsByNameIgnoringCase(ingredient.getName());
        if(!foundWithName.isEmpty()) {
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

    public List<Ingredient> getGroupChildren(long id) {
        return ingredientRepository.findByIds(ingredientRepository.findDirectGroupChildrenIds(id).toArray(new Long[0]));
    }

    public byte[] getImage(long id) {
        return ingredientRepository.getImage(id).orElse(null);
    }

    public Ingredient getByName(String name) {
        Set<Long> ids = ingredientRepository.findIdsByNameIgnoringCase(name);
        if (ids.isEmpty()) {
            return null;
        }
        return ingredientRepository.findById(ids.iterator().next()).orElse(null);
    }
}
