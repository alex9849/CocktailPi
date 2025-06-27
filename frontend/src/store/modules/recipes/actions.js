import RecipeService from 'src/services/recipe.service'

export const fetchRecipes = ({ commit }, { page, filter, userId, collectionId, categoryId, orderBy }) => {
  return RecipeService.getRecipes(
    page,
    userId,
    collectionId,
    filter.fabricable,
    filter.containsIngredients,
    filter.query,
    categoryId,
    orderBy
  ).then(response => {
    commit('addRecipes', response.content)
    commit('setPagination', { page: response.number, totalPages: response.totalPages })
  })
}
