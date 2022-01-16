<template>
  <q-header reveal bordered>
    <q-dialog v-model="showWebsocketReconnectDialog" seamless position="top">
      <q-banner inline-actions rounded class="bg-orange text-white">
        Connection lost! Reconnecting in: {{ secondsTillWebsocketReconnect }}
        <template v-slot:avatar>
          <q-icon :name="mdiAlert"
                  color="primary"
          />
        </template>
        <template v-slot:action>
          <q-btn flat
                 label="Reconnect now"
                 @click="connectWebsocket(true)"
          />
        </template>
      </q-banner>
    </q-dialog>
    <q-toolbar>
      <slot name="left" />

      <q-toolbar-title>
        CocktailMaker
      </q-toolbar-title>

      <div>
        <circular-cocktail-progress />
        <q-btn-dropdown
          size="md"
          flat
          :label="username"
          :icon="mdiAccountBox"
        >
          <q-list separator bordered style="border-radius: 0px">
            <q-item clickable :to="{name: 'myprofile'}">
              <q-item-section avatar>
                <q-icon :name="mdiAccountBox"/>
              </q-item-section>
              <q-item-section>
                Profile
              </q-item-section>
            </q-item>
            <q-item clickable @click="logout()">
              <q-item-section avatar>
                <q-icon :name="mdiPower"/>
              </q-item-section>
              <q-item-section>
                Logout
              </q-item-section>
            </q-item>
          </q-list>

        </q-btn-dropdown>
      </div>
    </q-toolbar>
  </q-header>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import { mdiAccountBox, mdiAlert, mdiPower } from '@quasar/extras/mdi-v5'
import CircularCocktailProgress from './Circular-Cocktail-Progress'
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'
import authHeader from '../services/auth-header'

export default {
  name: 'AppHeader',
  components: { CircularCocktailProgress },
  data () {
    return {
      reconnectTasks: [],
      showWebsocketReconnectDialog: false,
      reconnectThrottleInSeconds: 5,
      secondsTillWebsocketReconnect: 0,
      stompClient: null
    }
  },
  methods: {
    ...mapMutations({
      setCocktailProgress: 'cocktailProgress/setCocktailProgress',
      setPumpLayout: 'pumpLayout/setLayout'
    }),
    ...mapActions({
      storeLogout: 'auth/logout'
    }),
    logout () {
      this.storeLogout()
      this.$router.push({ name: 'login' })
    },
    connectWebsocket (websocketAutoReconnect) {
      this.stompClient = Stomp.over(() => new SockJS(this.$store.getters['auth/getFormattedServerAddress'] + '/ws'))
      this.stompClient.connectHeaders = {
        Authorization: authHeader()
      }
      const vm = this
      this.stompClient.onConnect = function () {
        vm.reconnectThrottleInSeconds = 5
        vm.showWebsocketReconnectDialog = false
        vm.stompClient.subscribe('/user/topic/cocktailprogress', cocktailProgressMessage => {
          if (cocktailProgressMessage.body === 'DELETE') {
            vm.setCocktailProgress(null)
          } else {
            const progress = JSON.parse(cocktailProgressMessage.body)
            progress.recipe.lastUpdate = new Date(progress.recipe.lastUpdate)
            vm.setCocktailProgress(progress)
          }
        })
        vm.stompClient.subscribe('/user/topic/pumplayout', layoutMessage => {
          vm.setPumpLayout(JSON.parse(layoutMessage.body))
        })
      }
      if (!process.env.DEV) {
        this.stompClient.debug = function (str) {}
      }
      for (const id of this.reconnectTasks) {
        clearTimeout(id)
      }
      this.reconnectTasks = []
      this.stompClient.onWebSocketClose = function () {
        if (websocketAutoReconnect) {
          vm.showWebsocketReconnectDialog = true
          const reconnectThrottle = vm.reconnectThrottleInSeconds
          vm.reconnectThrottleInSeconds = Math.min(20, vm.reconnectThrottleInSeconds * 2)
          for (let i = reconnectThrottle; i > 0; i--) {
            vm.reconnectTasks.push(setTimeout(() => {
              vm.secondsTillWebsocketReconnect = i
            }, (reconnectThrottle - i) * 1000))
          }
          vm.reconnectTasks.push(setTimeout(() => {
            vm.connectWebsocket(true)
          }, reconnectThrottle * 1000))
        }
      }
      this.stompClient.activate()
    },
    disconnectWebsocket () {
      if (this.stompClient != null) {
        this.stompClient.onWebSocketClose = () => {}
        this.stompClient.deactivate()
      }
    },
    destroyWebsocket () {
      this.disconnectWebsocket()
    }
  },
  computed: {
    ...mapGetters({
      user: 'auth/getUser',
      isLoggedIn: 'auth/isLoggedIn'
    }),
    username () {
      if (this.isLoggedIn) {
        return this.user.username
      }
      return ''
    }
  },
  watch: {
    isLoggedIn: {
      immediate: true,
      handler (val) {
        if (val) {
          this.connectWebsocket(true)
        } else {
          this.destroyWebsocket()
        }
      }
    }
  },
  setup () {
    return {
      mdiAccountBox: mdiAccountBox,
      mdiPower: mdiPower,
      mdiAlert: mdiAlert
    }
  },
  unmounted () {
    this.destroyWebsocket()
  }
}
</script>

<style scoped>

</style>
