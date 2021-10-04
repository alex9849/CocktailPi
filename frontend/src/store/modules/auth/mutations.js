export const loginSuccess = (state, jwtResponse) => {
  const onlyToken = Object.assign({}, jwtResponse);
  delete onlyToken.user;
  state.status.loggedIn = true;
  state.status.authToken = onlyToken;
  localStorage.setItem('authToken', JSON.stringify(onlyToken));
  setCurrentUser(state, jwtResponse.user)
};
export const loginFailure = (state) => {
  state.status.loggedIn = false;
  state.status.user = null;
  state.status.authToken = null;
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
};
export const logout = (state) => {
  state.status.loggedIn = false;
  state.status.user = null;
  state.status.authToken = null;
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
};
export const setCurrentUser = (state, user) => {
  state.status.user = user;
}
export const serverAddress = (state, serverAddress) => {
  state.status.serverAddress = serverAddress;
  localStorage.setItem('serverAddress', serverAddress);
};
