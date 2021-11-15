import {boot} from 'quasar/wrappers'
import axios from 'axios'
import authHeader from 'src/services/auth-header'
import {store} from '../store'

export default boot(({ Vue }) => {
  axios.defaults.baseURL = window.location.origin
  axios.interceptors.request.use(cfg => {
    cfg.headers.Authorization = authHeader()
    cfg.baseURL = store.getters['auth/getFormattedServerAddress']
    return cfg
  })
  Vue.prototype.$axios = axios
})
export { axios }
