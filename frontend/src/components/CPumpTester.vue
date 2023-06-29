<template>
  <q-card>
    <q-tabs
      class="bg-teal-2 rounded-borders"
      no-caps
      align="justify"
      v-model:model-value="pumpTester.mode"
    >
      <q-tab name="runSteps" label="Steps"/>
      <q-tab name="runLiquid" label="Liquid"></q-tab>
    </q-tabs>
    <div class="row justify-center items-center bg-grey-3">
      <div class="col-grow">
        <q-input
          v-model:model-value="pumpTester.runVal"
          style="padding-inline: 12px"
          type="number"
          square
          hide-bottom-space
          borderless
          label="Steps to run"
          :disable="pumpTester.running"
        >
        </q-input>
      </div>
      <div class="col-shrink q-pr-sm q-py-sm">
        <q-btn
          @click="runTester"
          v-if="!pumpTester.running"
          :icon="mdiPlay"
          label="Run"
          no-caps
          class="bg-green text-white"
        />
        <q-btn
          @click="cancelTester"
          v-else
          :icon="mdiStop"
          label="Stop"
          no-caps
          dark-percentage
          class="bg-red text-white"
        >
        </q-btn>
      </div>
    </div>
    <q-linear-progress
      :value="pumpTester.percentage / 100"
      :animation-speed="300"
      color="green"
    />
    <q-card-section
      v-if="pumpTester.result"
    >
      <div class="row justify-center items-center q-gutter-md">
        <div class="col-shrink">
          <table>
            <tr>
              <td><b>Steps transmitted:</b></td>
              <td>100</td>
            </tr>
            <tr>
              <td><b>Seconds taken:</b></td>
              <td>20</td>
            </tr>
          </table>
        </div>
        <div class="col-grow">
          <div class="row justify-center items-center q-col-gutter-sm">
            <div class="col">
              <q-input
                filled
                dense
                outlined
                label="ml pumped"
                v-model:model-value="pumpTester.liquidPumpedField"
              />
            </div>
            <div class="col-shrink">
              <q-icon
                :name="mdiEqual"
                size="lg"
                class="text-grey-8"
              />
            </div>
            <div class="col">
              <q-input
                filled
                dense
                readonly
                outlined
                disable
                label="steps/cl"
                v-model:model-value="pumpTester.liquidPumpedField"
              />
            </div>
          </div>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import {
  mdiEqual,
  mdiPlay,
  mdiStop
} from '@quasar/extras/mdi-v5'

export default {
  name: 'CPumpTester',
  data: () => {
    return {
      pumpTester: {
        mode: 'runSteps',
        runVal: '',
        percentage: 0,
        running: false,
        result: false,
        intervalTask: 0,
        liquidPumpedField: ''
      }
    }
  },
  created () {
    this.mdiPlay = mdiPlay
    this.mdiStop = mdiStop
    this.mdiEqual = mdiEqual
  },
  methods: {
    runTester () {
      this.pumpTester.running = true
      this.pumpTester.result = false
      this.pumpTester.percentage = 0
      this.pumpTester.intervalTask = setInterval(() => {
        this.pumpTester.percentage += 10
        if (this.pumpTester.percentage >= 100) {
          this.cancelTester()
        }
      }, 250)
    },
    cancelTester () {
      if (this.pumpTester.intervalTask) {
        clearInterval(this.pumpTester.intervalTask)
        this.pumpTester.result = true
        this.pumpTester.running = false
        this.pumpTester.percentage = 0
      }
    }
  }
}
</script>

<style scoped>

</style>
