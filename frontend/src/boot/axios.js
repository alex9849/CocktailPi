import { boot } from 'quasar/wrappers'
import axios from 'axios'
import authHeader from 'src/services/auth-header'
import store from '../store'

export default boot(({ app }) => {
  axios.defaults.onErrorNotify = true

  axios.interceptors.request.use(cfg => {
    cfg.headers.Authorization = authHeader()
    cfg.baseURL = process.env.API_BASE_URL || store().getters['auth/getFormattedServerAddress']
    return cfg
  })
  axios.interceptors.response.use(cfg => cfg, error => {
    if (error?.config?.onErrorNotify && error?.response?.data?.message) {
      app.config.globalProperties.$q.notify({
        type: 'negative',
        message: error.response.data.message
      })
    }
    throw error
  })
  app.config.globalProperties.$axios = axios
})
export { axios }
