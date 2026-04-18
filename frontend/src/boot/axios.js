import { boot } from 'quasar/wrappers'
import axios from 'axios'
import authHeader from 'src/services/auth-header'
import store from '../store'

export default boot(({ app }) => {
  axios.defaults.baseURL = window.location.origin
  axios.defaults.onErrorNotify = true
  axios.interceptors.request.use(cfg => {
    if (!cfg.headers.Authorization) {
      cfg.headers.Authorization = authHeader()
    }
    cfg.baseURL = store().getters['auth/getFormattedServerAddress']
    return cfg
  })
  axios.interceptors.response.use(cfg => cfg, async error => {
    if (!error?.config?.onErrorNotify) {
      throw error
    }
    let data = error?.response?.data
    if (!data) {
      throw error
    }
    if (data instanceof Blob && data.type === 'application/json') {
      data = JSON.parse(await data.text())
    }
    if (data?.message) {
      app.config.globalProperties.$q.notify({
        type: 'negative',
        message: data.message
      })
    }
    throw error
  })
  app.config.globalProperties.$axios = axios
})
export { axios }
