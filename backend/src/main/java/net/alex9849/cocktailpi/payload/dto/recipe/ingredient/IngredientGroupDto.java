package net.alex9849.cocktailpi.payload.dto.recipe.ingredient;

import lombok.*;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.IngredientGroup;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IngredientGroupDto {
    private interface Leaves { Set<Long> getLeafIds(); }
    private interface Children<T> { Set<T> getChildren(); }
    private interface MinAlcoholContent { Integer getMinAlcoholContent(); }
    private interface MaxAlcoholContent { Integer getMaxAlcoholContent(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Create extends IngredientDto.Request.Create {

            public Create() {}

            public Create(Response.Detailed detailed) {
                super(detailed);
            }

            @Override
            public String getType() {
                return "group";
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends IngredientDto.Response.Detailed implements Leaves, MinAlcoholContent, MaxAlcoholContent {
            Set<Long> leafIds;
            Integer minAlcoholContent;
            Integer maxAlcoholContent;
            boolean inBar;
            boolean onPump;

            public Detailed() {}

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

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Reduced extends IngredientDto.Response.Reduced {
            boolean inBar;
            boolean onPump;

            public Reduced(IngredientGroup ingredient) {
                super(ingredient);
            }

            @Override
            public String getType() {
                return "group";
            }
        }


        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Tree extends IngredientDto.Response.Detailed implements Children<IngredientDto.Response.Detailed> {
            Set<IngredientDto.Response.Detailed> children;
            boolean inBar;
            boolean onPump;

            public Tree() {}

            public Tree(IngredientGroup ingredientGroup) {
                super(ingredientGroup);
                this.inBar = ingredientGroup.isInBar();
                this.onPump = ingredientGroup.isOnPump();
                this.children = ingredientGroup.getChildren()
                        .stream().map(x -> IngredientDto.Response.Detailed.toDto(x, true))
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
    }
}
