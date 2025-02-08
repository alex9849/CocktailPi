export const cachedRecipes = state => state.cachedRecipes
export const scrollPosition = state => state.scrollPosition
export const pagination = state => state.pagination
export const getApplicableRoute = state => state.applicableRoute

export const isCachedRoute = state => (route) => {
  if (state.applicableRoute.name !== route.name) {
    return false
  }
  const cacheKeys = new Set(Object.keys(state.applicableRoute.query))
  const routeKeys = new Set(Object.keys(route.query))
  for (const key of cacheKeys) {
    if (!routeKeys.has(key)) {
      return false
    }
    routeKeys.delete(key)
  }
  if (routeKeys.size !== 0) {
    return false
  }
  for (const key in Object.keys(state.applicableRoute.query)) {
    if (state.applicableRoute.query[key] !== route.query[key]) {
      return false
    }
  }
  return true
}
