import { Platform } from 'quasar'

export const isLoggedIn = state => state.status.history.length > 0
export const getUser = state => isLoggedIn(state) ? state.status.history[state.status.history.length - 1].user : null
export const getAuthToken = state => isLoggedIn(state) ? state.status.history[state.status.history.length - 1].authToken : null
export const getLastRoute = state => isLoggedIn(state) ? state.status.history[state.status.history.length - 1].lastRoute : null
export const getServerAddress = state => state.status.serverAddress
export const allowLogback = state => state.status.history.length > 1
export const getLogbackUser = state => allowLogback(state) ? state.status.history[state.status.history.length - 2].user : null
export const getLogbackAuthToken = state => allowLogback(state) ? state.status.history[state.status.history.length - 2].authToken : null
export const getLogbackLastRoute = state => allowLogback(state) ? state.status.history[state.status.history.length - 2].lastRoute : null
export const getFormattedServerAddress = state => {
  if (!Platform.is.cordova) {
    return ''
  }
  const address = state.status.serverAddress.toLowerCase()
  if (!/^((http:\/\/)|(https:\/\/)).+/.test(address)) {
    return 'http://' + address
  }
  return address
}
export const getAdminLevel = state => {
  if (!getUser(state)) { return 0 }
  return getUser(state).adminLevel
}

export const isUser = state => {
  if (!getUser(state)) { return false }
  return getUser(state).adminLevel >= 1
}

export const isRecipeCreator = state => {
  if (!getUser(state)) { return false }
  return getUser(state).adminLevel >= 1
}
export const isPumpIngredientEditor = state => {
  if (!getUser(state)) { return false }
  return getUser(state).adminLevel >= 2
}
export const isAdmin = state => {
  if (!getUser(state)) { return false }
  return getUser(state).adminLevel >= 3
}
