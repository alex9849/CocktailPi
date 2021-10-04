import axios from 'axios';

const API_PATH = 'api/user/';

class UserService {
  getAllUsers() {
    return axios.get(API_PATH)
      .then(response => response.data);
  }

  getUser(userId) {
    return axios.get(API_PATH + userId)
      .then(response => response.data);
  }

  getMe() {
    return axios.get(API_PATH + 'current')
      .then(response => response.data);
  }

  getMyOwnedIngredients() {
    return axios.get(API_PATH + 'current/ownedingredients')
      .then(response => response.data);
  }

  addToMyOwnedIngredients(ingredientId) {
    return axios.put(API_PATH + 'current/ownedingredients/add', ingredientId, {headers: { 'Content-Type': 'application/json' }});
  }

  removeFromMyOwnedIngredients(ingredientId) {
    return axios.delete(API_PATH + 'current/ownedingredients/' + ingredientId);
  }

  updateMe(updateRequest) {
    return axios.put(API_PATH + 'current', updateRequest);
  }

  deleteUser(userId) {
    return axios.delete(API_PATH + userId);
  }

  updateUser(updateRequest) {
    return axios.put(API_PATH + updateRequest.userDto.id, updateRequest);
  }

  createUser(createUser) {
    return axios.post(API_PATH, createUser);
  }
}

export default new UserService();
