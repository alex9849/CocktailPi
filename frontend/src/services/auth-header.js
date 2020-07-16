export default function authHeader() {
  let authToken = JSON.parse(localStorage.getItem('authToken'));
  if (authToken && authToken.accessToken) {
    return authToken.tokenType + ' ' + authToken.accessToken;
  } else {
    return '';
  }
}
