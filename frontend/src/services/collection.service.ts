import axios from 'axios'
import RecipeService from './recipe.service'
import CollectionDtoRequest from 'CollectionDto/Request'
import CollectionDtoResponse from 'CollectionDto/Response'
import RecipeDtoResponse from 'RecipeDto/Response'

const API_PATH = 'api/collection/'

class CollectionService {
  createCollection (name: string) {
    return axios.post<CollectionDtoResponse.Detailed>(API_PATH, {
      name: name,
      description: 'My cool collection',
      completed: false
    } as CollectionDtoRequest.Create)
  }

  getCollection (id: bigint) {
    return axios.get<CollectionDtoResponse.Detailed>(API_PATH + String(id))
      .then(response => this.afterCollectionLoad(response.data))
  }

  updateCollection (collection: CollectionDtoRequest.Create, id: bigint, image: Blob, removeImage: boolean) {
    const uploadData = new FormData()
    const stringCollection = JSON.stringify(collection)
    const blobRecipe = new Blob([stringCollection], {
      type: 'application/json'
    })
    uploadData.append('collection', blobRecipe)
    if (image) {
      uploadData.append('image', image)
    }
    return axios.put(API_PATH + String(id) + '?removeImage=' + String(removeImage), uploadData, { headers: { 'Content-Type': 'multipart/form-data' } })
  }

  deleteCollection (id: bigint) {
    return axios.delete(API_PATH + String(id))
  }

  getCollectionsByUser (id: bigint) {
    const params = {
      ownerId: id
    }
    return axios.get<CollectionDtoResponse.Detailed[]>(API_PATH, { params })
      .then(response => response.data.map(x => this.afterCollectionLoad(x)))
  }

  addRecipeToCollection (collectionId: bigint, recipeId: bigint) {
    return axios.post(API_PATH + String(collectionId) + '/add', recipeId,
      { headers: { 'Content-Type': 'application/json' } })
  }

  removeRecipeFromCollection (collectionId: bigint, recipeId: bigint) {
    return axios.delete(API_PATH + String(collectionId) + '/' + String(recipeId))
  }

  getCollectionRecipes (collectionId: bigint) {
    return axios.get<RecipeDtoResponse.Detailed[]>(API_PATH + String(collectionId) + '/recipes')
      .then(response => response.data.map(x => RecipeService.afterRecipeLoad(x)))
  }

  afterCollectionLoad (collection: CollectionDtoResponse.Detailed) {
    collection.lastUpdate = new Date(collection.lastUpdate)
    return collection
  }
}

export default new CollectionService()
