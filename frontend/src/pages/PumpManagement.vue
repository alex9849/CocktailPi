<template>
  <q-page class="page-content" padding>
    <c-pump-management/>
    <c-reverse-pumping-settings/>
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
        currentIngredient: { name: 'Test' },
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
