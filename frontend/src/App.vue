<template>
  <q-ajax-bar
    color="red"
    position="top"
    size="3px"
    :hijack-filter="ajaxBarFilter"
  />
  <router-view/>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

const extendTokenBeforeExpirationInMs = 600000

export default {
  name: 'App',
  data () {
    return {
      tokenupdater: null
    }
  },
  created () {
    this.tokenupdater = setInterval(() => {
      if (this.getAuthToken) {
        const tokenExpiration = this.getAuthToken.tokenExpiration
        const currentDate = new Date()
        const refreshTime = new Date()
        refreshTime.setTime(tokenExpiration.getTime() - extendTokenBeforeExpirationInMs)
        if (refreshTime < currentDate) {
          this.refreshToken()
        }
      }
    }, extendTokenBeforeExpirationInMs / 3)
    if (this.getAuthToken) {
      const tokenExpiration = this.getAuthToken.tokenExpiration
      const currentDate = new Date()
      if (tokenExpiration > currentDate) {
        this.refreshToken()
      }
    }
  },
  beforeUnmount () {
    clearInterval(this.tokenupdater)
  },
  methods: {
    ...mapActions({
      refreshToken: 'auth/refreshToken'
    }),
    ajaxBarFilter (url) {
      return !/(.+)?\/websocket\/.+\/xhr_send(.+)?/.test(url)
    }
  },
  computed: {
    ...mapGetters('auth', ['isLoggedIn', 'getAuthToken'])
  }
}
</script>
