package net.alex9849.cocktailpi.payload.dto.recipe.ingredient;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import net.alex9849.cocktailpi.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.IngredientGroup;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IngredientDto {
    protected interface Id { long getId(); }
    protected interface Name { @NotNull @Size(min = 1, max = 30) String getName(); }
    protected interface NormalName { String getNormalName(); }
    protected interface ParentGroupId { Long getParentGroupId(); }
    protected interface ParentGroupName { String getParentGroupName(); }
    protected interface Type { String getType(); }
    protected interface InBar { boolean isInBar(); }
    protected interface OnPump { boolean isOnPump(); }
    protected interface Unit { Ingredient.Unit getUnit(); }
    protected interface LastUpdate { Date getLastUpdate(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = ManualIngredientDto.Request.Create.class, name = "manual"),
                @JsonSubTypes.Type(value = AutomatedIngredientDto.Request.Create.class, name = "automated"),
                @JsonSubTypes.Type(value = IngredientGroupDto.Request.Create.class, name = "group")
        })
        public abstract static class Create implements Name, ParentGroupId, Type {
            String name;
            Long parentGroupId;

            protected Create() {}

            protected Create(Response.Detailed detailed) {
                BeanUtils.copyProperties(detailed, this);
            }

            public static Create fromDetailedDto(Response.Detailed detailed) {
                if(detailed == null) {
                    return null;
                }
                if (detailed instanceof IngredientGroupDto.Response.Detailed) {
                    return new IngredientGroupDto.Request.Create((IngredientGroupDto.Response.Detailed) detailed);
                }
                if (detailed instanceof AddableIngredientDto.Response.Detailed) {
                    return AddableIngredientDto.Request.Create.fromDetailedDto((AddableIngredientDto.Response.Detailed) detailed);
                }
                throw new IllegalStateException("Unknown ingredient type: " + detailed.getClass().getName());
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = ManualIngredientDto.Response.Detailed.class, name = "manual"),
                @JsonSubTypes.Type(value = AutomatedIngredientDto.Response.Detailed.class, name = "automated"),
                @JsonSubTypes.Type(value = IngredientGroupDto.Response.Detailed.class, name = "group")
        })
        public abstract static class Detailed implements Id, Name, NormalName, ParentGroupId, ParentGroupName, Type, Unit, InBar, OnPump, LastUpdate {
            long id;
            String name;
            String normalName;
            Long parentGroupId;
            String parentGroupName;
            Date lastUpdate;

            protected Detailed() {}

            protected Detailed(Ingredient ingredient) {
                this.id = ingredient.getId();
                BeanUtils.copyProperties(ingredient, this);
                this.normalName = ingredient.getNormalName();
                if (ingredient.getParentGroup() != null) {
                    this.parentGroupId = ingredient.getParentGroupId();
                    this.parentGroupName = ingredient.getParentGroup().getName();
                }
            }

            public static Detailed toDto(Ingredient ingredient) {
                return toDto(ingredient, false);
            }

            public static Detailed toDto(Ingredient ingredient, boolean exportIngredientGroupChildren) {
                if (ingredient == null) {
                    return null;
                }
                if (ingredient instanceof IngredientGroup) {
                    if(exportIngredientGroupChildren) {
                        return new IngredientGroupDto.Response.Tree((IngredientGroup) ingredient);
                    } else {
                        return new IngredientGroupDto.Response.Detailed((IngredientGroup) ingredient);
                    }
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
    }
}
