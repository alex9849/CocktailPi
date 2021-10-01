import UserService from "src/services/user.service";

export const addIngredient = ({ dispatch, commit }, ingredientId) => {
  return UserService.addToMyOwnedIngredients(ingredientId)
    .then(() => {
      dispatch('fetchIngredients');
    })
}

export const fetchIngredients = ({ commit }) => {
  return UserService.getMyOwnedIngredients()
    .then(data => {
      commit('setOwnedIngredients', data)
      return data;
    })
}

export const removeIngredient = ({ dispatch, commit }, ingredientId) => {
  return UserService.removeFromMyOwnedIngredients(ingredientId)
    .then(() => {
      dispatch('fetchIngredients');
    })
}
