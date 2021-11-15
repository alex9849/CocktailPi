export const doPumpsHaveAllIngredientsForRecipe = (state) => {
  return (recipe) => {
    const pumpIngredientIds = new Set()
    for (const pump of state.pumpLayout) {
      if (pump.currentIngredient) {
        pumpIngredientIds.add(pump.currentIngredient.id)
      }
    }
    let success = true
    for (const productionSteps of recipe.productionSteps) {
      if (productionSteps.type !== 'addIngredients') {
        continue
      }
      for (const ingredientStep of productionSteps.stepIngredients) {
        success &= pumpIngredientIds.has(ingredientStep.ingredient.id)
      }
    }
    return success
  }
}

export const getPumpIngredients = (state) => {
  const pumpIngredients = []
  for (const pump of state.pumpLayout) {
    if (pump.currentIngredient && !pumpIngredients.some(x => x.id === pump.currentIngredient.id)) {
      pumpIngredients.push(pump.currentIngredient)
    }
  }
  return pumpIngredients
}

export const isCleaning = (state) => {
  return (pumpId) => {
    for (const pump of state.pumpLayout) {
      if (pump.id === pumpId) {
        return pump.cleaning
      }
    }
    return false
  }
}

export const getLayout = (state) => state.pumpLayout
