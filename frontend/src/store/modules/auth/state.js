const authToken = JSON.parse(localStorage.getItem('authToken'))
const user = JSON.parse(localStorage.getItem('user'))
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

export default function () {
  const history = []
  if (initialAuthToken) {
    history.push({
      user,
      authToken: initialAuthToken
    })
  }
  return {
    status: {
      history,
      serverAddress
    }
  }
}
