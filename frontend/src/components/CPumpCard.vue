<template>
  <q-card class="bg-white" style="display: flex; flex-direction: column">
    <q-card-section class="row items-center justify-around bg-cyan-1 q-pa-sm">
      <div class="col q-px-sm">
        <p class="text-h5 q-ma-none dotted-overflow" style="line-height: 1.5rem">
          {{ pump.name ? pump.name : 'Pump #' + String(pump.id) }}</p>
        <p
          class="q-ma-none"
          style="line-height: 1rem; font-family: 'Courier New',sans-serif"
        >
          <q-icon :name="typeNameData.icon"/>
          {{ typeNameData.label }}
        </p>
      </div>
      <div class="col-shrink">
        <div class="row q-gutter-sm">
          <q-badge :color="pumpedUpState.color" class="text-subtitle2">
            <div class="row q-col-gutter-sm">
              <p>
                {{ pumpedUpState.label }}
              </p>
            </div>
          </q-badge>
          <q-badge :color="pumpState.color" class="text-subtitle2">
            {{ pumpState.label }}
          </q-badge>
        </div>
      </div>
    </q-card-section>
    <q-linear-progress
      :query="progressBar.query"
      :value="progressBar.value"
      :reverse="progressBar.reverse"
      animation-speed="1000"
      color="cyan-4"
    />
    <q-card-section class="q-py-sm">
      <div class="row">
        <div class="col-6">
          <p class="text-weight-medium">Ingredient</p>
        </div>
        <div class="col-6">
          <p class="text-weight-medium">Filling level</p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p
          >
            {{ !!pump.currentIngredient ? pump.currentIngredient.name : 'None' }}
          </p>
        </div>
        <div class="col-6">
          <p
            :class="getDisplayAttribute(pump.fillingLevelInMl).class"
          >
            {{ getDisplayAttribute(pump.fillingLevelInMl, 'ml').label }}
          </p>
        </div>
      </div>
    </q-card-section>
    <hr
      v-if="showDetailed"
      class="text-grey-2 q-ma-none"
    >
    <q-card-section
      v-if="showDetailed && pump.type === 'stepper'"
      class="q-py-sm"
    >
      <div class="row">
        <div class="col-6">
          <p class="text-weight-medium">Steps per Cl</p>
        </div>
        <div class="col-6">
          <p class="text-weight-medium">Max steps per second</p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p
            :class="getDisplayAttribute(pump.stepsPerCl).class"
          >
            {{ getDisplayAttribute(pump.stepsPerCl, 'steps/cl').label }}
          </p>
        </div>
        <div class="col-6">
          <p
            :class="getDisplayAttribute(pump.maxStepsPerSecond).class"
          >
            {{ getDisplayAttribute(pump.maxStepsPerSecond, 'steps/s').label }}
          </p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p class="text-weight-medium">Acceleration</p>
        </div>
        <div class="col-6">
          <p class="text-weight-medium">Step pin</p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p
            :class="getDisplayAttribute(pump.acceleration).class"
          >
            {{ getDisplayAttribute(pump.acceleration, 'steps/s²').label }}
          </p>
        </div>
        <div class="col-6">
          <p
            :class="getDisplayAttribute(pump.stepPin).class"
          >
            {{ getDisplayAttribute(pump.stepPin).label }}
          </p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p class="text-weight-medium">Enable pin</p>
        </div>
        <div class="col-6">
          <p class="text-weight-medium">Tube capacity</p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p
            :class="getDisplayAttribute(pump.enablePin).class"
          >
            {{ getDisplayAttribute(pump.enablePin).label }}
          </p>
        </div>
        <div class="col-6">
          <p
            :class="getDisplayAttribute(pump.tubeCapacityInMl).class"
          >
            {{ getDisplayAttribute(pump.tubeCapacityInMl, 'ml').label }}
          </p>
        </div>
      </div>
    </q-card-section>
    <q-card-section
      v-if="showDetailed && pump.type === 'dc'"
      class="q-py-sm row items-center"
      style="flex-grow: 1"
    >
      <div class="col">
        <div class="row">
          <div class="col-6">
            <p class="text-weight-medium">Time per Cl</p>
          </div>
          <div class="col-6">
            <p class="text-weight-medium">Enable pin</p>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <p
              :class="getDisplayAttribute(pump.timePerClInMs).class"
            >
              {{ getDisplayAttribute(pump.timePerClInMs, 'ms/cl').label }}
            </p>
          </div>
          <div class="col-6">
            <p
              :class="getDisplayAttribute(pump.pin).class"
            >
              {{ getDisplayAttribute(pump.pin).label }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <p class="text-weight-medium">Running state</p>
          </div>
          <div class="col-6">
            <p class="text-weight-medium">Tube capacity</p>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <p
              :class="getDisplayAttribute(pump.isPowerStateHigh).class"
            >
              {{ getDisplayAttribute(pump.isPowerStateHigh).label }}
            </p>
          </div>
          <div class="col-6">
            <p
              :class="getDisplayAttribute(pump.tubeCapacityInMl).class"
            >
              {{ getDisplayAttribute(pump.tubeCapacityInMl, 'ml').label }}
            </p>
          </div>
        </div>
      </div>
    </q-card-section>
    <!--div style="flex-grow: 1"></div-->
    <hr class="text-grey-2 q-ma-none">
    <q-card-section class="q-pa-sm">
      <div class="row q-gutter-lg justify-end">
        <div class="col-auto">
          <q-btn
            no-caps
            round
            @click="$router.push({ name: 'editpump', params: { pumpId: pump.id }})"
            :icon="mdiPencilOutline"
            class="bg-info text-white"
          />
        </div>
        <div class="col">
          <div class="row q-gutter-sm justify-end">
            <q-btn
              no-caps
              round
              :icon="mdiReply"
              class="bg-green text-white"
              :loading="pumpDownBtnLoading"
              @click="onClickPumpUp(true)"
              :disable="runningState.running"
            >
            </q-btn>
            <q-btn
              no-caps
              round
              :icon="mdiShare"
              :loading="pumpUpBtnLoading"
              class="bg-green text-white"
              @click="onClickPumpUp(false)"
              :disable="runningState.running"
            >
            </q-btn>
            <q-btn
              no-caps
              round
              :color="runningState.running ? 'negative' : 'positive'"
              :icon="runningState.running? mdiStop : mdiPlay"
              :loading="runningBtnLoading"
              class="text-white"
              @click="onClickTurnOnOrOffPump()"
            >
            </q-btn>
          </div>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import {
  mdiProgressClock,
  mdiPump,
  mdiPencilOutline,
  mdiPlay,
  mdiStop,
  mdiReply,
  mdiShare,
  mdiSync
} from '@quasar/extras/mdi-v5'
import WebSocketService from '../services/websocket.service'
import PumpService from 'src/services/pump.service'

export default {
  name: 'CPumpCard',
  props: {
    pump: {
      type: Object,
      required: true
    },
    showDetailed: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      pumpDownBtnLoading: false,
      pumpUpBtnLoading: false,
      runningBtnLoading: false,
      runningState: {
        running: false,
        inPumpUp: false,
        forward: true,
        percentage: 0
      }
    }
  },
  watch: {
    pump: {
      immediate: false,
      handler (newValue, oldValue) {
        if (newValue.id !== oldValue.id) {
          WebSocketService.unsubscribe('/user/topic/runningstate/' + String(oldValue.id))
          this.runningState = Object.assign({}, {
            running: false,
            inPumpUp: false,
            forward: true,
            percentage: 0
          })
          WebSocketService.subscribe('/user/topic/pump/runningstate/' + String(newValue.id), (data) => {
            this.runningState = Object.assign(this.runningState, JSON.parse(data.body))
          })
        }
      }
    }
  },
  methods: {
    onClickTurnOnOrOffPump () {
      const vm = this
      this.runningBtnLoading = true
      if (this.runningState.running) {
        PumpService.stopPump(this.pump.id).then(() => {
          vm.$q.notify({
            type: 'positive',
            message: 'Pump #' + String(this.pump.id) + ' stopped!'
          })
        }).finally(this.runningBtnLoading = false)
      } else {
        PumpService.startPump(this.pump.id).then(() => {
          vm.$q.notify({
            type: 'positive',
            message: 'Pump #' + String(this.pump.id) + ' started!'
          })
        }).finally(this.runningBtnLoading = false)
      }
    },
    onClickPumpUp (reverse) {
      if (reverse) {
        this.pumpDownBtnLoading = true
        PumpService.pumpDown(this.pump.id).finally(this.pumpDownBtnLoading = false)
      } else {
        this.pumpUpBtnLoading = true
        PumpService.pumpUp(this.pump.id).finally(this.pumpUpBtnLoading = false)
      }
    },
    getDisplayAttribute (attr, suffix = '') {
      if (suffix) {
        suffix = ' ' + String(suffix)
      }
      if (!attr && attr !== 0) {
        return {
          class: 'text-red',
          label: '-- missing --'
        }
      } else {
        return {
          class: 'text-black',
          label: String(attr) + suffix
        }
      }
    }
  },
  created () {
    this.mdiPump = mdiPump
    this.mdiProgressClock = mdiProgressClock
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiPlay = mdiPlay
    this.mdiStop = mdiStop
    this.mdiReply = mdiReply
    this.mdiShare = mdiShare
    this.mdiSync = mdiSync
  },
  mounted () {
    WebSocketService.subscribe('/user/topic/pump/runningstate/' + String(this.pump.id), (data) => {
      this.runningState = Object.assign(this.runningState, JSON.parse(data.body))
    })
  },
  unmounted () {
    WebSocketService.unsubscribe('/user/topic/pump/runningstate/' + String(this.pump.id))
  },
  computed: {
    progressBar () {
      let value = this.runningState.forward ? this.runningState.percentage : (1 - this.runningState.percentage)
      if (!this.runningState.running && !this.runningState.inPumpUp) {
        value = 0
      }
      value = value / 100
      return {
        value: value,
        query: this.runningState.running && !this.runningState.inPumpUp,
        reverse: this.runningState.running && !this.runningState.inPumpUp
      }
    },
    typeNameData () {
      if (this.pump.type === 'stepper') {
        return {
          icon: this.mdiProgressClock,
          label: 'Stepper Pump'
        }
      } else {
        return {
          icon: this.mdiPump,
          label: 'DC Pump'
        }
      }
    },
    pumpedUpState () {
      const state = {
        color: '',
        label: ''
      }
      if (this.pump.pumpedUp) {
        state.color = 'positive'
        state.label = 'Pumped Up'
      } else {
        state.color = 'negative'
        state.label = 'Pumped Down'
      }
      if (this.runningState.inPumpUp) {
        state.color = 'warning'
        if (this.runningState.forward) {
          state.label = 'Pumping Up'
        } else {
          state.label = 'Pumping Down'
        }
      }
      return state
    },
    pumpState () {
      const state = {
        color: '',
        label: ''
      }
      switch (this.pump.state) {
        case 'READY':
          state.color = 'positive'
          state.label = 'Ready'
          break
        case 'INCOMPLETE':
          state.color = 'warning'
          state.label = 'Incomplete Configuration'
          break
        case 'DISABLED':
          state.color = 'negative'
          state.label = 'Disabled'
      }
      if (this.runningState.running) {
        state.color = 'positive'
        state.label = 'Running'
      }
      return state
    }
  }
}
</script>

<style scoped>

</style>
