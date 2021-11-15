import axios from 'axios'
import JsUtils from './JsUtils'

const API_PATH = 'api/recipe/'

class RecipeService {
  createRecipe (recipe, image) {
    const uploadData = new FormData()
    const stringRecipe = JSON.stringify(recipe)
    const blobRecipe = new Blob([stringRecipe], {
      type: 'application/json'
    })
    uploadData.append('recipe', blobRecipe)
    if (image) {
      uploadData.append('image', image)
    }
    return axios.post(API_PATH, uploadData, { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(response => response.data)
  }

  getRecipes (page, ownerId, permittedForUser, inCollection, inPublic, fabricable, inBar, containsIngredients,
    searchName, inCategoryId, orderBy) {
    const querystring = require('querystring')
    const inCategory = inCategoryId
    let params = {
      page,
      ownerId,
      inPublic,
      permittedForUser,
      inCollection,
      fabricable,
      inBar,
      containsIngredients,
      searchName,
      inCategory,
      orderBy
    }
    params = JsUtils.cleanObject(params)
    const config = {
      params,
      paramsSerializer: function (params) {
        return querystring.stringify(params)
      }
    }
    return axios.get(API_PATH, config)
      .then(response => {
        response.data.content = response.data.content.map(x => this.afterRecipeLoad(x))
        return response.data
      })
  }

  getRecipe (id) {
    return axios.get(API_PATH + id)
      .then(response => this.afterRecipeLoad(response.data))
  }

  updateRecipe (recipe, image, removeImage) {
    const uploadData = new FormData()
    const stringRecipe = JSON.stringify(recipe)
    const blobRecipe = new Blob([stringRecipe], {
      type: 'application/json'
    })
    uploadData.append('recipe', blobRecipe)
    if (image) {
      uploadData.append('image', image)
    }
    return axios.put(API_PATH + recipe.id + '?removeImage=' + !!removeImage, uploadData, { headers: { 'Content-Type': 'multipart/form-data' } })
  }

  deleteRecipe (recipeId) {
    return axios.delete(API_PATH + recipeId)
  }

  afterRecipeLoad (recipe) {
    recipe.lastUpdate = new Date(recipe.lastUpdate)
    return recipe
  }
}

export default new RecipeService()
