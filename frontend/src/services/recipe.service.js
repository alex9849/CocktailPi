import axios from 'axios'
import JsUtils from './JsUtils'
import Querystring from 'querystring'

const API_PATH = 'api/recipe/'

class RecipeService {
  createRecipe (createRecipe, image) {
    const uploadData = new FormData()
    const stringRecipe = JSON.stringify(createRecipe)
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

  getRecipes (page, ownerId, inCollection, fabricable, containsIngredients,
    searchName, inCategoryId, orderBy) {
    const inCategory = inCategoryId
    let params = {
      page,
      ownerId,
      inCollection,
      fabricable,
      containsIngredients,
      searchName,
      inCategory,
      orderBy
    }
    params = JsUtils.cleanObject(params)
    const config = {
      params,
      paramsSerializer: (params) => Querystring.stringify(params)
    }
    return axios.get(API_PATH, config)
      .then(response => {
        response.data.content = response.data.content.map(x => this.afterRecipeLoad(x))
        return response.data
      })
  }

  getIngredientRecipes () {
    return axios.get(API_PATH + 'ingredient')
      .then(response => {
        response.data = response.data.map(x => this.afterRecipeLoad(x))
        return response.data
      })
  }

  getRecipe (id, isIngredient = false) {
    let params = {
      isIngredient
    }
    params = JsUtils.cleanObject(params)
    return axios.get(API_PATH + String(id), { params })
      .then(response => this.afterRecipeLoad(response.data))
  }

  updateRecipe (id, createRecipe, image, removeImage) {
    const uploadData = new FormData()
    const stringRecipe = JSON.stringify(createRecipe)
    const blobRecipe = new Blob([stringRecipe], {
      type: 'application/json'
    })
    uploadData.append('recipe', blobRecipe)
    if (image) {
      uploadData.append('image', image)
    }
    return axios.put(API_PATH + String(id) + '?removeImage=' + String(removeImage), uploadData, { headers: { 'Content-Type': 'multipart/form-data' } })
  }

  deleteRecipe (recipeId) {
    return axios.delete(API_PATH + String(recipeId))
  }

  afterRecipeLoad (recipe) {
    recipe.lastUpdate = new Date(recipe.lastUpdate)
    return recipe
  }
}

export class RecipeDtoMapper {
  toRecipeCreateDto (detailed) {
    const createDto = {
      name: detailed.name,
      ownerId: detailed.ownerId,
      defaultAmountToFill: detailed.defaultAmountToFill,
      description: detailed.description,
      productionSteps: [],
      defaultGlassId: detailed.defaultGlass?.id,
      categoryIds: []
    }
    if (detailed.categories) {
      for (const category of detailed.categories) {
        createDto.categoryIds.push(category.id)
      }
    }
    if (detailed.productionSteps) {
      for (const prodStep of detailed.productionSteps) {
        createDto.productionSteps.push(this.toProductionStepCreateDto(prodStep))
      }
    }
    return createDto
  }

  toProductionStepCreateDto (prodStep) {
    if (prodStep.type === 'addIngredients') {
      const stepIngredients = []
      for (const addedIngredient of prodStep.stepIngredients) {
        stepIngredients.push(this.toProductionStepIngredientCreateDto(addedIngredient))
      }
      return {
        type: prodStep.type,
        stepIngredients
      }
    }

    if (prodStep.type === 'writtenInstruction') {
      return {
        type: prodStep.type,
        message: prodStep.message
      }
    }
    throw new Error('ProductionStep-Type unknown: ' + prodStep.type)
  }

  toProductionStepIngredientCreateDto (pStepIngredient) {
    return {
      amount: pStepIngredient.amount,
      scale: pStepIngredient.scale,
      boostable: pStepIngredient.boostable,
      ingredientId: pStepIngredient.ingredient.id
    }
  }
}

export default new RecipeService()

export const recipeDtoMapper = new RecipeDtoMapper()
