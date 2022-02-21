import axios from 'axios'
import FeasibilityReportDto from 'FeasibilityReportDto/Response'

const API_PATH = 'api/cocktail/'

class CocktailService {
  order (recipeId: bigint, amount: number) {
    return axios.put(API_PATH + String(recipeId), null, {
      params: {
        amount: amount
      }
    })
  }

  checkFeasibility (recipeId: bigint, amount: number) {
    return axios.get<FeasibilityReportDto.Detailed>(API_PATH + String(recipeId) + '/feasibility', {
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
