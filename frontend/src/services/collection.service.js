import axios from 'axios';
import RecipeService from './recipe.service'

const API_PATH = 'api/collection/';

class CollectionService {

  createCollection(name) {
    return axios.put(API_PATH, {
      name: name,
      description: "My cool collection",
      completed: false
    });
  };

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
    return axios.get(API_PATH, {params});
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
      .then(response => response.data.map(x => RecipeService.afterRecipeLoad(x)));
  }
}

export default new CollectionService();
