export const loginSuccess = (state, jwtResponse) => {
  const onlyToken = Object.assign({}, jwtResponse);
  delete onlyToken.user;
  state.status.loggedIn = true;
  state.status.authToken = onlyToken;
  state.status.user = jwtResponse.user;
  localStorage.setItem('authToken', JSON.stringify(onlyToken));
  localStorage.setItem('user', JSON.stringify(jwtResponse.user));
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
