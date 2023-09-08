import axios from 'axios'
import JsUtils from 'src/services/JsUtils'

const API_PATH = 'api/cocktail/'

class CocktailService {
  order (recipeId, orderConfig, isIngredient = false) {
    let params = {
      isIngredient
    }
    params = JsUtils.cleanObject(params)
    return axios.put(API_PATH + String(recipeId), orderConfig, { params })
  }

  checkFeasibility (recipeId, orderConfig, isIngredient = false) {
    let params = {
      isIngredient
    }
    params = JsUtils.cleanObject(params)
    return axios.put(API_PATH + String(recipeId) + '/feasibility', orderConfig, { params })
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
