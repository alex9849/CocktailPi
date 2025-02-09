import JsUtils from 'src/services/JsUtils'

export const cachedRecipes = state => state.cachedRecipes
export const scrollPosition = state => state.scrollPosition
export const pagination = state => state.pagination
export const getApplicableRoute = state => state.applicableRoute

export const isCachedRoute = state => (route) => {
  if (state.applicableRoute.name !== route.name) {
    return false
  }
  if (!JsUtils.deepEquals(state.applicableRoute.query, route.query)) {
    return false
  }
  return JsUtils.deepEquals(state.applicableRoute.params, route.params)
}
