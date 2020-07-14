import AuthService from '../../services/auth.service';

const user = JSON.parse(localStorage.getItem('user'));
const currentDate = new Date();
const initialState = function ()  {
  let status = { status: { loggedIn: false }, user: null };
  if(!user || !user.tokenExpiration)
    return status;
  user.tokenExpiration = new Date(user.tokenExpiration);
  if(user.tokenExpiration < currentDate) {
    return status;
  }
  status.user = user;
  status.status.loggedIn = true;
  return status;
}();


export const auth = {
  namespaced: true,
  state: initialState,
  getters: {
    isLoggedIn: state => {
      return state.status.loggedIn;
    },
    getUser: state => {
      return state.user;
    }
  },
  actions: {
    login({ commit }, user) {
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
    },
    refreshToken({ commit }) {
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
    },
    logout({ commit }) {
      commit('logout');
    }
  },
  mutations: {
    loginSuccess(state, user) {
      state.status.loggedIn = true;
      state.user = user;
      localStorage.setItem('user', JSON.stringify(user));
    },
    loginFailure(state) {
      state.status.loggedIn = false;
      state.user = null;
      localStorage.removeItem('user');
    },
    logout(state) {
      state.status.loggedIn = false;
      state.user = null;
      localStorage.removeItem('user');
    }
  }
};
