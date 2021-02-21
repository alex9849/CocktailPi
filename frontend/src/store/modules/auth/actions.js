import AuthService from "src/services/auth.service";

export const login = ({ commit }, loginRequest) => {
  return AuthService.login(loginRequest).then(
    jwtResponse => {
      commit('loginSuccess', jwtResponse);
      return Promise.resolve(jwtResponse);
    },
    error => {
      commit('loginFailure');
      return Promise.reject(error);
    }
  );
};
export const refreshToken = ({ commit }) => {
  return AuthService.refreshToken()
    .then(jwtResponse => {
        commit('loginSuccess', jwtResponse);
        return Promise.resolve(jwtResponse);
      },
      error => {
        commit('loginFailure');
        return Promise.reject(error);
      });
};
export const logout = ({ commit }) => {
  commit('logout');
};

export const serverAddress = ({ commit }, serverAddress) => {
  let trimmed = serverAddress.trim();
  commit('serverAddress', serverAddress);
};
