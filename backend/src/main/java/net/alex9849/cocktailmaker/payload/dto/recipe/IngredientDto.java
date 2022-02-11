package net.alex9849.cocktailmaker.payload.dto.recipe;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.alex9849.cocktailmaker.model.recipe.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.IngredientGroup;
import net.alex9849.cocktailmaker.model.recipe.ManualIngredient;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ManualIngredientDto.class, name = "manual"),
        @JsonSubTypes.Type(value = AutomatedIngredientDto.class, name = "automated"),
        @JsonSubTypes.Type(value = IngredientGroupDto.class, name = "group")
})
public abstract class IngredientDto {
    private long id;

    @NotNull
    @Size(min = 1, max = 30)
    private String name;

    private Long parentGroupId;
    private String parentGroupName;

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

    public String getParentGroupName() {
        return parentGroupName;
    }

    public void setParentGroupName(String parentGroupName) {
        this.parentGroupName = parentGroupName;
    }

    public static IngredientDto toDto(Ingredient ingredient) {
        if(ingredient instanceof ManualIngredient) {
            return new ManualIngredientDto((ManualIngredient) ingredient);
        }
        if(ingredient instanceof AutomatedIngredient) {
            return new AutomatedIngredientDto((AutomatedIngredient) ingredient);
        }
        if(ingredient instanceof IngredientGroup) {
            return new IngredientGroupDto((IngredientGroup) ingredient);
        }
        throw new IllegalStateException("IngredientType is not supported yet!");
    }

    public Long getParentGroupId() {
        return parentGroupId;
    }

    public abstract String getType();

    public void setParentGroupId(Long parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

    public abstract boolean isInBar();

    public abstract Ingredient.Unit getUnit();


}
