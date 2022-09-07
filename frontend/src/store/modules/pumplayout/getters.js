export const getPumpIngredients = (state) => {
  const pumpIngredients = []
  for (const pump of state.pumpLayout) {
    if (pump.currentIngredient && !pumpIngredients.some(x => x.id === pump.currentIngredient.id)) {
      pumpIngredients.push(pump.currentIngredient)
    }
  }
  return pumpIngredients
}

export const getPumpOccupation = (state) => {
  return (pumpId) => {
    for (const pump of state.pumpLayout) {
      if (pump.id === pumpId) {
        return pump.occupation
      }
    }
    return false
  }
}

export const isPumpingReversed = (state) => {
  // TODO Add correct attribute
  return (pumpId) => {
    for (const pump of state.pumpLayout) {
      if (pump.id === pumpId) {
        return pump.reversed
      }
    }
    return false
  }
}

export const isPumpedUp = (state) => {
  return (pumpId) => {
    for (const pump of state.pumpLayout) {
      if (pump.id === pumpId) {
        return pump.pumpedUp
      }
    }
    return false
  }
}

export const anyOccupied = (state) => state.pumpLayout.some(x => x.occupation !== 'NONE')

export const getLayout = (state) => state.pumpLayout
