import axios from 'axios';

const API_PATH = 'api/auth/';

class AuthService {
  login(user) {
    return axios
      .post(API_PATH + 'login', {
        username: user.username,
        password: user.password
      })
      .then(response => {
        return response.data;
      });
  }
}

export default new AuthService();
