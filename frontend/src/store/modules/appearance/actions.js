import SystemService from 'src/services/system.service'

export const fetchAppearanceSettings = ({ commit }) => {
  return SystemService.getAppearanceSettings().then(
    x => {
      commit('setAppearanceSettings', x)
      return Promise.resolve(x)
    }
  )
}
