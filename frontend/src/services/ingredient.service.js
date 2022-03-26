const API_PATH = 'api/ingredient/'
import axios from 'axios'

class IngredientService {
  getIngredients () {
    return axios.get(API_PATH)
      .then(response => response.data)
  }

  getIngredientsFilter (autocomplete, filterManualIngredients, filterAutomaticIngredients,
    filterIngredientGroups, groupLeafId, inBar) {
    return axios.get(API_PATH, {
      params: {
        autocomplete: autocomplete,
        filterManualIngredients: filterManualIngredients,
        filterAutomaticIngredients: filterAutomaticIngredients,
        filterIngredientGroups: filterIngredientGroups,
        groupLeafId: groupLeafId,
        inBar: inBar
      }
    })
      .then(response => response.data)
  }

  updateIngredient (id, createIngredient) {
    return axios.put(API_PATH + String(id), createIngredient)
  }

  createIngredient (createIngredient) {
    return axios.post(API_PATH, createIngredient)
  }

  deleteIngredient (id) {
    return axios.delete(API_PATH + String(id))
  }

  addToBar (ingredientId) {
    return axios.put(API_PATH + String(ingredientId) + '/bar')
  }

  removeFromBar (ingredientId) {
    return axios.delete(API_PATH + String(ingredientId) + '/bar')
  }
}

export default new IngredientService()

class IngredientDtoMapper {
  toIngredientCreateDto (ingredient) {
    if (ingredient.type === 'group') {
      return {
        type: ingredient.type,
        name: ingredient.name,
        parentGroupId: ingredient.parentGroupId
      }
    } else if (ingredient.type === 'manual') {
      return {
        name: ingredient.name,
        parentGroupId: ingredient.parentGroupId,
        type: ingredient.type,
        unit: ingredient.unit,
        alcoholContent: ingredient.alcoholContent
      }
    } else if (ingredient.type === 'automated') {
      return {
        type: ingredient.type,
        alcoholContent: ingredient.alcoholContent,
        name: ingredient.name,
        parentGroupId: ingredient.parentGroupId,
        pumpTimeMultiplier: ingredient.pumpTimeMultiplier
      }
    }
    throw new Error('Unknown ingredient type: ' + ingredient.type)
  }
}

export const ingredientDtoMapper = new IngredientDtoMapper()
