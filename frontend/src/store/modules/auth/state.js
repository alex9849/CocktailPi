const user = JSON.parse(localStorage.getItem('user'));
const currentDate = new Date();
const initialUser = function ()  {
  let status = { status: { loggedIn: false }, user: null };
  if(!user || !user.tokenExpiration)
    return null;
  user.tokenExpiration = new Date(user.tokenExpiration);
  if(user.tokenExpiration < currentDate) {
    return null;
  }
  return user;
}();
const inistialLoggedIn = function () {
  return !!user
}();

export default function () {
  return {
    status: {
      user: initialUser,
      loggedIn: inistialLoggedIn
    }
  }
}
