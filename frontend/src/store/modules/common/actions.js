import SystemService from 'src/services/system.service'

export const fetchGlobalSettings = ({ commit }) => {
  return SystemService.getGlobalSettings().then(
    globalSettings => {
      commit('setGlobalSettings', globalSettings)
      return Promise.resolve(globalSettings)
    }
  )
}

export const fetchDefaultFilter = ({ commit }) => {
  return SystemService.getDefaultFilter().then(
    x => {
      commit('setDefaultFilter', x)
      return Promise.resolve(x)
    }
  )
}

export const fetchAppearanceSettings = ({ commit }, i18n) => {
  return SystemService.getAppearanceSettings().then(
    x => {
      commit('setAppearanceSettings', { payload: x, i18n: i18n })
      return Promise.resolve(x)
    }
  )
}
