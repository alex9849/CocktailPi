import { i18n } from 'boot/i18n'
import { calcTextColor, isDark, complementColor } from 'src/mixins/utils'
import { colors } from 'quasar'

export const setAppearanceSettings = (state, payload) => {
  i18n.global.locale.value = payload.language.name
  const settings = {
    language: payload.language,
    recipePageSize: payload.recipePageSize,
    colors: {}
  }
  if (colors.brightness(payload.colors.normal.cardBody) > 240) {
    payload.colors.normal.cardBodyTableOdd = '#FFFFFF'
  } else {
    payload.colors.normal.cardBodyTableOdd = colors.lighten(payload.colors.normal.cardBody, 10)
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

  const style = document.documentElement.style
  style.setProperty('--q-primary', settings.colors.normal.btnPrimary)
  style.setProperty('--q-primary-text', settings.colors.normal.btnPrimaryText)
  style.setProperty('--q-navigation-active', settings.colors.normal.btnNavigationActive)
  style.setProperty('--q-navigation-active-text', settings.colors.normal.btnNavigationActiveText)
  style.setProperty('--q-sidebar', settings.colors.normal.sidebar)
  style.setProperty('--q-header', settings.colors.normal.header)
  style.setProperty('--q-header-text', settings.colors.normal.headerText)
  style.setProperty('--q-background', settings.colors.normal.background)
  style.setProperty('--q-background-text', settings.colors.normal.backgroundText)
  style.setProperty('--q-background-info-icon', settings.colors.normal.cardBackgroundInfoIcon)
  style.setProperty('--q-card-header', settings.colors.normal.cardHeader)
  style.setProperty('--q-card-header-text', settings.colors.normal.cardHeaderText)
  style.setProperty('--q-card-body', settings.colors.normal.cardBody)
  style.setProperty('--q-card-body-text', settings.colors.normal.cardBodyText)
  style.setProperty('--q-card-body-table-odd', settings.colors.normal.cardBodyTableOdd)
  style.setProperty('--q-card-body-table-odd-text', settings.colors.normal.cardBodyTableOddText)
  style.setProperty('--q-card-item-group', settings.colors.normal.cardItemGroup)
  style.setProperty('--q-card-item-group-text', settings.colors.normal.cardItemGroupText)

  style.setProperty('--q-sv-background', settings.colors.simpleView.background)
  style.setProperty('--q-sv-background-text', settings.colors.simpleView.backgroundText)
  style.setProperty('--q-sv-header', settings.colors.simpleView.header)
  style.setProperty('--q-sv-header-text', settings.colors.simpleView.headerText)
  style.setProperty('--q-sv-btn-navigation', settings.colors.simpleView.btnNavigation)
  style.setProperty('--q-sv-btn-navigation-text', settings.colors.simpleView.btnNavigationText)
  style.setProperty('--q-sv-btn-navigation-active', settings.colors.simpleView.btnNavigationActive)
  style.setProperty('--q-sv-btn-navigation-active-text', settings.colors.simpleView.btnNavigationActiveText)
  style.setProperty('--q-sv-btn-primary', settings.colors.simpleView.btnPrimary)
  style.setProperty('--q-sv-btn-primary-text', settings.colors.simpleView.btnPrimaryText)
  style.setProperty('--q-sv-sidebar', settings.colors.simpleView.sidebar)
  style.setProperty('--q-sv-sidebar-text', settings.colors.simpleView.sidebarText)
  style.setProperty('--q-sv-cocktailprogress', settings.colors.simpleView.cocktailProgress)
  style.setProperty('--q-sv-card-primary', settings.colors.simpleView.cardPrimary)
  style.setProperty('--q-sv-card-primary-text', settings.colors.simpleView.cardPrimaryText)
  state.appearance = settings
}
