import Vue from 'vue'
import Vuex from 'vuex'
import auth from './modules/auth/index';
import cocktailProgress from './modules/cocktailprogress/index'
import pumpLayout from './modules/pumplayout/index'
import bar from './modules/bar/index'

// import example from './module-example'

Vue.use(Vuex);

/*
 * If not building with SSR mode, you can
 * directly export the Store instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Store instance.
 */
const store = new Vuex.Store({
  modules: {
    auth: auth,
    cocktailProgress: cocktailProgress,
    pumpLayout: pumpLayout,
    bar: bar
  },

  // enable strict mode (adds overhead!)
  // for dev mode only
  strict: process.env.DEV
});

export default function (/* { ssrContext } */) {
  return store;
}

export {
  store
};
