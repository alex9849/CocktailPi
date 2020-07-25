package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.Recipe;
import net.alex9849.cocktailmaker.model.recipe.RecipeIngredient;
import net.alex9849.cocktailmaker.model.recipe.Tag;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.user.UserDto;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipeDto {

    public RecipeDto() {}

    public RecipeDto(Recipe recipe) {
        BeanUtils.copyProperties(recipe, this);
        User owner = new User();
        if(recipe.getOwner() != null) {
            owner.setUsername(recipe.getOwner().getUsername());
            owner.setId(recipe.getOwner().getId());
        } else {
            owner.setUsername("System");
        }
        this.owner = new UserDto(owner);
        recipe.getRecipeIngredients().sort((Comparator.comparingInt(RecipeIngredient::getIndex)));
        this.recipeIngredients = recipe.getRecipeIngredients().stream()
                .map(RecipeIngredientDto::new).collect(Collectors.toList());
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
    @Size(min = 0, max = 100)
    private String shortDescription;

    @NotNull
    private List<RecipeIngredientDto> recipeIngredients;

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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public List<RecipeIngredientDto> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<RecipeIngredientDto> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
