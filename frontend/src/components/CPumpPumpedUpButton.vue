<template>
  <q-checkbox
    :model-value="isPumpedUp(pumpId)"
    @update:model-value="toggle($event)"
    :disable="readOnly || isPumpingUpOrDown(pumpId)"
    :checked-icon="isPumpingUpOrDown(pumpId) ? mdiTimerSandEmpty : mdiCheckCircle"
    :unchecked-icon="isPumpingUpOrDown(pumpId) ? mdiTimerSandEmpty : mdiCloseCircleOutline"
    :color="color"
    keep-color
  />
</template>

<script>
import { mapGetters } from 'vuex'
import { mdiCheckCircle, mdiCloseCircleOutline, mdiTimerSandEmpty } from '@quasar/extras/mdi-v5'

export default {
  name: 'CPumpPumpedUpButton',
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
    this.mdiCheckCircle = mdiCheckCircle
    this.mdiCloseCircleOutline = mdiCloseCircleOutline
    this.mdiTimerSandEmpty = mdiTimerSandEmpty
  },
  methods: {
    toggle (state) {
      // Todo: Implement
    }
  },
  computed: {
    ...mapGetters({
      isPumpingUpOrDown: 'pumpLayout/isPumpingUpOrDown',
      isPumpedUp: 'pumpLayout/isPumpedUp'
    }),
    color () {
      if (this.isPumpingUpOrDown(this.pumpId)) {
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
