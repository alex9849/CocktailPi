import SystemService from 'src/services/system.service'

export const performUpdate = async ({ commit }) => {
  await SystemService.getCheckUpdate(true)
  try {
    SystemService.performUpdate()
  } catch (e) {
    if (e.response.status !== 504) {
      throw e
    }
  }

  commit('setIsUpdateRunning', true)

  let connectionLost = false
  while (!connectionLost) {
    await new Promise(resolve => setTimeout(resolve, 1000))
    try {
      await SystemService.getVersion()
    } catch (e) {
      connectionLost = true
    }
  }
  await new Promise(resolve => setTimeout(resolve, 9000))
  while (connectionLost) {
    await new Promise(resolve => setTimeout(resolve, 1000))
    try {
      await SystemService.getVersion()
      connectionLost = false
    } catch (e) {
    }
  }
  location.reload()
}
