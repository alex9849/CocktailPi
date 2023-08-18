export const setLastRecipeListRoute = (state, lastRecipeListRoute) => {
  state.lastRecipeListRoute = lastRecipeListRoute
}

export const setGlobalSettings = (state, payload) => {
  state.globalSettings = payload
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
