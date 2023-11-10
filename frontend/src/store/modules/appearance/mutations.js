import { i18n } from 'boot/i18n'
import { calcTextColor, isDark } from 'src/mixins/utils'
import { setCssVar, colors } from 'quasar'

export const setAppearanceSettings = (state, payload) => {
  i18n.global.locale = payload.language.name
  const settings = {
    language: payload.language,
    colors: {}
  }
  for (const areaKey in payload.colors) {
    const areaColors = {}
    for (const colorKey in payload.colors[areaKey]) {
      const color = payload.colors[areaKey][colorKey]
      const textColor = calcTextColor(color)
      areaColors[colorKey] = color
      areaColors[colorKey + 'Text'] = textColor
      areaColors[colorKey + 'Dark'] = isDark(color)
    }
    settings.colors[areaKey] = areaColors
  }

  setCssVar('primary', settings.colors.normal.btnPrimary)
  setCssVar('navigation', settings.colors.normal.btnNavigation)
  setCssVar('navigation-active', settings.colors.normal.btnNavigationActive)
  setCssVar('sidebar', settings.colors.normal.sidebar)
  setCssVar('header', settings.colors.normal.header)
  setCssVar('header-text', settings.colors.normal.headerText)
  state.appearance = settings
}
