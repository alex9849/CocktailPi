package net.alex9849.cocktailpi.payload.dto.recipe.ingredient;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import net.alex9849.cocktailpi.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.AutomatedIngredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.ManualIngredient;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AddableIngredientDto {
    private interface AlcoholContent { @Min(0) @Max(100) int getAlcoholContent(); }
    private interface HasImage { boolean isHasImage(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public abstract static class Create extends IngredientDto.Request.Create implements AlcoholContent {
            int alcoholContent;

            protected Create() {}

            protected Create(Response.Detailed detailed) {
                super(detailed);
            }

            public static Create fromDetailedDto(AddableIngredientDto.Response.Detailed detailed) {
                if(detailed == null) {
                    return null;
                }
                if (detailed instanceof ManualIngredientDto.Response.Detailed) {
                    return new ManualIngredientDto.Request.Create((ManualIngredientDto.Response.Detailed) detailed);
                }
                if (detailed instanceof AutomatedIngredientDto.Response.Detailed) {
                    return new AutomatedIngredientDto.Request.Create((AutomatedIngredientDto.Response.Detailed) detailed);
                }
                throw new IllegalStateException("Unknown ingredient type: " + detailed.getClass().getName());
            }
        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public abstract static class Detailed extends IngredientDto.Response.Detailed implements AlcoholContent, HasImage {
            int alcoholContent;
            boolean inBar;
            boolean hasImage;

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
            int alcoholContent;
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
