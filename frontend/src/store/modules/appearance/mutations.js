import { i18n } from 'boot/i18n'
import { calcTextColor, isDark, complementColor } from 'src/mixins/utils'
import { setCssVar, colors } from 'quasar'

export const setAppearanceSettings = (state, payload) => {
  i18n.global.locale = payload.language.name
  const settings = {
    language: payload.language,
    colors: {}
  }
  if (colors.brightness(payload.colors.normal.cardBody) > 240) {
    payload.colors.normal.cardBodyTableOdd = '#FFFFFF'
  } else {
    payload.colors.normal.cardBodyTableOdd = colors.lighten(payload.colors.normal.cardBody, 3)
  }
  payload.colors.normal.cardBackgroundInfoIcon = complementColor(payload.colors.normal.background, 50)
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
  setCssVar('navigation-active', settings.colors.normal.btnNavigationActive)
  setCssVar('navigation-active-text', settings.colors.normal.btnNavigationActiveText)
  setCssVar('sidebar', settings.colors.normal.sidebar)
  setCssVar('header', settings.colors.normal.header)
  setCssVar('header-text', settings.colors.normal.headerText)
  setCssVar('background', settings.colors.normal.background)
  setCssVar('background-text', settings.colors.normal.backgroundText)
  setCssVar('background-info-icon', settings.colors.normal.cardBackgroundInfoIcon)
  setCssVar('card-header', settings.colors.normal.cardHeader)
  setCssVar('card-header-text', settings.colors.normal.cardHeaderText)
  setCssVar('card-body', settings.colors.normal.cardBody)
  setCssVar('card-body-text', settings.colors.normal.cardBodyText)
  setCssVar('card-body-table-odd', settings.colors.normal.cardBodyTableOdd)
  setCssVar('card-body-table-odd-text', settings.colors.normal.cardBodyTableOddText)
  setCssVar('card-item-group', settings.colors.normal.cardItemGroup)
  setCssVar('card-item-group-text', settings.colors.normal.cardItemGroupText)

  setCssVar('sv-background', settings.colors.simpleView.background)
  setCssVar('sv-background-text', settings.colors.simpleView.backgroundText)
  setCssVar('sv-header', settings.colors.simpleView.header)
  setCssVar('sv-header-text', settings.colors.simpleView.headerText)
  setCssVar('sv-btn-navigation', settings.colors.simpleView.btnNavigation)
  setCssVar('sv-btn-navigation-text', settings.colors.simpleView.btnNavigationText)
  setCssVar('sv-btn-navigation-active', settings.colors.simpleView.btnNavigationActive)
  setCssVar('sv-btn-navigation-active-text', settings.colors.simpleView.btnNavigationActiveText)
  setCssVar('sv-btn-primary', settings.colors.simpleView.btnPrimary)
  setCssVar('sv-btn-primary-text', settings.colors.simpleView.btnPrimaryText)
  setCssVar('sv-sidebar', settings.colors.simpleView.sidebar)
  setCssVar('sv-sidebar-text', settings.colors.simpleView.sidebarText)
  setCssVar('sv-cocktailprogress', settings.colors.simpleView.cocktailProgress)
  setCssVar('sv-card-primary', settings.colors.simpleView.cardPrimary)
  setCssVar('sv-card-primary-text', settings.colors.simpleView.cardPrimaryText)
  state.appearance = settings
}
