import { createI18n } from 'vue-i18n'
import { boot } from 'quasar/wrappers'
import messages from 'src/i18n'

export const i18n = createI18n({
  legacy: false,
  locale: 'en_US',
  fallbackLocale: 'en_US',
  globalInjection: true,
  messages
})

export default boot(({ app }) => {
  // Tell app to use the I18n instance
  app.use(i18n)
})
