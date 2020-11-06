import axios from 'axios';

const API_PATH = 'api/recipe/';

class RecipeService {
  createRecipe(recipe, image) {
    let uploadData = new FormData();
    const stringRecipe = JSON.stringify(recipe);
    const blobRecipe = new Blob([stringRecipe], {
      type: 'application/json'
    });
    uploadData.append("recipe", blobRecipe);
    if(image) {
      uploadData.append("image", image);
    }
    return axios.post(API_PATH, uploadData, {headers:{'Content-Type' :'multipart/form-data'}})
      .then(response => response.data);
  }

  getRecipes(page, ownerId, inPublic, searchName, inCategoryId) {
    let config = {
      params: {
        page,
        ownerId,
        inPublic,
        searchName,
        inCategory: inCategoryId
      }
    };
    return axios.get(API_PATH, config)
      .then(response => {
        for(let recipe of response.data.content) {
          recipe.lastUpdate = new Date(recipe.lastUpdate)
        }
        return response.data;
      });
  }

  getRecipe(id) {
    return axios.get(API_PATH + id)
      .then(response => {
        response.data.lastUpdate = new Date(response.data.lastUpdate)
        return response.data;
      });
  }

  updateRecipe(recipe, image, removeImage) {
    let uploadData = new FormData();
    const stringRecipe = JSON.stringify(recipe);
    const blobRecipe = new Blob([stringRecipe], {
      type: 'application/json'
    });
    uploadData.append("recipe", blobRecipe);
    if(image) {
      uploadData.append("image", image);
    }
    return axios.put(API_PATH + recipe.id + "?removeImage=" + !!removeImage, uploadData, {headers:{'Content-Type' :'multipart/form-data'}});
  }

  deleteRecipe(recipe) {
    return axios.delete(API_PATH + recipe.id);
  }
}

export default new RecipeService();
