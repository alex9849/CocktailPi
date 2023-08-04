<template>
  <q-btn
    :icon="pumpUpDirectionReversed ? mdiReply : mdiShare"
    color="green"
    @click="onClickPumpUp(pumpId)"
    dense
    rounded
    :disable="disable"
    :loading="loading"
  >
    <q-tooltip>
      {{ pumpUpDirectionReversed ? 'Pump back' : 'Pump up' }}
    </q-tooltip>
  </q-btn>
</template>

<script>
import { mdiReply, mdiShare } from '@quasar/extras/mdi-v5'
import PumpService from 'src/services/pump.service'
import { mapGetters } from 'vuex'

export default {
  name: 'CPumpUpButton',
  props: {
    pumpId: {
      type: Number,
      reqiured: true
    },
    currentPumpDirectionReversed: {
      type: Boolean,
      required: true
    },
    pumpUpDirectionReversed: {
      type: Boolean,
      default: () => false
    },
    disable: {
      type: Boolean,
      default: false
    },
    running: {
      type: Boolean,
      default: false
    }
  },
  created () {
    this.mdiShare = mdiShare
    this.mdiReply = mdiReply
  },
  methods: {
    onClickPumpUp () {
      if (this.pumpUpDirectionReversed) {
        PumpService.pumpDown(this.pumpId)
      } else {
        PumpService.pumpUp(this.pumpId)
      }
    }
  },
  computed: {
    ...mapGetters({
      getPumpOccupation: 'pumpLayout/getPumpOccupation'
    }),
    loading () {
      return this.running &&
        (this.currentPumpDirectionReversed === this.pumpUpDirectionReversed)
    }
  }
}
</script>

<style scoped>

</style>
