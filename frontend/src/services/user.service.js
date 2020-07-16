import axios from 'axios';
import authHeader from './auth-header';

const API_PATH = 'api/user/';

class UserService {
  getAllUsers() {
    return axios.get(API_PATH)
      .then(response => response.data);
  }

  deleteUser(user) {

  }

  getUserBoard() {
    return axios.get(API_PATH + 'user', {headers: authHeader()});
  }

  getAdminBoard() {
    return axios.get(API_PATH + 'admin', {headers: authHeader()});
  }
}

export default new UserService();
