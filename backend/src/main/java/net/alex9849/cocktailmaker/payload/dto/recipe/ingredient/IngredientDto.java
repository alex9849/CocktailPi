package net.alex9849.cocktailmaker.payload.dto.recipe.ingredient;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import net.alex9849.cocktailmaker.model.recipe.ingredient.*;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IngredientDto {
    protected interface Id { long getId(); }
    protected interface Name { @NotNull @Size(min = 1, max = 30) String getName(); }
    protected interface ParentGroupId { Long getParentGroupId(); }
    protected interface ParentGroupName { String getParentGroupName(); }
    protected interface Type { String getType(); }
    protected interface InBar { boolean isInBar(); }
    protected interface OnPump { boolean isOnPump(); }
    protected interface Unit { Ingredient.Unit getUnit(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter
        @Setter
        @EqualsAndHashCode
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = ManualIngredientDto.Request.Create.class, name = "manual"),
                @JsonSubTypes.Type(value = AutomatedIngredientDto.Request.Create.class, name = "automated"),
                @JsonSubTypes.Type(value = IngredientGroupDto.Request.Create.class, name = "group")
        })
        public abstract static class Create implements Name, ParentGroupId, Type {
            String name;
            Long parentGroupId;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode
        public abstract static class Detailed implements Id, Name, ParentGroupId, ParentGroupName, Type, Unit, InBar, OnPump {
            long id;
            String name;
            Long parentGroupId;
            String parentGroupName;

            protected Detailed(Ingredient ingredient) {
                this.id = ingredient.getId();
                BeanUtils.copyProperties(ingredient, this);
                if (ingredient.getParentGroup() != null) {
                    this.parentGroupId = ingredient.getParentGroupId();
                    this.parentGroupName = ingredient.getParentGroup().getName();
                }
            }

            public static Detailed toDto(Ingredient ingredient) {
                if (ingredient == null) {
                    return null;
                }
                if (ingredient instanceof IngredientGroup) {
                    return new IngredientGroupDto.Response.Detailed((IngredientGroup) ingredient);
                }
                if (ingredient instanceof AddableIngredient) {
                    return AddableIngredientDto.Response.Detailed.toDto((AddableIngredient) ingredient);
                }
                throw new IllegalStateException("Unknown ingredient type: " + ingredient.getClass().getName());

            }
        }

        @Getter @Setter @EqualsAndHashCode
        public abstract static class Reduced implements Id, Name, Type, InBar, OnPump {
            final long id;
            String name;

            protected Reduced(Ingredient ingredient) {
                this.id = ingredient.getId();
                BeanUtils.copyProperties(ingredient, this);
            }

            public static Reduced toDto(Ingredient ingredient) {
                if(ingredient == null) {
                    return null;
                }
                if(ingredient instanceof IngredientGroup) {
                    return new IngredientGroupDto.Response.Reduced((IngredientGroup) ingredient);
                }
                if(ingredient instanceof AddableIngredient) {
                    return AddableIngredientDto.Response.Reduced.toDto((AddableIngredient) ingredient);
                }
                throw new IllegalStateException("Unknown ingredient type: " + ingredient.getClass().getName());

            }
        }

        @Getter @Setter @EqualsAndHashCode
        public abstract static class Export implements Id, Name, ParentGroupId, Type, Unit {
            long id;
            String name;
            Long parentGroupId;

            protected Export(Ingredient ingredient) {
                this.id = ingredient.getId();
                BeanUtils.copyProperties(ingredient, this);
                if (ingredient.getParentGroup() != null) {
                    this.parentGroupId = ingredient.getParentGroupId();
                }
            }

            public static Export toDto(Ingredient ingredient) {
                if (ingredient == null) {
                    return null;
                }
                if (ingredient instanceof IngredientGroup) {
                    return new IngredientGroupDto.Response.Export((IngredientGroup) ingredient);
                }
                if (ingredient instanceof AddableIngredient) {
                    return AddableIngredientDto.Response.Export.toDto((AddableIngredient) ingredient);
                }
                throw new IllegalStateException("Unknown ingredient type: " + ingredient.getClass().getName());

            }
        }
    }
}
