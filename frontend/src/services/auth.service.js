import axios from 'axios'

const API_PATH = 'api/auth/'

class AuthService {
  login (loginRequest) {
    return axios
      .post(API_PATH + 'login', loginRequest)
      .then(response => {
        // JwtResponse
        response.data.tokenExpiration = new Date(response.data.tokenExpiration)
        return response.data
      })
  }

  refreshToken (currentToken = null) {
    let config = null
    if (currentToken != null) {
      config = {
        headers: {
          Authorization: currentToken.tokenType + ' ' + currentToken.accessToken
        }
      }
    }
    return axios
      .get(API_PATH + 'refreshToken', config)
      .then(response => {
        // JwtResponse
        response.data.tokenExpiration = new Date(response.data.tokenExpiration)
        return response.data
      })
  }

  setPasswordOnly (passwordOnly) {
    return axios
      .put(API_PATH + 'passwordOnly', { passwordOnly })
      .then(response => {
        return response.data
      })
  }
}

export default new AuthService()
