package net.alex9849.cocktailmaker.payload.dto.recipe;

import lombok.*;
import net.alex9849.cocktailmaker.model.recipe.Ingredient;
import net.alex9849.cocktailmaker.model.recipe.IngredientGroup;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IngredientGroupDto {
    private interface Leaves { Set<Long> getLeafIds(); }
    private interface MinAlcoholContent { int getMinAlcoholContent(); }
    private interface MaxAlcoholContent { int getMaxAlcoholContent(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode
        public static class Create extends IngredientDto.Request.Create {

            @Override
            public String getType() {
                return "group";
            }

            @Override
            public Ingredient.Unit getUnit() {
                return Ingredient.Unit.MILLILITER;
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode
        public static class Detailed extends IngredientDto.Response.Detailed implements Leaves, MinAlcoholContent, MaxAlcoholContent {
            Set<Long> leafIds;
            int minAlcoholContent;
            int maxAlcoholContent;
            boolean inBar;

            public Detailed(IngredientGroup ingredientGroup) {
                super(ingredientGroup);
                this.leafIds = ingredientGroup.getAddableIngredientChildren()
                        .stream().map(Ingredient::getId)
                        .collect(Collectors.toSet());
            }

            @Override
            public String getType() {
                return "group";
            }

            @Override
            public Ingredient.Unit getUnit() {
                return Ingredient.Unit.MILLILITER;
            }
        }

        @Getter @Setter @EqualsAndHashCode
        public static class Reduced extends IngredientDto.Response.Reduced {
            boolean inBar;

            public Reduced(IngredientGroup ingredient) {
                super(ingredient);
            }

            @Override
            public String getType() {
                return "group";
            }
        }
    }
}
