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
          v-model:model-value="advice.type"
        >
          <q-tab
            name="PUMP_ML"
            :disable="isRunning"
            label="Liquid"
            @click="reset()"
          />
          <q-tab
            name="PUMP_STEPS"
            :disable="isRunning"
            label="Steps"
            @click="reset()"
          />
        </q-tabs>
      </div>
    </div>
    <div class="row justify-center bg-grey-3 items-center">
      <div class="col-grow">
        <q-input
          v-model:model-value="advice.amount"
          style="padding-inline: 12px"
          type="number"
          square
          hide-bottom-space
          borderless
          :label="runValFieldLabel"
          :disable="isRunning"
        >
          <template v-slot:append>
            {{ runValSuffix }}
          </template>
        </q-input>
      </div>
      <div class="col-shrink q-pr-sm q-py-sm">
        <q-btn
          @click="onClickRun"
          v-if="!isRunning"
          :icon="mdiPlay"
          label="Run"
          no-caps
          class="bg-green text-white"
        />
        <q-btn
          @click="onClickCancel"
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
      :value="progressBar"
      :animation-speed="300"
      color="green"
    />
    <q-card-section
      v-if="!!jobMetrics"
    >
      <div class="row justify-center items-center q-gutter-md">
        <div class="col-shrink">
          <table>
            <tr>
              <td><b>Steps made:</b></td>
              <td>{{ jobMetrics.stepsMade }}</td>
            </tr>
            <tr>
              <td><b>Time taken:</b></td>
              <td>{{ jobMetrics.stopTime - jobMetrics.startTime }} ms</td>
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
                :disable="isRunning"
                label="ml pumped"
                v-model:model-value="trueLiquidPumpedField"
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
                v-model:model-value="trueLiquidPumpedField"
              >
                <template v-slot:append>
                  st/cl
                </template>
                <template v-slot:after>
                  <q-btn
                    @click="clickApplyMlPumpMetric"
                    no-caps
                    :dense="$q.screen.xs"
                    :disable="!trueLiquidPumpedField || isRunning"
                    class="bg-green text-white"
                    label="Apply"
                    :icon="this.applyMlPumpMetricIcon"
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
import WebSocketService from 'src/services/websocket.service'
import PumpService from 'src/services/pump.service'

export default {
  name: 'CPumpTester',
  props: {
    pumpId: {
      type: Number,
      required: true
    },
    pumpType: {
      type: String
    }
  },
  data: () => {
    return {
      advice: {
        type: 'PUMP_ML',
        amount: ''
      },
      runningJobId: null,
      applyMlPumpMetricIcon: mdiSync,
      trueLiquidPumpedField: '',
      jobState: {
        lastJobId: null,
        runningState: {
          jobId: 0,
          runInfinity: false,
          forward: true,
          percentage: 0
        }
      },
      jobMetrics: null
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
    onClickRun () {
      PumpService.dispatchPumpAdvice(this.pumpId, this.advice)
        .then(id => {
          this.runningJobId = id
        })
    },
    onClickCancel () {
      PumpService.stopPump(this.pumpId)
        .then(() => {
          this.runningJobId = null
        })
    },
    fetchMetrics () {
      PumpService.getMetrics(this.runningJobId)
        .then(metrics => {
          this.jobMetrics = Object.assign({}, metrics)
        })
    },
    reset () {
      this.runningJobId = null
      this.advice.amount = 0
      this.jobMetrics = null
    },
    clickApplyMlPumpMetric () {
      this.applyMlPumpMetricIcon = mdiCheck
      setTimeout(() => {
        this.applyMlPumpMetricIcon = mdiSync
      }, 2000)
    }
  },
  watch: {
    pumpId: {
      immediate: true,
      handler (newValue, oldValue) {
        if (newValue !== oldValue) {
          if (oldValue !== undefined) {
            WebSocketService.unsubscribe('/user/topic/runningstate/' + String(oldValue))
            this.jobState = Object.assign({}, {
              lastJobId: null,
              runningState: {
                jobId: 0,
                runInfinity: false,
                forward: true,
                percentage: 0
              }
            })
          }

          WebSocketService.subscribe('/user/topic/pump/runningstate/' + String(newValue), (data) => {
            this.jobState = Object.assign(this.jobState, JSON.parse(data.body))
            if (this.jobState.lastJobId && this.jobState.lastJobId === this.runningJobId) {
              this.fetchMetrics()
            }
          })
        }
      }
    }
  },
  unmounted () {
    WebSocketService.unsubscribe('/user/topic/pump/runningstate/' + String(this.pumpId))
  },
  computed: {
    isRunning () {
      return !!this.jobState.runningState?.jobId && this.jobState.runningState.jobId === this.runningJobId
    },
    progressBar () {
      if (!this.jobState.runningState) {
        return 0
      }
      const runningState = this.jobState.runningState
      return runningState.percentage / 100
    },
    runValFieldLabel () {
      if (this.advice.type === 'PUMP_ML') {
        return 'Ml to pump'
      }
      return 'Steps to run'
    },
    runValSuffix () {
      if (this.advice.type === 'PUMP_ML') {
        return 'ml'
      }
      return 'st'
    }
  }
}
</script>

<style scoped>

</style>
