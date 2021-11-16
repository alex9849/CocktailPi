const authToken = JSON.parse(localStorage.getItem('authToken'))
let serverAddress = localStorage.getItem('serverAddress')

if (serverAddress === null || serverAddress === undefined) {
  serverAddress = ''
}

const currentDate = new Date()

const initialAuthToken = (function () {
  if (!authToken || !authToken.tokenExpiration) {
    return null
  }
  authToken.tokenExpiration = new Date(authToken.tokenExpiration)
  if (authToken.tokenExpiration < currentDate) {
    return null
  }
  return authToken
}())

const initialLoggedIn = (function () {
  return !!initialAuthToken
}())

export default function () {
  return {
    status: {
      user: null,
      authToken: initialAuthToken,
      loggedIn: initialLoggedIn,
      serverAddress
    }
  }
}
