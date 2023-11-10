import { i18n } from 'boot/i18n'

export const setLastRecipeListRoute = (state, lastRecipeListRoute) => {
  state.lastRecipeListRoute = lastRecipeListRoute
}

export const setGlobalSettings = (state, payload) => {
  state.globalSettings = payload
}

export const setAppearanceSettings = (state, payload) => {
  i18n.global.locale = payload.language.name
  payload.colors = {
    normal: {
      header: '#85452b',
      sidebar: '#bf947b',
      btnPrimary: '#85452b',
      btnNavigation: '#bf947b',
      btnNavigationActive: '#fddfb1',
      cardPrimary: '#f3f3f3',
      cardSecondary: '#fdfdfe'
    },
    simpleView: {
      header: '#1a237e',
      sidebar: '#616161',
      btnPrimary: '#616161',
      btnNavigation: '#616161',
      btnNavigationActive: '#b968c7',
      cocktailProgress: '#1b5e20'
    }
  }
  state.appearanceSettings = payload
}

export const setShowDonateDialog = (state, payload) => {
  state.showDonateDialog = payload
}

export const setShowExternalLinksAsQrCode = (state, payload) => {
  state.externalLink.asQrCode = payload
}

export const openExternalLink = (state, payload) => {
  state.externalLink.destination = payload
  state.externalLink.trigger = !state.externalLink.trigger
}

export const setDefaultFilter = (state, payload) => {
  state.defaultFilter = payload
}
