import AuthService from 'src/services/auth.service'
import { mapActions, mapGetters, mapMutations } from 'vuex'

export const logback = {
  computed: {
    ...mapGetters({
      allowLogback: 'auth/allowLogback',
      logbackUser: 'auth/getLogbackUser',
      logbackAuthToken: 'auth/getLogbackAuthToken',
      lastUserRoute: 'auth/getLastRoute'
    })
  },
  methods: {
    ...mapActions({
      storeLogback: 'auth/logback'
    }),
    ...mapMutations({
      setAuthToken: 'auth/updateToken',
      setCurrentUser: 'auth/setCurrentUser'
    }),
    logback () {
      if (!this.allowLogback) {
        return
      }
      const oldAdminLevel = this.logbackUser.adminLevel
      AuthService.refreshToken(this.logbackAuthToken)
        .then((tokenResponse) => {
          this.storeLogback()
            .then(() => {
              this.setAuthToken(tokenResponse)
              this.setCurrentUser(tokenResponse.user)
              if (tokenResponse.user.adminLevel >= oldAdminLevel) {
                this.$router.push(this.lastUserRoute)
              } else {
                this.$router.push({ name: 'dashboard' })
              }
              this.$q.notify({
                type: 'positive',
                message: this.$t('page.login.notifications.switched_back_to_user', {
                  username: tokenResponse.user.username
                })
              })
            })
        })
    }
  }
}
