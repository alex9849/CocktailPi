import SystemService from 'src/services/system.service'

export const fetchGlobalSettings = ({ commit }) => {
  return SystemService.getGlobalSettings().then(
    globalSettings => {
      commit('setGlobalSettings', globalSettings)
      return Promise.resolve(globalSettings)
    }
  )
}
