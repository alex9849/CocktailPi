import { createStore } from 'vuex'
import auth from './modules/auth/index'
import cocktailProgress from './modules/cocktailprogress/index'
import pumpLayout from './modules/pumplayout/index'
import category from './modules/category/index'
import bar from './modules/bar/index'

export default function (/* { ssrContext } */) {
  const Store = createStore({
    modules: {
      auth: auth,
      cocktailProgress: cocktailProgress,
      pumpLayout: pumpLayout,
      category: category,
      bar: bar
    },

    // enable strict mode (adds overhead!)
    // for dev mode only
    strict: process.env.DEV
  })

  return Store
}
