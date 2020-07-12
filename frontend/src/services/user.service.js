import axios from 'axios';
import authHeader from './auth-header';

const API_PATH = 'api/test/';

class UserService {
  getPublicContent() {
    return axios.get(API_PATH + 'all');
  }

  getUserBoard() {
    return axios.get(API_PATH + 'user', { headers: authHeader() });
  }

  getAdminBoard() {
    return axios.get(API_PATH + 'admin', { headers: authHeader() });
  }
}

export default new UserService();
