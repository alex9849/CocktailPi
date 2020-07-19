import axios from 'axios';

const API_PATH = 'api/recipe/';

class RecipeService {
  getRecipes(ownerId, inPublic, system) {
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
    if(system != null) {
      path += filterAdded?'&':'?';
      path += "system=" + system;
      filterAdded = true;
    }
    return axios.get(path)
      .then(response => response.data);
  }
}

export default new RecipeService();
