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
        @JsonSubTypes.Type(value = ManualIngredientDto.class, name = "manual"),
        @JsonSubTypes.Type(value = AutomatedIngredientDto.class, name = "automated")
})
public abstract class IngredientDto {
    private long id;

    @NotNull
    @Size(min = 1, max = 30)
    private String name;

    @Min(0) @Max(100)
    private int alcoholContent;

    private boolean inBar;

    public IngredientDto() {}

    public IngredientDto(Ingredient ingredient) {
        BeanUtils.copyProperties(ingredient, this);
    }

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

    public int getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(int alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    public Ingredient.Unit getUnit() {
        return Ingredient.Unit.MILLILITER;
    }

    public abstract String getType();

    public boolean isInBar() {
        return inBar;
    }

    public void setInBar(boolean inBar) {
        this.inBar = inBar;
    }

    public static IngredientDto toDto(Ingredient ingredient) {
        if(ingredient instanceof ManualIngredient) {
            return new ManualIngredientDto((ManualIngredient) ingredient);
        }
        if(ingredient instanceof AutomatedIngredient) {
            return new AutomatedIngredientDto((AutomatedIngredient) ingredient);
        }
        throw new IllegalStateException("IngredientType is not supported yet!");
    }


}
