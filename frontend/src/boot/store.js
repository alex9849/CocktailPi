import { boot } from 'quasar/wrappers'
import store from '../store'

export default boot(({ app }) => {
  app.use(store)
})
