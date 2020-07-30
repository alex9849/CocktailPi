import axios from 'axios';

const API_PATH = 'api/recipe/';

class RecipeService {
  createRecipe(recipe) {
    return axios.post(API_PATH, recipe)
      .then(response => response.data);
  }

  getRecipes(ownerId, inPublic) {
    let path = API_PATH;
    let filterAdded = false;
    if(ownerId != null) {
      path += filterAdded?'&':'?';
      path += "ownerId=" + ownerId;
      filterAdded = true;
    }
    if(inPublic != null) {
      path += filterAdded?'&':'?';
      path += "inPublic=" + inPublic;
      filterAdded = true;
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
