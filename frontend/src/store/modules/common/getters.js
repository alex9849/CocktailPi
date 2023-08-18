export const getLastRecipeListRoute = state => state.lastRecipeListRoute
export const isAllowReversePumping = state => state.globalSettings.allowReversePumping
export const isShowDonateDialog = state => state.showDonateDialog
export const showExternalLinksAsQrCode = state => state.externalLink.asQrCode
export const getExternalLink = state => state.externalLink.destination
export const getTrigger = state => state.externalLink.trigger
