package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.Tag;
import net.alex9849.cocktailmaker.payload.dto.user.UserDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipeDto {

    public RecipeDto() {}

    public RecipeDto(Recipe recipe) {
        this.id = recipe.getId();
        this.name = recipe.getName();
        this.inPublic = recipe.isInPublic();
        this.owner = new UserDto(recipe.getOwner());
        this.description = recipe.getDescription();
        this.recipeIngredients = recipe.getRecipeIngredients().stream()
                .map(RecipeIngredientDto::new).collect(Collectors.toSet());
        this.tags = recipe.getTags().stream().map(Tag::getName)
                .collect(Collectors.toSet());

    }

    private Long id;

    @NotNull
    @Size(min = 1, max = 30)
    private String name;

    private boolean inPublic;

    private UserDto owner;

    @NotNull
    @Size(min = 0, max = 3000)
    private String description;

    @NotNull
    private Set<RecipeIngredientDto> recipeIngredients;

    @NotNull
    private Set<String> tags;

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

    public Set<RecipeIngredientDto> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(Set<RecipeIngredientDto> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
