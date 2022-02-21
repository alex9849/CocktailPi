const API_PATH = 'api/ingredient/'
import axios from 'axios'
import IngredientDtoResponse from 'IngredientDto/Response'
import IngredientDtoRequest from 'IngredientDto/Request'
import IngredientGroupDtoResponse from 'IngredientGroupDto/Response'
import IngredientGroupDtoRequest from 'IngredientGroupDto/Request'
import ManualIngredientDtoResponse from 'ManualIngredientDto/Response'
import ManualIngredientDtoRequest from 'ManualIngredientDto/Request'
import AutomatedIngredientDtoResponse from 'AutomatedIngredientDto/Response'
import AutomatedIngredientDtoRequest from 'AutomatedIngredientDto/Request'

class IngredientService {
  getIngredients () {
    return axios.get<IngredientDtoResponse.Detailed[]>(API_PATH)
      .then(response => response.data)
  }

  getIngredientsFilter (autocomplete?: string, filterManualIngredients?: boolean, filterAutomaticIngredients?: boolean,
                        filterIngredientGroups?: boolean, inBar?: boolean) {
    return axios.get<IngredientDtoResponse.Detailed[]>(API_PATH, {
      params: {
        autocomplete: autocomplete,
        filterManualIngredients: filterManualIngredients,
        filterAutomaticIngredients: filterAutomaticIngredients,
        filterIngredientGroups: filterIngredientGroups,
        inBar: inBar
      }
    })
      .then(response => response.data)
  }

  updateIngredient (id: bigint, ingredient: IngredientDtoRequest.Create) {
    return axios.put(API_PATH + String(id), ingredient)
  }

  createIngredient (ingredient: IngredientDtoRequest.Create) {
    return axios.post<IngredientDtoResponse.Detailed>(API_PATH, ingredient)
  }

  deleteIngredient (id: bigint) {
    return axios.delete(API_PATH + String(id))
  }

  addToBar (ingredientId: bigint) {
    return axios.put(API_PATH + String(ingredientId) + '/bar')
  }

  removeFromBar (ingredientId: bigint) {
    return axios.delete(API_PATH + String(ingredientId) + '/bar')
  }
}

export default new IngredientService()

export class IngredientDtoMapper {

  toIngredientCreateDto(ingredient: IngredientDtoResponse.Detailed) {
    if((<IngredientGroupDtoResponse.Detailed>ingredient)) {
      const ingredientGroup = (<IngredientGroupDtoResponse.Detailed>ingredient)
      return {
        type: ingredientGroup.type,
        name: ingredientGroup.name,
        parentGroupId: ingredientGroup.parentGroupId
      } as IngredientGroupDtoRequest.Create
    }
    else if ((<ManualIngredientDtoResponse.Detailed>ingredient)) {
      const mIngredient = (<ManualIngredientDtoResponse.Detailed>ingredient)
      return {
        name: mIngredient.name,
        parentGroupId: mIngredient.parentGroupId,
        type: mIngredient.type,
        unit: mIngredient.unit,
        alcoholContent: mIngredient.alcoholContent
      } as ManualIngredientDtoRequest.Create
    }
    else if ((<AutomatedIngredientDtoResponse.Detailed>ingredient)) {
      const aIngredient = (<AutomatedIngredientDtoResponse.Detailed>ingredient)
      return {
        type: aIngredient.type,
        alcoholContent: aIngredient.alcoholContent,
        name: aIngredient.name,
        parentGroupId: aIngredient.parentGroupId,
        pumpTimeMultiplier: aIngredient.pumpTimeMultiplier
      } as AutomatedIngredientDtoRequest.Create
    }
    throw 'Unknown ingredient type: ' + ingredient.type
  }

}
