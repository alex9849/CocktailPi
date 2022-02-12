package net.alex9849.cocktailmaker.payload.dto.recipe;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IngredientDto {
    private interface Id { long getId(); }
    private interface Name { @NotNull @Size(min = 1, max = 30) String getName(); }
    private interface ParentGroupId { Long getParentGroupId(); }
    private interface ParentGroupName { String getParentGroupName(); }
    private interface Type { String getType(); }
    private interface InBar { boolean isInBar(); }
    private interface Unit { Ingredient.Unit getUnit(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = ManualIngredientDto.Request.Create.class, name = "manual"),
                @JsonSubTypes.Type(value = AutomatedIngredientDto.Request.Create.class, name = "automated"),
                @JsonSubTypes.Type(value = IngredientGroupDto.Request.Create.class, name = "group")
        })
        public abstract static class Create implements Name, ParentGroupId, Type, Unit {
            String name;
            Long parentGroupId;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public abstract static class Detailed implements Id, Name, ParentGroupId, ParentGroupName, Type, InBar, Unit {
            final long id;
            String name;
            Long parentGroupId;
            String parentGroupName;

            protected Detailed(Ingredient ingredient) {
                this.id = ingredient.getId();
                BeanUtils.copyProperties(ingredient, this);
                if(ingredient.getParentGroup() == null) {
                    this.parentGroupId = ingredient.getParentGroupId();
                    this.parentGroupName = ingredient.getParentGroup().getName();
                }
            }
        }

        @Getter @Setter @EqualsAndHashCode
        public abstract static class Reduced implements Id, Name, Type, InBar {
            final long id;
            String name;

            protected Reduced(Ingredient ingredient) {
                this.id = ingredient.getId();
                BeanUtils.copyProperties(ingredient, this);
            }
        }
    }
}
