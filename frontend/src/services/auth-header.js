export default function authHeader() {
  let user = JSON.parse(localStorage.getItem('user'));
  if (user && user.accessToken) {
    return user.tokenType + ' ' + user.accessToken;
  } else {
    return '';
  }
}
