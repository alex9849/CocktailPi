import axios from 'axios';
import RecipeService from './recipe.service'

const API_PATH = 'api/collection/';

class CollectionService {

  createCollection(name) {
    return axios.post(API_PATH, {
      name: name,
      description: "My cool collection",
      completed: false
    });
  };

  getCollection(id) {
    return axios.get(API_PATH + id)
      .then(response => response.data);
  }

  updateCollection(collection, image, removeImage) {
    let uploadData = new FormData();
    const stringCollection = JSON.stringify(collection);
    const blobRecipe = new Blob([stringCollection], {
      type: 'application/json'
    });
    uploadData.append("collection", blobRecipe);
    if(image) {
      uploadData.append("image", image);
    }
    return axios.put(API_PATH + collection.id + "?removeImage=" + !!removeImage, uploadData, {headers:{'Content-Type' :'multipart/form-data'}});

  };

  deleteCollection(collectionId) {
    return axios.delete(API_PATH + collectionId)
  };

  getCollectionsByUser(id) {
    let params = {
      ownerId: id
    };
    return axios.get(API_PATH, {params})
      .then(response => response.data);;
  };

  addRecipeToCollection(collectionId, recipeId) {
    return axios.post(API_PATH + collectionId + "/add", recipeId,
      {headers: { 'Content-Type': 'application/json' }})
  };

  removeRecipeFromCollection(collectionId, recipeId) {
    return axios.delete(API_PATH + collectionId + "/" + recipeId)
  };

  getCollectionRecipes(collectionId) {
    return axios.get(API_PATH + collectionId + "/recipes")
      .then(response => response.data.map(x => RecipeService.afterRecipeLoad(x)))
      .then(response => response.data);;
  }
}

export default new CollectionService();
