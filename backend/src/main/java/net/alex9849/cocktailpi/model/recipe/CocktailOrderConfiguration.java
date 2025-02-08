package net.alex9849.cocktailpi.model.recipe;

import net.alex9849.cocktailpi.model.recipe.ingredient.AddableIngredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CocktailOrderConfiguration {
    //IngredientGroupId, Replacement
    private Map<Long, AddableIngredient> productionStepReplacements = new HashMap<>();
    private Integer amountOrderedInMl;
    private Customisations customisations;

    public Map<Long, AddableIngredient> getProductionStepReplacements() {
        return productionStepReplacements;
    }

    public void setProductionStepReplacements(Map<Long, AddableIngredient> productionStepReplacements) {
        this.productionStepReplacements = productionStepReplacements;
    }

    public Integer getAmountOrderedInMl() {
        return amountOrderedInMl;
    }

    public void setAmountOrderedInMl(int amountOrderedInMl) {
        this.amountOrderedInMl = amountOrderedInMl;
    }

    public AddableIngredient getReplacement(long ingredientGroupId) {
        return productionStepReplacements
                .getOrDefault(ingredientGroupId, null);
    }

    public Customisations getCustomisations() {
        return customisations;
    }

    public void setCustomisations(Customisations customisations) {
        this.customisations = customisations;
    }

    public static class Customisations {
        private int boost;
        private List<AdditionalIngredient> additionalIngredients;

        public int getBoost() {
            return boost;
        }

        public void setBoost(int boost) {
            this.boost = boost;
        }

        public List<AdditionalIngredient> getAdditionalIngredients() {
            return additionalIngredients;
        }

        public void setAdditionalIngredients(List<AdditionalIngredient> additionalIngredients) {
            this.additionalIngredients = additionalIngredients;
        }

        public static class AdditionalIngredient {
            private AddableIngredient ingredient;
            private int amount;

            public AddableIngredient getIngredient() {
                return ingredient;
            }

            public void setIngredient(AddableIngredient ingredient) {
                this.ingredient = ingredient;
            }

            public int getAmount() {
                return amount;
            }

            public void setAmount(int amount) {
                this.amount = amount;
            }
        }
    }
}
