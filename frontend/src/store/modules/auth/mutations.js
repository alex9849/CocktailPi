export const loginSuccess = (state, user) => {
  state.status.loggedIn = true;
  state.status.user = user;
  localStorage.setItem('user', JSON.stringify(user));
};
export const loginFailure = (state) => {
  state.status.loggedIn = false;
  state.status.user = null;
  localStorage.removeItem('user');
};
export const logout = (state) => {
  state.status.loggedIn = false;
  state.user = null;
  localStorage.removeItem('user');
};
