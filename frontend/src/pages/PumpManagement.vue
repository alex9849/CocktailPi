<template>
  <q-page class="page-content" padding>
    <c-pump-management/>
    <c-reverse-pumping-settings/>
    <div class="row q-col-gutter-md">
      <div
        class="col-12 col-sm-6 col-lg-4"
        v-bind:key="i"
        v-for="i in 10"
      >
        <c-pump-card
          style="height: 100%"
          :pump="i % 2 === 0 ? examplePump : otherPump"
          show-detailed
        />
      </div>
    </div>
  </q-page>
</template>

<script>

import CPumpManagement from 'components/CPumpManagement'
import CReversePumpingSettings from 'components/CReversePumpingSettings'
import CPumpCard from 'components/CPumpCard.vue'

export default {
  name: 'PumpManagement',
  components: { CPumpCard, CReversePumpingSettings, CPumpManagement },
  data () {
    return {
      otherPump: {
        dtype: 'DcPump',
        name: 'The name is cool',
        fillingLevelInMl: '',
        tubeCapacityInMl: '',
        currentIngredient: '',
        pumpedUp: true,
        state: 'ready',
        pin: '',
        timePerClInMs: '',
        powerStateHigh: ''
      },
      examplePump: {
        dtype: 'StepperPump',
        name: 'The name is cool',
        fillingLevelInMl: '',
        tubeCapacityInMl: '',
        currentIngredient: '',
        pumpedUp: true,
        state: 'ready',
        stepPin: '',
        enablePin: '',
        stepsPerCl: '',
        maxStepsPerSecond: '',
        acceleration: ''
      }
    }
  }
}
</script>

<style scoped>
</style>
