import { i18n } from 'boot/i18n'
import { calcTextColor } from 'src/mixins/utils'

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
    }
    settings.colors[areaKey] = areaColors
  }
  state.appearance = settings
}
