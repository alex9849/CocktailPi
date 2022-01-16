<template>
  <q-dialog v-model="showWebsocketReconnectDialog" position="top" seamless>
    <q-banner class="bg-orange text-white" inline-actions rounded>
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
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'
import { Stomp } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import authHeader from 'src/services/auth-header'
import { mdiAlert } from '@quasar/extras/mdi-v5'

export default {
  name: 'AppSocket',
  data () {
    return {
      reconnectTasks: [],
      showWebsocketReconnectDialog: false,
      reconnectThrottleInSeconds: 5,
      secondsTillWebsocketReconnect: 0,
      stompClient: null
    }
  },
  setup () {
    return {
      mdiAlert: mdiAlert
    }
  },
  methods: {
    ...mapMutations({
      setCocktailProgress: 'cocktailProgress/setCocktailProgress',
      setPumpLayout: 'pumpLayout/setLayout'
    }),
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
      isLoggedIn: 'auth/isLoggedIn'
    })
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
  unmounted () {
    this.destroyWebsocket()
  }
}
</script>

<style scoped>

</style>
