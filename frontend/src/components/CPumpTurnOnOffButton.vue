<template>
  <q-btn
    :icon="isPumpTurnedOn ? mdiStop : mdiPlay"
    :color="isPumpTurnedOn ? 'red' : 'green'"
    @click="onClickTurnOnOrOffPump()"
    dense
    rounded
  >
    <template v-slot:default>
      <q-tooltip>
        {{ isPumpTurnedOn ? 'Turn off' : 'Turn on' }}
      </q-tooltip>
    </template>
  </q-btn>
</template>

<script>
import { mdiPlay, mdiStop } from '@quasar/extras/mdi-v5'
import { mapGetters } from 'vuex'
import PumpService from 'src/services/pump.service'

export default {
  name: 'CPumpTurnOnOffButton',
  props: {
    pumpId: {
      type: Number,
      reqiured: true
    }
  },
  created () {
    this.mdiPlay = mdiPlay
    this.mdiStop = mdiStop
  },
  methods: {
    onClickTurnOnOrOffPump () {
      const vm = this
      if (this.isPumpTurnedOn) {
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
