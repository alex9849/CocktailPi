<template>
  <q-checkbox
    :model-value="isPumpedUp(pumpId)"
    @update:model-value="toggle($event)"
    :disable="disable"
    :checked-icon="isPumpingUp ? mdiTimerSandEmpty : mdiCheckCircleOutline"
    :unchecked-icon="isPumpingUp ? mdiTimerSandEmpty : mdiCloseCircleOutline"
    :color="color"
    keep-color
  />
</template>

<script>
import { mapGetters } from 'vuex'
import { mdiCheckCircleOutline, mdiCloseCircleOutline, mdiTimerSandEmpty } from '@quasar/extras/mdi-v5'
import PumpService, { pumpDtoMapper } from 'src/services/pump.service'

export default {
  name: 'CPumpedUpIconButton',
  props: {
    pumpId: {
      type: Number,
      required: true
    },
    readOnly: {
      type: Boolean,
      default: true
    }
  },
  created () {
    this.mdiCheckCircleOutline = mdiCheckCircleOutline
    this.mdiCloseCircleOutline = mdiCloseCircleOutline
    this.mdiTimerSandEmpty = mdiTimerSandEmpty
  },
  methods: {
    toggle (state) {
      PumpService.patchPump(this.pumpId, pumpDtoMapper.toPumpPatchDto({
        pumpedUp: state
      }))
    }
  },
  computed: {
    ...mapGetters({
      getPumpOccupation: 'pumpLayout/getPumpOccupation',
      isPumpedUp: 'pumpLayout/isPumpedUp'
    }),
    isPumpingUp () {
      return this.getPumpOccupation(this.pumpId) === 'PUMPING_UP'
    },
    disable () {
      return this.readOnly || this.getPumpOccupation(this.pumpId) !== 'NONE'
    },
    color () {
      if (this.isPumpingUp) {
        return 'orange'
      }
      if (this.isPumpedUp(this.pumpId)) {
        return 'positive'
      } else {
        return 'negative'
      }
    }
  }
}
</script>

<style scoped>

</style>
