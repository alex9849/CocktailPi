export const isLoggedIn = state => state.status.loggedIn;
export const getUser = state => state.status.user;
export const getAuthToken = state => state.status.authToken;
export const isAdmin = state => {
  if(!getUser(state))
    return false;
  return state.status.user.role.includes('admin');
};
