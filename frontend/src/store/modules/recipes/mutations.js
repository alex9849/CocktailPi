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

export const setApplicableRoute = (state, route) => {
  state.applicableRoute = route
}

export const reset = (state) => {
  state.cachedRecipes = []
  state.applicableRoute = null
  state.scrollPosition = 0
  state.pagination.page = 1
  state.pagination.totalPages = 1
}
