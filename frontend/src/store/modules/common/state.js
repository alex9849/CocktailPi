export default function () {
  let storedLanguage = localStorage.getItem('language')
  if (!storedLanguage) {
    storedLanguage = 'en_US'
  }
  return {
    lastRecipeListRoute: null,
    showDonateDialog: false,
    externalLink: {
      asQrCode: false,
      destination: null,
      trigger: false
    },
    globalSettings: {
      allowReversePumping: false,
      donation: {
        donated: false,
        showDisclaimer: false,
        disclaimerDelay: 30000
      }
    },
    appearanceSettings: {
      language: storedLanguage
    },
    defaultFilter: {
      enable: false,
      filter: null
    }
  }
}
