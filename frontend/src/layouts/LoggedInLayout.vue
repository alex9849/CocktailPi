<template>
  <app-socket
    ref="appSocket"
  />
  <router-view/>
  <app-qr-link />
</template>

<style scoped>
</style>

<script>

import { store } from '../store'
import AppSocket from 'components/AppSocket'
import AppQrLink from 'components/AppQrLink.vue'

export default {
  name: 'LoggedInLayout',
  components: { AppQrLink, AppSocket },
  beforeRouteEnter (to, from, next) {
    Promise.all([
      store.dispatch('category/fetchCategories'),
      store.dispatch('auth/fetchCurrentUser'),
      store.dispatch('common/fetchGlobalSettings')
    ]).then(() => next())
      .catch(() => {
        const query = {
          redirectTo: to.fullPath
        }
        if (!query.redirectTo || query.redirectTo === '/') {
          delete query.redirectTo
        }
        store.dispatch('auth/logout')
          .finally(() => next({
            name: 'login',
            query
          }))
      })
  }
}
</script>
