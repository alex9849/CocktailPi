import SystemService from 'src/services/system.service'

export const performUpdate = async ({ commit }) => {
  const currentVersion = (await SystemService.getVersion()).version

  await SystemService.getCheckUpdate(true)
  try {
    SystemService.performUpdate()
  } catch (e) {
    if (e.response.status !== 504) {
      throw e
    }
  }

  commit('setIsUpdateRunning', true)

  let version = currentVersion
  while (version === currentVersion) {
    await new Promise(resolve => setTimeout(resolve, 1000))
    try {
      version = (await SystemService.getVersion()).version
    } catch (e) {
    }
  }
  location.reload()
}
