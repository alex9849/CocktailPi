package net.alex9849.cocktailmaker.payload.dto.recipe.ingredient;

import lombok.*;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailmaker.model.recipe.ingredient.ManualIngredient;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AddableIngredientDto {
    private interface AlcoholContent { @Min(0) @Max(100) int getAlcoholContent(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public abstract static class Create extends IngredientDto.Request.Create implements AlcoholContent {
            int alcoholContent;

            protected Create() {}

            protected Create(Response.Detailed detailed) {
                super(detailed);
            }
        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public abstract static class Detailed extends IngredientDto.Response.Detailed implements AlcoholContent {
            int alcoholContent;
            boolean inBar;

            protected Detailed() {}

            protected Detailed(AddableIngredient ingredient) {
                super(ingredient);
            }

            public static Detailed toDto(AddableIngredient ingredient) {
                if(ingredient == null) {
                    return null;
                }
                if (ingredient instanceof ManualIngredient) {
                    return new ManualIngredientDto.Response.Detailed((ManualIngredient) ingredient);
                }
                if (ingredient instanceof AutomatedIngredient) {
                    return new AutomatedIngredientDto.Response.Detailed((AutomatedIngredient) ingredient);
                }
                throw new IllegalStateException("Unknown ingredient type: " + ingredient.getClass().getName());
            }
        }

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public abstract static class Reduced extends IngredientDto.Response.Reduced {
            boolean inBar;

            protected Reduced(AddableIngredient ingredient) {
                super(ingredient);
            }

            public static Reduced toDto(AddableIngredient ingredient) {
                if(ingredient == null) {
                    return null;
                }
                if(ingredient instanceof ManualIngredient) {
                    return new ManualIngredientDto.Response.Reduced((ManualIngredient) ingredient);
                }
                if(ingredient instanceof AutomatedIngredient) {
                    return new AutomatedIngredientDto.Response.Reduced((AutomatedIngredient) ingredient);
                }
                throw new IllegalStateException("Unknown ingredient type: " + ingredient.getClass().getName());
            }
        }
    }
}
