package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.category.CategoryDto;
import net.alex9849.cocktailmaker.payload.dto.user.UserDto;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeDto {

    public RecipeDto() {}

    public RecipeDto(Recipe recipe) {
        BeanUtils.copyProperties(recipe, this);
        User owner = new User();
        owner.setUsername(recipe.getOwner().getUsername());
        owner.setId(recipe.getOwner().getId());
        this.lastUpdate = recipe.getLastUpdate();
        this.ingredientsInBar = recipe.isIngredientsInBar();
        this.owner = new UserDto(owner);
        Map<Integer, List<RecipeIngredient>> byProductionStep = recipe
                .getRecipeIngredients().stream()
                .collect(Collectors.groupingBy(x -> x.getId().getProductionStep()));
        List<Integer> steps = new ArrayList<>(byProductionStep.keySet());
        steps.sort(Comparator.comparingInt(x -> x));
        this.recipeIngredients = steps.stream().map(x -> byProductionStep.get(x).stream()
                .map(RecipeIngredientDto::new).collect(Collectors.toList()))
                .collect(Collectors.toList());
        this.categories = recipe.getCategories().stream().map(CategoryDto::new)
                .collect(Collectors.toSet());
    }

    private Long id;

    @NotNull
    @Size(min = 1, max = 30)
    private String name;

    private boolean inPublic;

    @NotNull
    private UserDto owner;

    @NotNull
    @Size(min = 0, max = 3000)
    private String description;

    @NotNull
    private List<List<RecipeIngredientDto>> recipeIngredients;

    @NotNull
    private Set<CategoryDto> categories;

    private Date lastUpdate;

    private boolean ingredientsInBar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
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

    public boolean isIngredientsInBar() {
        return ingredientsInBar;
    }
}
