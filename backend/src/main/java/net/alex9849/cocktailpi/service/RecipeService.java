package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.model.Glass;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.recipe.IngredientRecipe;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.model.recipe.productionstep.WrittenInstructionProductionStep;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.AddIngredientsProductionStepDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.ProductionStepDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.ProductionStepIngredientDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.WrittenInstructionProductionStepDto;
import net.alex9849.cocktailpi.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    PumpService pumpService;

    @Autowired
    IngredientService ingredientService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    GlassService glassService;


    public Recipe createRecipe(Recipe recipe) {
        if(userService.getUser(recipe.getOwner().getId()) == null) {
            throw new IllegalArgumentException("User doesn't exist!");
        }
        Recipe newRecipe = recipeRepository.create(recipe);
        webSocketService.invalidateRecipeScrollCaches();
        return newRecipe;
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
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        return new PageImpl<>(recipeRepository.findByIds(offset, pageSize, sort, retained.toArray(new Long[1])), pageable, retained.size());
    }

    public List<Recipe> getRecipeByName(String name) {
        if(name == null || name.isBlank()) {
            return null;
        }
        Set<Long> idsByName = recipeRepository.getIdsByName(name);
        return idsByName.stream().map(x -> recipeRepository.findById(x).orElse(null)).toList();
    }

    public List<IngredientRecipe> getCurrentIngredientRecipes() {
        List<Ingredient> ingredients = ingredientService.getIngredientByFilter(null, true, false,
                true, null, false, true, false, false);
        List<Pump> pumps = pumpService.getAllPumps();
        Glass siDefaultGlass = glassService.getIngredientRecipeDefaultGlass();
        return ingredients.stream()
                .map(x -> genIngredientRecipe(x, pumps, siDefaultGlass))
                .sorted(Comparator.comparing(Recipe::getName))
                .toList();
    }

    public IngredientRecipe getIngredientRecipe(long ingredientId) {
        Ingredient ingredient = ingredientService.getIngredient(ingredientId);
        if(ingredient == null) {
            return null;
        }
        return genIngredientRecipe(ingredient);
    }

    public IngredientRecipe genIngredientRecipe(Ingredient ingredient) {
        return genIngredientRecipe(ingredient, pumpService.getAllPumps(), glassService.getIngredientRecipeDefaultGlass());
    }

    public IngredientRecipe genIngredientRecipe(Ingredient ingredient, List<Pump> pumps, Glass defaultGlass) {
        IngredientRecipe recipe = new IngredientRecipe();
        long mlLeft = pumps.stream()
                .filter(Pump::isCompleted)
                .filter(x -> x.getCurrentIngredientId() != null)
                .filter(x -> x.getCurrentIngredientId() == ingredient.getId())
                .mapToInt(Pump::getFillingLevelInMl).sum();
        recipe.setIngredient(ingredient);
        recipe.setId(ingredient.getId());
        recipe.setName(ingredient.getName());
        recipe.setOwner(userService.getSystemUser());
        recipe.setCategories(new ArrayList<>());
        recipe.setDescription(ingredient.getName());
        recipe.setLastUpdate(ingredient.getLastUpdate());
        recipe.setDefaultGlass(defaultGlass);
        if(ingredient instanceof AddableIngredient addableIngredient) {
            recipe.setHasImage(addableIngredient.isHasImage());
        }
        ProductionStepIngredient psIngredient = new ProductionStepIngredient();
        psIngredient.setIngredient(ingredient);
        psIngredient.setScale(true);
        psIngredient.setBoostable(false);
        psIngredient.setAmount(50);
        AddIngredientsProductionStep aiProductionStep = new AddIngredientsProductionStep();
        aiProductionStep.setStepIngredients(new ArrayList<>(List.of(psIngredient)));
        recipe.setProductionSteps(new ArrayList<>(List.of(aiProductionStep)));
        recipe.setMlLeft(mlLeft);
        return recipe;
    }

    public Recipe getById(long id) {
        return recipeRepository.findById(id).orElse(null);
    }

    public List<Recipe> getByIds(Long... ids) {
        return recipeRepository.findByIds(0, Long.MAX_VALUE, Sort.by(Sort.Direction.ASC, "name"), ids);
    }

    public List<Recipe> getAll() {
        return recipeRepository.findAll();
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
        if(recipeRepository.update(recipe)) {
            webSocketService.invalidateRecipeScrollCaches();
            return true;
        }
        return false;
    }

    public void delete(long recipeId) {
        recipeRepository.delete(recipeId);
        webSocketService.invalidateRecipeScrollCaches();
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
                throw new IllegalArgumentException("Category with id " + categoryId + " doesn't exist!");
            }
            categories.add(category);
        }
        if(recipeDto.getDefaultGlassId() != null) {
            Glass glass = glassService.getById(recipeDto.getDefaultGlassId());
            if(glass == null) {
                throw new IllegalArgumentException("Glass with id " + recipeDto.getDefaultGlassId() + " doesn't exist!");
            }
            recipe.setDefaultGlass(glass);
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
