import SystemService from 'src/services/system.service'

export const performUpdate = async ({ commit }) => {
  const currentVersion = (await SystemService.getVersion()).version
  commit('setIsUpdateRunning', true)

  await SystemService.getCheckUpdate(true)
  SystemService.performUpdate()

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
