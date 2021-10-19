package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.payload.dto.OwnerDto;
import net.alex9849.cocktailmaker.payload.dto.category.CategoryDto;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeDto {

    public RecipeDto() {}

    public RecipeDto(Recipe recipe) {
        BeanUtils.copyProperties(recipe, this);
        this.owner = new OwnerDto(recipe.getOwner());
        this.lastUpdate = recipe.getLastUpdate();
        Map<Long, List<RecipeIngredient>> byProductionStep = recipe
                .getProductionSteps().stream()
                .collect(Collectors.groupingBy(RecipeIngredient::getProductionStep));
        List<Long> steps = new ArrayList<>(byProductionStep.keySet());
        steps.sort(Comparator.comparingLong(x -> x));
        this.recipeIngredients = steps.stream().map(x -> byProductionStep.get(x).stream()
                .map(RecipeIngredientDto::new).collect(Collectors.toList()))
                .collect(Collectors.toList());
        this.categories = recipe.getCategories().stream().map(CategoryDto::new)
                .collect(Collectors.toSet());
    }

    private long id;

    @NotNull
    @Size(min = 1, max = 30)
    private String name;

    private boolean inPublic;

    @NotNull
    private OwnerDto owner;

    @NotNull
    @Size(min = 0, max = 3000)
    private String description;

    @NotNull
    private List<List<RecipeIngredientDto>> recipeIngredients;

    @NotNull
    private Set<CategoryDto> categories;

    @NotNull
    private long defaultAmountToFill;

    private Date lastUpdate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInPublic() {
        return inPublic;
    }

    public void setInPublic(boolean inPublic) {
        this.inPublic = inPublic;
    }

    public OwnerDto getOwner() {
        return owner;
    }

    public void setOwner(OwnerDto owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<List<RecipeIngredientDto>> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<List<RecipeIngredientDto>> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public Set<CategoryDto> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryDto> categories) {
        this.categories = categories;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public long getDefaultAmountToFill() {
        return defaultAmountToFill;
    }

    public void setDefaultAmountToFill(long defaultAmountToFill) {
        this.defaultAmountToFill = defaultAmountToFill;
    }
}
