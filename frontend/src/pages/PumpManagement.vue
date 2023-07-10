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
          :pump="examplePump"
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
      examplePump: {
        dtype: 'stepper',
        name: 'The name is cool',
        stepPin: '',
        enablePin: '',
        fillingLevelInMl: '',
        tube_capacity: '',
        steps_per_cl: '',
        min_step_delta: '',
        acceleration: '',
        pumpedUp: false
      }
    }
  }
}
</script>

<style scoped>
</style>
