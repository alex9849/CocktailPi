import { createStore } from 'vuex'
import auth from './modules/auth/index'
import cocktailProgress from './modules/cocktailprogress/index'
import pumpLayout from './modules/pumplayout/index'
import category from './modules/category/index'
import websocket from './modules/websocket/index'
import common from './modules/common/index'
import appearance from 'src/store/modules/appearance'
import updater from 'src/store/modules/updater'

const store = createStore({
  modules: {
    auth,
    cocktailProgress,
    pumpLayout,
    category,
    websocket,
    appearance,
    updater,
    common
  },

  // enable strict mode (adds overhead!)
  // for dev mode only
  strict: process.env.DEV
})

export default function (/* { ssrContext } */) {
  return store
}

export {
  store
}
