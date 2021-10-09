import {Platform} from 'quasar'

export const isLoggedIn = state => state.status.loggedIn;
export const getUser = state => state.status.user;
export const getAuthToken = state => state.status.authToken;
export const getServerAddress = state => state.status.serverAddress;
export const getFormattedServerAddress = state => {
  if(!Platform.is.cordova) {
    return "";
  }
  let address = state.status.serverAddress.toLowerCase();
  if(!/^((http:\/\/)|(https:\/\/)).+/.test(address)) {
    return "http://" + address;
  }
  return address;
};
export const getAdminLevel = state => {
  if(!getUser(state))
    return 0;
  return state.status.user.adminLevel;
};

export const isUser = state => {
  if(!getUser(state))
    return false;
  return state.status.user.adminLevel >= 1;
};

export const isRecipeCreator = state => {
  if(!getUser(state))
    return false;
  return state.status.user.adminLevel >= 2;
};
export const isPumpIngredientEditor = state => {
  if(!getUser(state))
    return false;
  return state.status.user.adminLevel >= 3;
};
export const isAdmin = state => {
  if(!getUser(state))
    return false;
  return state.status.user.adminLevel >= 4;
};
