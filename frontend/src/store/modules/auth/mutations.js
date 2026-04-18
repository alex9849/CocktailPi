import { getAuthToken, getUser, isLoggedIn } from 'src/store/modules/auth/getters'

export const loginSuccess = (state, jwtResponse) => {
  const onlyToken = Object.assign({}, jwtResponse)
  delete onlyToken.user
  state.status.lastRoute = null
  state.status.history.push({
    user: jwtResponse.user,
    authToken: onlyToken
  })
  localStorage.setItem('authToken', JSON.stringify(onlyToken))
  setCurrentUser(state, jwtResponse.user)
}

export const updateToken = (state, jwtResponse) => {
  const onlyToken = Object.assign({}, jwtResponse)
  delete onlyToken.user
  if (isLoggedIn(state)) {
    state.status.history[state.status.history.length - 1].authToken = onlyToken
  }
  localStorage.setItem('authToken', JSON.stringify(onlyToken))
  setCurrentUser(state, jwtResponse.user)
}

export const onTokenInvalid = (state) => {
  state.status.history.clear()
  localStorage.removeItem('authToken')
  localStorage.removeItem('user')
}
export const logout = (state) => {
  state.status.history.pop()
  if (isLoggedIn(state)) {
    localStorage.setItem('user', JSON.stringify(getUser(state)))
    localStorage.setItem('authToken', JSON.stringify(getAuthToken(state)))
  } else {
    localStorage.removeItem('authToken')
    localStorage.removeItem('user')
  }
}
export const setCurrentUser = (state, user) => {
  if (isLoggedIn(state)) {
    state.status.history[state.status.history.length - 1].user = user
    localStorage.setItem('user', JSON.stringify(user))
  }
}
export const serverAddress = (state, serverAddress) => {
  state.status.serverAddress = serverAddress
  localStorage.setItem('serverAddress', serverAddress)
}
