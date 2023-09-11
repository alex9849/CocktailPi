<template>
  <q-dialog
    :model-value="show"
    @update:model-value="$emit('update:show', $event)"
  >
    <q-card
      class="full-width"
    >
      <q-card-section>
        <div class="text-center q-mb-lg">
          <h5>What type of pump do you want to add?</h5>
        </div>
        <div class="row justify-center q-col-gutter-lg">
          <div class="col-5">
            <q-card
              class="clickable"
              @click="$emit('clickType', 'dc')"
            >
              <q-card-section class="text-center">
                <q-icon size="lg" :name="mdiPump"/>
                <p class="text-bold">DC-Pump</p>
              </q-card-section>
            </q-card>
          </div>
          <div class="col-5">
            <q-card
              class="clickable"
              @click="$emit('clickType', 'stepper')"
            >
              <q-card-section class="text-center">
                <q-icon size="lg">
                  <img src="~assets/icons/stepper-motor.svg" />
                </q-icon>
                <p class="text-bold">Stepper-Pump</p>
              </q-card-section>
            </q-card>
          </div>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script>
import { defineComponent } from 'vue'
import { mdiProgressClock, mdiPump } from '@quasar/extras/mdi-v5'

export default defineComponent({
  name: 'CPumpSetupTypeSelector',
  props: {
    show: {
      type: Boolean,
      default: false
    }
  },
  created () {
    this.mdiPump = mdiPump
    this.mdiProgressClock = mdiProgressClock
  }
})
</script>

<style scoped>

</style>
