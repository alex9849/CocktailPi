import axios from 'axios';

const API_PATH = 'api/cocktail/';

class CocktailService {

  order(recipeId) {
    return axios.put(API_PATH + recipeId);
  }

  cancelCocktail() {
    return axios.delete(API_PATH);
  }
}

export default new CocktailService();
