const API_PATH = 'api/ingredient/'
import axios from 'axios'

class IngredientService {
  getIngredients () {
    return axios.get(API_PATH)
      .then(response => {
        response.data = response.data.map(x => this.afterIngredientLoad(x))
        return response.data
      })
  }

  getIngredientsFilter (autocomplete, filterManualIngredients, filterAutomaticIngredients,
    filterIngredientGroups, groupChildrenGroupId, inBar, onPump, inBarOrOnPump) {
    return axios.get(API_PATH, {
      params: {
        autocomplete,
        filterManualIngredients,
        filterAutomaticIngredients,
        filterIngredientGroups,
        groupChildrenGroupId,
        inBar,
        onPump,
        inBarOrOnPump
      }
    })
      .then(response => {
        response.data = response.data.map(x => this.afterIngredientLoad(x))
        return response.data
      })
  }

  updateIngredient (id, updateIngredient, image, removeImage = false) {
    const uploadData = new FormData()
    const stringIngredient = JSON.stringify(updateIngredient)
    const blobIngredient = new Blob([stringIngredient], {
      type: 'application/json'
    })
    uploadData.append('ingredient', blobIngredient)
    if (image) {
      uploadData.append('image', image)
    }
    return axios.put(API_PATH + String(id) + '?removeImage=' + String(removeImage), uploadData, { headers: { 'Content-Type': 'multipart/form-data' } })
  }

  createIngredient (createIngredient, image) {
    const uploadData = new FormData()
    const stringIngredient = JSON.stringify(createIngredient)
    const blobIngredient = new Blob([stringIngredient], {
      type: 'application/json'
    })
    uploadData.append('ingredient', blobIngredient)
    if (image) {
      uploadData.append('image', image)
    }
    return axios.post(API_PATH, uploadData, { headers: { 'Content-Type': 'multipart/form-data' } })
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

  afterIngredientLoad (ingredient) {
    ingredient.lastUpdate = new Date(ingredient.lastUpdate)
    return ingredient
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
        bottleSize: ingredient.bottleSize,
        parentGroupId: ingredient.parentGroupId,
        pumpTimeMultiplier: ingredient.pumpTimeMultiplier
      }
    }
    throw new Error('Unknown ingredient type: ' + ingredient.type)
  }
}

export const ingredientDtoMapper = new IngredientDtoMapper()
