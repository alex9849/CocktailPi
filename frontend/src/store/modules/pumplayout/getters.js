export const doPumpsHaveAllIngredientsForRecipe = (state) => {
  return (recipe) => {
    let pumpIngredientIds = new Set();
    for(let pump of state.pumpLayout) {
      if(pump.currentIngredient) {
        pumpIngredientIds.add(pump.currentIngredient.id)
      }
    }
    let success = true;
    for(let productionstep of recipe.recipeIngredients) {
      for(let ingredientstep of productionstep) {
        success &= pumpIngredientIds.has(ingredientstep.ingredient.id)
      }
    }
    return success;
  }
};

export const areEnoughPumpsAvailable = (state) => {
  return (recipe) => {
    let recipeIngredientIds = new Set();

    for(let productionstep of recipe.recipeIngredients) {
      for(let ingredientstep of productionstep) {
        recipeIngredientIds.add(ingredientstep.ingredient.id);
      }
    }
    return recipeIngredientIds.size <= state.pumpLayout.length;
  }
};

export const isCleaning = (state) => {
  return (pumpId) => {
    for(let pump of state.pumpLayout) {
      if(pump.id === pumpId) {
        return pump.cleaning;
      }
    }
    return false;
  }
};

export const getLayout = (state) => state.pumpLayout;
