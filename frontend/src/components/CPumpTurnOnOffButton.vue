<template>
  <q-btn
    :icon="running ? mdiStop : mdiPlay"
    :color="running ? 'red' : 'green'"
    :disable="disable"
    @click="onClickTurnOnOrOffPump()"
    dense
    rounded
  >
    <template v-slot:default>
      <q-tooltip>
        {{ running ? 'Turn off' : 'Turn on' }}
      </q-tooltip>
    </template>
  </q-btn>
</template>

<script>
import { mdiPlay, mdiStop } from '@quasar/extras/mdi-v5'
import { mapGetters } from 'vuex'
import PumpService from 'src/services/pump.service'
import WebsocketService from 'src/services/websocket.service'

export default {
  name: 'CPumpTurnOnOffButton',
  props: {
    pumpId: {
      type: Number,
      required: true
    }
  },
  data () {
    return {
      running: false
    }
  },
  created () {
    this.mdiPlay = mdiPlay
    this.mdiStop = mdiStop
  },
  watch: {
    pumpId: {
      immediate: true,
      handler (newVal, oldVal) {
        if (oldVal !== undefined) {
          WebsocketService.unsubscribe(this, '/user/topic/pump/runningstate/' + String(oldVal))
        }

        WebsocketService.subscribe(this, '/user/topic/pump/runningstate/' + String(newVal), (data) => {
          this.running = !!JSON.parse(data.body).runningState
        }, true)
      }
    }
  },
  unmounted () {
    WebsocketService.unsubscribe(this, '/user/topic/pump/runningstate/' + String(this.pumpId))
  },
  methods: {
    onClickTurnOnOrOffPump () {
      const vm = this
      if (this.running) {
        PumpService.stopPump(this.pumpId).then(() => {
          vm.$q.notify({
            type: 'positive',
            message: 'Pump #' + String(this.pumpId) + ' stopped!'
          })
        })
      } else {
        PumpService.startPump(this.pumpId).then(() => {
          vm.$q.notify({
            type: 'positive',
            message: 'Pump #' + String(this.pumpId) + ' started!'
          })
        })
      }
    }
  },
  computed: {
    ...mapGetters({
      getPumpOccupation: 'pumpLayout/getPumpOccupation'
    }),
    disable () {
      return this.getPumpOccupation(this.pumpId) === 'COCKTAIL_PRODUCTION'
    },
    isPumpingUp () {
      return this.getPumpOccupation(this.pumpId) === 'PUMPING_UP'
    },
    isPumpTurnedOn () {
      return this.isPumpingUp || this.getPumpOccupation(this.pumpId) === 'RUNNING'
    }
  }
}
</script>

<style scoped>

</style>
