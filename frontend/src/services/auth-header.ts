import {JwtResponseNoUser} from 'src/models/response/JwtResponse';

export default function authHeader () {
  const authToken = JSON.parse(<string>localStorage.getItem('authToken')) as JwtResponseNoUser
  if (authToken && authToken.accessToken) {
    return authToken.tokenType + ' ' + authToken.accessToken
  } else {
    return ''
  }
}
