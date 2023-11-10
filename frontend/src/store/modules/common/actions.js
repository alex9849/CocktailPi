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
