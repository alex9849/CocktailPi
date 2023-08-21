package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.Category;
import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailmaker.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailmaker.model.recipe.productionstep.WrittenInstructionProductionStep;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.AddIngredientsProductionStepDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.ProductionStepDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.ProductionStepIngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.productionstep.WrittenInstructionProductionStepDto;
import net.alex9849.cocktailmaker.repository.CollectionRepository;
import net.alex9849.cocktailmaker.repository.ProductionStepRepository;
import net.alex9849.cocktailmaker.repository.RecipeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    ProductionStepRepository productionStepRepository;

    @Autowired
    IngredientService ingredientService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CollectionRepository collectionRepository;

    public Recipe createRecipe(Recipe recipe) {
        if(userService.getUser(recipe.getOwner().getId()) == null) {
            throw new IllegalArgumentException("User doesn't exist!");
        }
        return recipeRepository.create(recipe);
    }

    public Page<Recipe> getRecipesByFilter(Long ownerId, Long inCollection,
                                           Long inCategory, Long[] containsIngredients,
                                           String searchName, FabricableFilter fabricable, int pageNumber,
                                           int pageSize, Sort sort) {
        long offset = (long) pageNumber * pageSize;
        List<Set<Long>> idsToFindSetList = new ArrayList<>();

        if(inCategory != null) {
            idsToFindSetList.add(recipeRepository.getIdsInCategory(inCategory));
        }
        if(ownerId != null) {
            idsToFindSetList.add(recipeRepository.getIdsByOwnerId(ownerId));
        }
        if(inCollection != null) {
            idsToFindSetList.add(recipeRepository.findIdsInCollection(inCollection));
        }
        if(containsIngredients != null) {
            idsToFindSetList.add(recipeRepository.getIdsWithIngredients(containsIngredients));
        }
        if(searchName != null) {
            idsToFindSetList.add(recipeRepository.getIdsContainingName(searchName));
        }
        switch (fabricable) {
            case IN_BAR -> idsToFindSetList.add(recipeRepository.getIdsOfRecipesWithAllIngredientsInBarOrOnPumps());
            case AUTOMATICALLY -> idsToFindSetList.add(recipeRepository.getIdsOfFullyAutomaticallyFabricableRecipes());
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if(idsToFindSetList.isEmpty()) {
            return new PageImpl<>(recipeRepository.findAll(offset, pageSize, sort), pageable, recipeRepository.count());
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
            return new PageImpl<>(Collections.emptyList());
        }
        return new PageImpl<>(recipeRepository.findByIds(offset, pageSize, sort, retained.toArray(new Long[1])), pageable, retained.size());
    }

    public Recipe getById(long id) {
        return recipeRepository.findById(id).orElse(null);
    }

    public List<Recipe> getByIds(Long... ids) {
        return recipeRepository.findByIds(0, Long.MAX_VALUE, Sort.by(Sort.Direction.ASC, "name"), ids);
    }

    public byte[] getImage(long recipeId) {
        return recipeRepository.getImage(recipeId).orElse(null);
    }

    public void setImage(long recipeId, byte[] image) {
        recipeRepository.setImage(recipeId, image);
    }

    public boolean updateRecipe(Recipe recipe) {
        if(recipeRepository.findById(recipe.getId()).isEmpty()) {
            throw new IllegalArgumentException("Recipe doesn't exist!");
        }
        return recipeRepository.update(recipe);
    }

    public void delete(long recipeId) {
        recipeRepository.delete(recipeId);
    }

    public Recipe fromDto(RecipeDto.Request.Create recipeDto) {
        if(recipeDto == null) {
            return null;
        }
        Recipe recipe = new Recipe();
        BeanUtils.copyProperties(recipeDto, recipe);
        if(recipeDto.getProductionSteps() != null) {
            recipe.setProductionSteps(recipeDto.getProductionSteps().stream()
                    .map(this::fromDto).collect(Collectors.toList()));
        }

        List<Category> categories = new ArrayList<>();
        for(Long categoryId : recipeDto.getCategoryIds()) {
            Category category = categoryService.getCategory(categoryId);
            if(category == null) {
                throw new IllegalArgumentException("Category with id " + category.getId() + " doesn't exist!");
            }
            categories.add(category);
        }
        recipe.setCategories(categories);
        User user = userService.getUser(recipeDto.getOwnerId());
        if(user == null) {
            throw new IllegalArgumentException("User with id " + recipeDto.getOwnerId() + " doesn't exist!");
        }
        recipe.setOwner(userService.getUser(recipeDto.getOwnerId()));
        return recipe;
    }

    private ProductionStep fromDto(ProductionStepDto.Request.Create productionStepDto) {
        if(productionStepDto == null) {
            return null;
        }
        if(productionStepDto instanceof WrittenInstructionProductionStepDto.Request.Create) {
            return fromDto((WrittenInstructionProductionStepDto.Request.Create) productionStepDto);
        }
        if(productionStepDto instanceof AddIngredientsProductionStepDto.Request.Create) {
            return fromDto((AddIngredientsProductionStepDto.Request.Create) productionStepDto);
        }
        throw new IllegalStateException("ProductionSteDtoType not supported: " + productionStepDto.getType());
    }

    private WrittenInstructionProductionStep fromDto(WrittenInstructionProductionStepDto.Request.Create dto) {
        if(dto == null) {
            return null;
        }
        WrittenInstructionProductionStep pStep = new WrittenInstructionProductionStep();
        BeanUtils.copyProperties(dto, pStep);
        return pStep;
    }

    private AddIngredientsProductionStep fromDto(AddIngredientsProductionStepDto.Request.Create dto) {
        if(dto == null) {
            return null;
        }
        AddIngredientsProductionStep pStep = new AddIngredientsProductionStep();
        BeanUtils.copyProperties(dto, pStep);
        pStep.setStepIngredients(dto.getStepIngredients().stream()
                .map(this::fromDto).collect(Collectors.toList()));
        return pStep;
    }

    private ProductionStepIngredient fromDto(ProductionStepIngredientDto.Request.Create dto) {
        if(dto == null) {
            return null;
        }
        ProductionStepIngredient psi = new ProductionStepIngredient();
        BeanUtils.copyProperties(dto, psi);
        Ingredient ingredient = ingredientService.getIngredient(dto.getIngredientId());
        if(ingredient == null) {
            throw new IllegalArgumentException("Ingredient with Id \""
                    + dto.getIngredientId() + "\" doesn't exist!");
        }
        psi.setIngredient(ingredient);
        return psi;
    }

    public List<ProductionStep> getProductionStepsByRecipeId(long recipeId) {
        return productionStepRepository.loadByRecipeId(recipeId);
    }

    public enum FabricableFilter {
        ALL, IN_BAR, AUTOMATICALLY
    }
}
