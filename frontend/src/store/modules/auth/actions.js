import AuthService from "src/services/auth.service";

export const login = ({ commit }, user) => {
  return AuthService.login(user).then(
    user => {
      user.tokenExpiration = new Date(user.tokenExpiration);
      commit('loginSuccess', user);
      return Promise.resolve(user);
    },
    error => {
      commit('loginFailure');
      return Promise.reject(error);
    }
  );
};
export const refreshToken = ({ commit }) => {
  return AuthService.refreshToken()
    .then(user => {
        user.tokenExpiration = new Date(user.tokenExpiration);
        commit('loginSuccess', user);
        return Promise.resolve(user);
      },
      error => {
        commit('loginFailure');
        return Promise.reject(error);
      });
};
export const logout = ({ commit }) => {
  commit('logout');
};
