<template>
  <div class="relative-position">
    <div v-if="disable || isUnknownJobRunning" style="z-index: 1; height: 100%; width: 100%" class="row justify-center items-center absolute">
      <div
        class="text-h6 text-italic rounded-borders q-pa-sm text-white"
        style="background: rgba(244, 152, 54, 0.9) !important;"
      >
        <div v-if="isUnknownJobRunning" class="row justify-center q-gutter-sm">
          <p>{{ $t('component.pump_tester.unknown_job_running') }}</p>
          <q-btn
            color="negative"
            label="Cancel job"
            @click="onClickCancel"
          />
        </div>
        <div v-else class="text-center">
          {{ printDisableReason }}
        </div>
      </div>
    </div>
    <q-card :class="{disabled: disable || isUnknownJobRunning}">
      <div class="row items-center justify-center bg-teal-3">
        <div class="col-shrink">
          <p class="text-subtitle2 q-pa-sm">{{ $t('component.pump_tester.ref_metric') }}</p>
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
              :label="$t('component.pump_tester.metrics.liquid')"
              @click="reset()"
            />
            <q-tab
              v-if="pump.type === 'stepper'"
              name="PUMP_STEPS"
              :disable="isRunning"
              :label="$t('component.pump_tester.metrics.steps')"
              @click="reset()"
            />
            <q-tab
              v-if="pump.type === 'dc'"
              name="PUMP_TIME"
              :disable="isRunning"
              :label="$t('component.pump_tester.metrics.time')"
              @click="reset()"
            />
          </q-tabs>
        </div>
      </div>
      <q-form
        @submit="onClickRun"
        class="row justify-center bg-grey-3 items-center"
      >
        <div class="col-grow">
          <q-input
            v-model:model-value="advice.amount"
            style="padding-inline: 12px"
            type="number"
            square
            hide-bottom-space
            borderless
            :label="runValField.label"
            :disable="isRunning"
          >
            <template v-slot:append>
              {{ runValField.suffix }}
            </template>
          </q-input>
        </div>
        <div class="col-shrink q-pr-sm q-py-sm">
          <q-btn
            @click="onClickRun"
            v-if="!isRunning"
            :disable="!isFinite(advice.amount) || advice.amount < 1"
            :icon="mdiPlay"
            :label="$t('component.pump_tester.run_btn_label')"
            no-caps
            class="bg-green text-white"
          />
          <q-btn
            @click="onClickCancel"
            v-else
            :icon="mdiStop"
            :label="$t('component.pump_tester.stop_btn_label')"
            no-caps
            class="bg-red text-white"
          >
          </q-btn>
        </div>
      </q-form>
      <q-linear-progress
        :value="progressBar"
        :query="jobState?.runningState?.runInfinity"
        :reverse="jobState?.runningState?.runInfinity"
        :animation-speed="300"
        color="green"
      />
      <q-card-section
        v-if="!!jobMetrics && !isRunning"
      >
        <div class="row justify-center items-center q-gutter-md">
          <div class="col-shrink">
            <table>
              <tbody>
                <tr v-if="pump.type === 'stepper'">
                  <td><b>{{ $t('component.pump_tester.metrics.steps_made') }}</b></td>
                  <td>{{ jobMetrics.stepsMade }}</td>
                </tr>
                <tr>
                  <td><b>{{ $t('component.pump_tester.metrics.liquid_pumped') }}</b></td>
                  <td>
                    <p>{{ jobMetrics.mlPumped }} ml <i>(should-value)</i></p>
                  </td>
                </tr>
                <tr>
                  <td><b>{{ $t('component.pump_tester.metrics.time_taken') }}</b></td>
                  <td>{{ jobMetrics.stopTime - jobMetrics.startTime }} ms</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div
            class="col-grow"
            v-if="pump.type !== 'valve'"
          >
            <q-form
              @submit="clickApplyMlPumpMetric"
              class="row justify-center items-center q-col-gutter-sm"
            >
              <div class="col-12 col-lg">
                <q-input
                  filled
                  dense
                  outlined
                  type="number"
                  :label="$t('component.pump_tester.true_liquid_pumped_field')"
                  v-model:model-value.number="trueLiquidPumpedField"
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
                  :model-value="calcedPerClMetricValue.val"
                >
                  <template v-slot:append>
                    {{ calcedPerClMetricValue.unit }}
                  </template>
                  <template v-slot:after>
                    <q-btn
                      @click="clickApplyMlPumpMetric"
                      no-caps
                      :dense="$q.screen.xs"
                      :disable="!calcedPerClMetricValue.val"
                      class="bg-green text-white"
                      :label="$t('component.pump_tester.apply_per_cl_metric_value_btn_label')"
                      :icon="this.applyMlPumpMetricIcon"
                    />
                  </template>
                </q-input>
              </div>
            </q-form>
          </div>
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script>
import { mdiCheck, mdiEqual, mdiPlay, mdiStop, mdiSync } from '@quasar/extras/mdi-v5'
import WebSocketService from 'src/services/websocket.service'
import PumpService from 'src/services/pump.service'

export default {
  name: 'CPumpTester',
  props: {
    pump: {
      type: Object,
      required: true
    },
    disable: {
      type: Boolean,
      default: false
    },
    disableReason: {
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
      if (!isFinite(this.advice.amount) || this.advice.amount < 1) {
        return
      }
      this.jobMetrics = null
      PumpService.dispatchPumpAdvice(this.pump.id, this.advice)
        .then(id => {
          this.runningJobId = id
          if (this.runningJobId === this.jobState.lastJobId) {
            this.fetchMetrics()
          }
        })
    },
    onClickCancel () {
      PumpService.stopPump(this.pump.id)
    },
    fetchMetrics () {
      PumpService.getMetrics(this.runningJobId)
        .then(metrics => {
          this.jobMetrics = Object.assign({}, metrics)
        })
    },
    reset () {
      this.runningJobId = null
      this.advice.amount = ''
      this.jobMetrics = null
    },
    clickApplyMlPumpMetric () {
      if (!this.calcedPerClMetricValue.val) {
        return
      }
      this.applyMlPumpMetricIcon = mdiCheck
      this.$emit('update:perClMetric', this.calcedPerClMetricValue.val)
      this.trueLiquidPumpedField = ''
      setTimeout(() => {
        this.applyMlPumpMetricIcon = mdiSync
      }, 2000)
    }
  },
  watch: {
    pump: {
      immediate: true,
      handler (newValue, oldValue) {
        if (newValue.id !== oldValue?.id) {
          if (oldValue !== undefined) {
            WebSocketService.unsubscribe(this, '/user/topic/runningstate/' + String(oldValue.id))
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

          WebSocketService.subscribe(this, '/user/topic/pump/runningstate/' + String(newValue.id), (data) => {
            this.jobState = Object.assign(this.jobState, JSON.parse(data.body))
            if (this.jobState.lastJobId && this.jobState.lastJobId === this.runningJobId) {
              this.fetchMetrics()
            }
          }, true)
        }
      }
    }
  },
  unmounted () {
    WebSocketService.unsubscribe(this, '/user/topic/pump/runningstate/' + String(this.pump.id))
  },
  computed: {
    isRunning () {
      return !!this.jobState.runningState?.jobId && this.jobState.runningState.jobId === this.runningJobId
    },
    isUnknownJobRunning () {
      if (!this.jobState.runningState || !this.jobState.runningState.jobId) {
        return false
      }
      return this.jobState.runningState.jobId !== this.runningJobId
    },
    progressBar () {
      if (!this.jobState.runningState) {
        return 0
      }
      const runningState = this.jobState.runningState
      return runningState.percentage / 100
    },
    calcedPerClMetricValue () {
      let metricVal
      let unit
      const ret = {
        unit,
        val: ''
      }
      switch (this.pump.type) {
        case 'dc':
          metricVal = this.pump.timePerClInMs
          ret.unit = 'ms/cl'
          break
        case 'stepper':
          metricVal = this.pump.stepsPerCl
          ret.unit = 'st/cl'
          break
      }
      if (!isFinite(this.trueLiquidPumpedField) || !isFinite(this.jobMetrics?.mlPumped) ||
        this.trueLiquidPumpedField <= 0 || this.jobMetrics.mlPumped <= 0) {
        return ret
      }
      if (metricVal) {
        ret.val = Math.max(1, Math.round((this.jobMetrics.mlPumped / this.trueLiquidPumpedField) * metricVal))
      }
      return ret
    },
    printDisableReason () {
      if (this.disableReason) {
        return this.disableReason
      }
      return this.$t('component.pump_tester.default_disable_reason')
    },
    runValField () {
      switch (this.advice.type) {
        case 'PUMP_ML':
          return {
            label: this.$t('component.pump_tester.metrics.liquid_run_val_field'),
            suffix: 'ml'
          }
        case 'PUMP_TIME':
          return {
            label: this.$t('component.pump_tester.metrics.time_run_val_field'),
            suffix: 'ms'
          }
        case 'PUMP_STEPS':
          return {
            label: this.$t('component.pump_tester.metrics.steps_run_val_field'),
            suffix: 'st'
          }
        default:
          return {
            label: this.$t('component.pump_tester.metrics.unknown_run_metric'),
            suffix: ''
          }
      }
    }
  }
}
</script>

<style scoped>
  tbody tr {
    background-color: transparent;
  }
</style>
