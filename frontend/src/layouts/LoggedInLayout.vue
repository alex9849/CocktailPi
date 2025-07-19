<template>
  <app-socket
    ref="appSocket"
  />
  <router-view/>
  <app-donation-disclaimer
    ref="donationDisclaimer"
  />
  <app-qr-link />
</template>

<style scoped>
</style>

<script>

import { store } from '../store'
import AppSocket from 'components/AppSocket'
import AppQrLink from 'components/AppQrLink.vue'
import AppDonationDisclaimer from 'components/AppDonationDisclaimer.vue'

export default {
  name: 'LoggedInLayout',
  components: { AppDonationDisclaimer, AppQrLink, AppSocket },
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
