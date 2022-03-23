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
