import axios from 'axios'

const API_PATH = 'api/cocktail/'

class CocktailService {
  order (recipeId, amount) {
    return axios.put(API_PATH + String(recipeId), null, {
      params: {
        amount: amount
      }
    })
  }

  checkFeasibility (recipeId, amount) {
    return axios.get(API_PATH + String(recipeId) + '/feasibility', {
      params: {
        amount: amount
      }
    }).then(response => response.data)
  }

  cancelCocktail () {
    return axios.delete(API_PATH)
  }

  continueProduction () {
    return axios.post(API_PATH + 'continueproduction')
  }
}

export default new CocktailService()
