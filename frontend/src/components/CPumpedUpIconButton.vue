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
    isPumpingUp: {
      type: Boolean,
      required: true
    },
    disable: {
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
      isPumpedUp: 'pumpLayout/isPumpedUp'
    }),
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
