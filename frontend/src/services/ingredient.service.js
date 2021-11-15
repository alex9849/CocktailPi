const API_PATH = 'api/ingredient/'
import axios from 'axios'

class IngredientService {
  getIngredients () {
    return axios.get(API_PATH)
      .then(response => response.data)
  }

  getIngredientsFilter (autocomplete, filterManualIngredients) {
    return axios.get(API_PATH, {
      params: {
        autocomplete: autocomplete,
        filterManualIngredients: filterManualIngredients
      }
    })
      .then(response => response.data)
  }

  updateIngredient (ingredient) {
    return axios.put(API_PATH + ingredient.id, ingredient)
  }

  createIngredient (ingredient) {
    return axios.post(API_PATH, ingredient)
  }

  deleteIngredient (ingredient) {
    return axios.delete(API_PATH + ingredient.id)
  }
}

export default new IngredientService()
