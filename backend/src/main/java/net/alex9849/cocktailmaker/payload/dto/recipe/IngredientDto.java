package net.alex9849.cocktailmaker.payload.dto.recipe;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.ManualIngredient;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ManualIngredientDto.class, name = "MANUAL"),
        @JsonSubTypes.Type(value = AutomaticIngredientDto.class, name = "AUTOMATIC")
})
public abstract class IngredientDto {
    private Long id;

    @NotNull
    @Size(min = 1, max = 30)
    private String name;

    @Min(0) @Max(100)
    private int alcoholContent;

    public IngredientDto() {}

    public IngredientDto(Ingredient ingredient) {
        BeanUtils.copyProperties(ingredient, this);
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

    public int getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(int alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    public static IngredientDto toDto(Ingredient ingredient) {
        if(ingredient instanceof ManualIngredient) {
            return new ManualIngredientDto((ManualIngredient) ingredient);
        }
        if(ingredient instanceof AutomatedIngredient) {
            return new AutomaticIngredientDto((AutomatedIngredient) ingredient);
        }
        throw new IllegalStateException("IngredientType is not implemented yet!");
    }
}
