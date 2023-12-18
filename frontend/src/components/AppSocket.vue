<template>
  <q-dialog
    v-model:model-value="showReconnectDialog"
    position="top"
    seamless
  >
    <q-banner class="bg-orange text-white" inline-actions rounded>
      Connection lost! Reconnecting in: {{ secondsTillWebsocketReconnect }}
      <template v-slot:avatar>
        <q-icon :name="mdiAlert"
        />
      </template>
      <template v-slot:action>
        <q-btn flat
               label="Reconnect now"
               @click="connectWebsocket()"
        />
      </template>
    </q-banner>
  </q-dialog>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'
import WebsocketService from '../services/websocket.service'
import { mdiAlert } from '@quasar/extras/mdi-v5'

export default {
  name: 'AppSocket',
  setup () {
    return {
      mdiAlert
    }
  },
  mounted () {
    const vm = this
    WebsocketService.subscribe(this, '/user/topic/cocktailprogress', cocktailProgressMessage => {
      if (cocktailProgressMessage.body === 'DELETE') {
        vm.setCocktailProgress(null)
      } else {
        const progress = JSON.parse(cocktailProgressMessage.body)
        progress.recipe.lastUpdate = new Date(progress.recipe.lastUpdate)
        vm.setCocktailProgress(progress)
      }
    }, true)
    WebsocketService.subscribe(this, '/user/topic/pump/layout', layoutMessage => {
      vm.setPumpLayout(JSON.parse(layoutMessage.body))
    }, true)
  },
  methods: {
    ...mapMutations({
      setCocktailProgress: 'cocktailProgress/setCocktailProgress',
      setPumpLayout: 'pumpLayout/setLayout'
    }),
    connectWebsocket () {
      WebsocketService.connectWebsocket()
    },
    disconnectWebsocket () {
      WebsocketService.unsubscribe(this, '/user/topic/cocktailprogress')
      WebsocketService.unsubscribe(this, '/user/topic/pump/layout')
      WebsocketService.disconnectWebsocket()
    }
  },
  computed: {
    ...mapGetters({
      isLoggedIn: 'auth/isLoggedIn',
      showReconnectDialog: 'websocket/isShowReconnectDialog',
      secondsTillWebsocketReconnect: 'websocket/getSecondsTillWebsocketReconnect'
    })
  },
  watch: {
    isLoggedIn: {
      immediate: true,
      handler (val) {
        if (val) {
          this.connectWebsocket()
        } else {
          this.disconnectWebsocket()
        }
      }
    }
  },
  unmounted () {
    this.disconnectWebsocket()
  }
}
</script>

<style scoped>

</style>
