import axios from 'axios';

const API_PATH = 'api/auth/';

class AuthService {
  login(loginRequest) {
    return axios
      .post(API_PATH + 'login', loginRequest)
      .then(response => {
        //JwtResponse
        response.data.tokenExpiration = new Date(response.data.tokenExpiration);
        return response.data;
      });
  }
  refreshToken(token) {
    return axios
      .get(API_PATH + 'refreshToken')
      .then(response => {
        //JwtResponse
        response.data.tokenExpiration = new Date(response.data.tokenExpiration);
        return response.data;
      });
  }
}

export default new AuthService();
