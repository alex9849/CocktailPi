import axios from 'axios';

const API_PATH = 'api/cocktail/';

class CocktailService {

  order(recipeId, amount) {
    return axios.put(API_PATH + recipeId, null, {
      params: {
        amount: amount
      }
    });
  }

  cancelCocktail() {
    return axios.delete(API_PATH);
  }

  continueProduction() {
    return axios.post(API_PATH + 'continueproduction')
  }
}

export default new CocktailService();
