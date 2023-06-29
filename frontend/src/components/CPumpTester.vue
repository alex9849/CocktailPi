<template>
  <q-card>
    <div class="row items-center justify-center bg-teal-3">
      <div class="col-shrink">
        <p class="text-subtitle2 q-pa-sm">Metric:</p>
      </div>
      <div class="col-grow">
        <q-tabs
          class="rounded-borders bg-teal-3"
          no-caps
          stretch
          active-bg-color="teal-4"
          align="justify"
          v-model:model-value="pumpTester.mode"
        >
          <q-tab
            name="runSteps"
            :disable="pumpTester.running"
            label="Steps"
            @click="reset()"
          />
          <q-tab
            name="runLiquid"
            :disable="pumpTester.running"
            label="Liquid"
            @click="reset()"
          />
        </q-tabs>
      </div>
    </div>
    <div class="row justify-center bg-grey-3 items-center">
      <div class="col-grow">
        <q-input
          v-model:model-value="pumpTester.runVal"
          style="padding-inline: 12px"
          type="number"
          square
          hide-bottom-space
          borderless
          :label="runValFieldLabel"
          :disable="pumpTester.running"
        >
          <template v-slot:append>
            {{runValSuffix}}
          </template>
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
              <td>{{ pumpTester.running ? '...' : resultData.stepsTransmitted }}</td>
            </tr>
            <tr>
              <td><b>Seconds taken:</b></td>
              <td>{{ pumpTester.running ? '...' : resultData.timeTaken }}</td>
            </tr>
          </table>
        </div>
        <div class="col-grow">
          <div class="row justify-center items-center q-col-gutter-sm">
            <div class="col-12 col-lg">
              <q-input
                filled
                dense
                outlined
                :disable="pumpTester.running"
                label="ml pumped"
                v-model:model-value="pumpTester.liquidPumpedField"
              >
                <template v-slot:append>
                  ml
                </template>
              </q-input>
            </div>
            <div class="col-shrink">
              <q-icon
                :name="mdiEqual"
                size="lg"
                class="text-grey-8"
              />
            </div>
            <div class="col-12 col-lg">
              <q-input
                filled
                dense
                readonly
                outlined
                disable
                label="steps/cl"
                v-model:model-value="pumpTester.liquidPumpedField"
              >
                <template v-slot:append>
                  st/cl
                </template>
                <template v-slot:after>
                  <q-btn
                    @click="clickApplyMlPumpMetric"
                    no-caps
                    :dense="$q.screen.xs"
                    :disable="!pumpTester.liquidPumpedField || pumpTester.running"
                    class="bg-green text-white"
                    label="Apply"
                    :icon="this.pumpTester.applyMlPumpMetricIcon"
                  />
                </template>
              </q-input>
            </div>
          </div>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import { mdiCheck, mdiEqual, mdiPlay, mdiStop, mdiSync } from '@quasar/extras/mdi-v5'

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
        applyMlPumpMetricIcon: mdiSync,
        intervalTask: 0,
        liquidPumpedField: ''
      },
      resultData: {
        stepsTransmitted: 100,
        timeTaken: 20
      }
    }
  },
  created () {
    this.mdiPlay = mdiPlay
    this.mdiStop = mdiStop
    this.mdiEqual = mdiEqual
    this.mdiSync = mdiSync
    this.mdiCheck = mdiCheck
  },
  methods: {
    reset () {
      this.pumpTester.runVal = ''
      this.pumpTester.result = false
      this.pumpTester.liquidPumpedField = ''
    },
    runTester () {
      this.pumpTester.running = true
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
    },
    clickApplyMlPumpMetric () {
      this.pumpTester.applyMlPumpMetricIcon = mdiCheck
      setTimeout(() => {
        this.pumpTester.applyMlPumpMetricIcon = mdiSync
      }, 2000)
    }
  },
  computed: {
    runValFieldLabel () {
      if (this.pumpTester.mode === 'runSteps') {
        return 'Steps to run'
      }
      return 'Ml to pump'
    },
    runValSuffix () {
      if (this.pumpTester.mode === 'runSteps') {
        return 'st'
      }
      return 'ml'
    }
  }
}
</script>

<style scoped>

</style>
