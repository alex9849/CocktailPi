import { boot } from 'quasar/wrappers'
import axios from 'axios'
import authHeader from 'src/services/auth-header'
import store from '../store'

export default boot(({ app }) => {
  axios.defaults.baseURL = window.location.origin
  axios.interceptors.request.use(cfg => {
    cfg.headers.Authorization = authHeader()
    cfg.baseURL = store().getters['auth/getFormattedServerAddress']
    return cfg
  })
  axios.interceptors.response.use(cfg => cfg, error => {
    app.config.globalProperties.$q.notify({
      type: 'negative',
      message: error.response.data.message
    })
    throw error
  })
  app.config.globalProperties.$axios = axios
})
export { axios }
