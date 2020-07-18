package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.Ingredient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class IngredientDto {

    private Long id;

    @NotNull
    @Size(min = 1, max = 30)
    private String name;

    public IngredientDto() {}

    public IngredientDto(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();
    }

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
}
