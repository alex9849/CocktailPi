import Vue from 'vue'
import axios from 'axios'
import authHeader from "src/services/auth-header";
import {store} from '../store'

Vue.prototype.$axios = axios;
Vue.prototype.$axios.defaults.baseURL = window.location.origin;
Vue.prototype.$axios.interceptors.request.use(cfg => {
  cfg.headers.Authorization = authHeader();
  cfg.baseURL = store.getters['auth/getFormattedServerAddress'];
  return cfg;
});

