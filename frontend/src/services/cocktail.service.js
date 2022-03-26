import axios from 'axios'

const API_PATH = 'api/cocktail/'

class CocktailService {
  order (recipeId, orderConfig) {
    return axios.put(API_PATH + String(recipeId), orderConfig)
  }

  checkFeasibility (recipeId, orderConfig) {
    return axios.put(API_PATH + String(recipeId) + '/feasibility', orderConfig)
      .then(response => response.data)
  }

  cancelCocktail () {
    return axios.delete(API_PATH)
  }

  continueProduction () {
    return axios.post(API_PATH + 'continueproduction')
  }
}

export default new CocktailService()
