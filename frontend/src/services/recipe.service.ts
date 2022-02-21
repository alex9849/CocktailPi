import axios from 'axios'
import JsUtils from './JsUtils'
import RecipeDtoResponse from 'RecipeDto/Response'
import RecipeDtoRequest from 'RecipeDto/Request'
import AddIngredientsProductionStepDtoRequest from 'AddIngredientsProductionStepDto/Request'
import AddIngredientsProductionStepDtoResponse from 'AddIngredientsProductionStepDto/Response'
import WrittenInstructionProductionStepDtoRequest from 'WrittenInstructionProductionStepDto/Request'
import WrittenInstructionProductionStepDtoResponse from 'WrittenInstructionProductionStepDto/Response'
import ProductionStepIngredientDtoRequest from 'ProductionStepIngredientDto/Request'
import ProductionStepIngredientDtoResponse from 'ProductionStepIngredientDto/Response'
import ProductionStepDtoResponse from 'ProductionStepDto/Response'

import {SpringPage} from 'src/models/response/SpringPage';
import Querystring from 'querystring'

const API_PATH = 'api/recipe/'

class RecipeService {

  createRecipe (recipe: RecipeDtoRequest.Create, image: Blob) {
    const uploadData = new FormData()
    const stringRecipe = JSON.stringify(recipe)
    const blobRecipe = new Blob([stringRecipe], {
      type: 'application/json'
    })
    uploadData.append('recipe', blobRecipe)
    if (image) {
      uploadData.append('image', image)
    }
    return axios.post<RecipeDtoResponse.Detailed>(API_PATH, uploadData, { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(response => response.data)
  }

  getRecipes (page?: number, ownerId?: bigint, inCollection?: bigint, fabricable?: boolean, inBar?: boolean, containsIngredients?: bigint[],
    searchName?: string, inCategoryId?: bigint, orderBy?: string) {
    const inCategory = inCategoryId
    let params = {
      page,
      ownerId,
      inCollection,
      fabricable,
      inBar,
      containsIngredients,
      searchName,
      inCategory,
      orderBy
    }
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    params = JsUtils.cleanObject(params)
    const config = {
      params,
      // eslint-disable-next-line @typescript-eslint/no-unsafe-argument
      paramsSerializer: (params: any) => Querystring.stringify(params)
    }
    return axios.get<SpringPage<RecipeDtoResponse.SearchResult>>(API_PATH, config)
      .then(response => {
        response.data.content = response.data.content.map(x => this.afterRecipeLoad(x))
        return response.data
      })
  }

  getRecipe (id: bigint) {
    return axios.get<RecipeDtoResponse.Detailed>(API_PATH + String(id))
      .then(response => this.afterRecipeLoad(response.data))
  }

  updateRecipe (id: bigint, recipe: RecipeDtoRequest.Create, image: Blob, removeImage: boolean) {
    const uploadData = new FormData()
    const stringRecipe = JSON.stringify(recipe)
    const blobRecipe = new Blob([stringRecipe], {
      type: 'application/json'
    })
    uploadData.append('recipe', blobRecipe)
    if (image) {
      uploadData.append('image', image)
    }
    return axios.put(API_PATH + String(id) + '?removeImage=' + String(removeImage), uploadData, { headers: { 'Content-Type': 'multipart/form-data' } })
  }

  deleteRecipe (recipeId: bigint) {
    return axios.delete(API_PATH + String(recipeId))
  }

  afterRecipeLoad <T extends RecipeDtoResponse.SearchResult | RecipeDtoResponse.Detailed> (recipe: T): T {
    recipe.lastUpdate = new Date(recipe.lastUpdate)
    return recipe
  }
}

export class RecipeDtoMapper {
  toRecipeCreateDto(detailed: RecipeDtoResponse.Detailed) {
    const createDto = {
      name: detailed.name,
      ownerId: detailed.ownerId,
      defaultAmountToFill: detailed.defaultAmountToFill,
      description: detailed.description,
      productionSteps: [],
      categoryIds: []
    } as RecipeDtoRequest.Create
    for(const category of detailed.categories) {
      createDto.categoryIds.push(category.id)
    }
    for(const prodStep of detailed.productionSteps) {
      createDto.productionSteps.push(this.toProductionStepCreateDto(prodStep))
    }
    return createDto
  }

  toProductionStepCreateDto(prodStep: ProductionStepDtoResponse.Detailed) {
    if((<AddIngredientsProductionStepDtoResponse.Detailed>prodStep)) {
      const addIProdStep = (<AddIngredientsProductionStepDtoResponse.Detailed>prodStep)
      const stepIngredients: ProductionStepIngredientDtoRequest.Create[] = []
      for(const addedIngredient of addIProdStep.stepIngredients) {
        stepIngredients.push(this.toProductionStepIngredientCreateDto(addedIngredient))
      }
      return  {
        type: addIProdStep.type,
        stepIngredients: stepIngredients
      } as AddIngredientsProductionStepDtoRequest.Create
    }

    if((<WrittenInstructionProductionStepDtoResponse.Detailed>prodStep)) {
      const wIProdStep = (<WrittenInstructionProductionStepDtoResponse.Detailed>prodStep)
      return {
        type: wIProdStep.type,
        message: wIProdStep.message
      } as WrittenInstructionProductionStepDtoRequest.Create
    }
    throw 'ProductionStep-Type unknown: ' + prodStep.type
  }

  toProductionStepIngredientCreateDto(pStepIngredient: ProductionStepIngredientDtoResponse.Detailed) {
    return {
      amount: pStepIngredient.amount,
      scale: pStepIngredient.scale,
      ingredientId: pStepIngredient.ingredient.id
    } as ProductionStepIngredientDtoRequest.Create
  }
}

export default new RecipeService()
