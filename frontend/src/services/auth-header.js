import store from '../store/index'

export default function authHeader() {
  let user = JSON.parse(localStorage.getItem('user'));
  if (user && user.accessToken) {
    return user.accessToken;
  } else {
    return '';
  }
}
