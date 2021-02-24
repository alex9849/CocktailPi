const authToken = JSON.parse(localStorage.getItem('authToken'));
const user = JSON.parse(localStorage.getItem('user'));
let serverAddress = localStorage.getItem('serverAddress');

if(serverAddress === null || serverAddress === undefined) {
  serverAddress = "";
}

const currentDate = new Date();

const initialAuthToken = function ()  {

  let status = { status: { loggedIn: false }, user: null };
  if(!authToken || !authToken.tokenExpiration)
    return null;
  authToken.tokenExpiration = new Date(authToken.tokenExpiration);
  if(authToken.tokenExpiration < currentDate) {
    return null;
  }
  return authToken;
}();

const initialLoggedIn = function () {
  return !!initialAuthToken
}();

const initialUser = function() {
  if(initialAuthToken){
    return user;
  }
  return null;
}();

export default function () {
  return {
    status: {
      user: initialUser,
      authToken: initialAuthToken,
      loggedIn: initialLoggedIn,
      serverAddress
    }
  }
}
