export default function () {
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
    }
  }
}
