export const setRecipes = (state, recipes) => {
  state.cachedRecipes = recipes
}

export const addRecipes = (state, newRecipes) => {
  state.cachedRecipes.push(...newRecipes)
}

export const setScrollPosition = (state, position) => {
  state.scrollPosition = position
}

export const setPagination = (state, pagination) => {
  state.pagination = pagination
}
