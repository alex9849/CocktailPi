import axios from 'axios'
import LoginRequest from 'src/models/request/LoginRequest';
import JwtResponse from 'src/models/response/JwtResponse';

const API_PATH = 'api/auth/'

class AuthService {
  login (loginRequest: LoginRequest) {
    return axios
      .post<JwtResponse>(API_PATH + 'login', loginRequest)
      .then(response => {
        // JwtResponse
        response.data.tokenExpiration = new Date(response.data.tokenExpiration)
        return response.data
      })
  }

  refreshToken () {
    return axios
      .get<JwtResponse>(API_PATH + 'refreshToken')
      .then(response => {
        // JwtResponse
        response.data.tokenExpiration = new Date(response.data.tokenExpiration)
        return response.data
      })
  }
}

export default new AuthService()
