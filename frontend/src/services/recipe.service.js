import axios from 'axios';

const API_PATH = 'api/recipe/';

class RecipeService {
  createRecipe(recipe) {
    return axios.post(API_PATH, recipe)
      .then(response => response.data);
  }

  getRecipes(page, ownerId, inPublic, searchName) {
    let path = API_PATH + "?page=" + page;
    if(ownerId != null) {
      path += "&ownerId=" + ownerId;
    }
    if(inPublic != null) {
      path += "&inPublic=" + inPublic;
    }
    if(searchName != null) {
      path += "&searchName=" + searchName;
    }
    return axios.get(path)
      .then(response => response.data);
  }

  getRecipe(id) {
    return axios.get(API_PATH + id)
      .then(response => response.data);
  }

  updateRecipe(recipe) {
    return axios.put(API_PATH + recipe.id, recipe);
  }

  deleteRecipe(recipe) {
    return axios.delete(API_PATH + recipe.id);
  }
}

export default new RecipeService();
