import axios from 'axios'
import RecipeService from './recipe.service'

const API_PATH = 'api/collection/'

class CollectionService {
  createCollection (name) {
    return axios.post(API_PATH, {
      name: name,
      description: 'My cool collection',
      completed: false
    })
  }

  getCollection (id) {
    return axios.get(API_PATH + String(id))
      .then(response => this.afterCollectionLoad(response.data))
  }

  updateCollection (createCollection, id, image, removeImage) {
    const uploadData = new FormData()
    const stringCollection = JSON.stringify(createCollection)
    const blobRecipe = new Blob([stringCollection], {
      type: 'application/json'
    })
    uploadData.append('collection', blobRecipe)
    if (image) {
      uploadData.append('image', image)
    }
    return axios.put(API_PATH + String(id) + '?removeImage=' + String(removeImage), uploadData, { headers: { 'Content-Type': 'multipart/form-data' } })
  }

  deleteCollection (id) {
    return axios.delete(API_PATH + String(id))
  }

  getCollectionsByUser (id) {
    const params = {
      ownerId: id
    }
    return axios.get(API_PATH, { params })
      .then(response => response.data.map(x => this.afterCollectionLoad(x)))
  }

  getCollections () {
    return axios.get(API_PATH)
      .then(response => response.data.map(x => this.afterCollectionLoad(x)))
  }

  addRecipeToCollection (collectionId, recipeId) {
    return axios.post(API_PATH + String(collectionId) + '/add', recipeId,
      { headers: { 'Content-Type': 'application/json' } })
  }

  removeRecipeFromCollection (collectionId, recipeId) {
    return axios.delete(API_PATH + String(collectionId) + '/' + String(recipeId))
  }

  getCollectionRecipes (collectionId) {
    return axios.get(API_PATH + String(collectionId) + '/recipes')
      .then(response => response.data.map(x => RecipeService.afterRecipeLoad(x)))
  }

  afterCollectionLoad (collection) {
    collection.lastUpdate = new Date(collection.lastUpdate)
    return collection
  }
}

export default new CollectionService()

export class CollectionDtoMapper {
  toCreateCollectionDto (detailed) {
    return {
      name: detailed.name,
      description: detailed.description,
      completed: detailed.completed
    }
  }
}

export const collectionDtoMapper = new CollectionDtoMapper()
