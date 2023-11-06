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
        {{ tooltip }}
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
      required: true
    },
    running: {
      type: Boolean,
      default: false
    }
  },
  created () {
    this.mdiPlay = mdiPlay
    this.mdiStop = mdiStop
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
      disable: 'cocktailProgress/hasCocktailProgress'
    }),
    tooltip () {
      if (this.running) {
        return this.$t('component.pump_turn_on_btn.tooltip_off')
      }
      return this.$t('component.pump_turn_on_btn.tooltip_on')
    }
  }
}
</script>

<style scoped>

</style>
