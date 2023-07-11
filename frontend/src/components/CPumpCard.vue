<template>
  <q-card class="bg-white" style="display: flex; flex-direction: column">
    <q-card-section class="row items-center justify-around bg-cyan-1 q-pa-sm">
      <div class="col-7 q-px-sm">
        <p class="text-h5 q-ma-none dotted-overflow" style="line-height: 1.5rem">{{ pump.name? pump.name : 'Pump #' + String(pump.id) }}</p>
        <p
          class="q-ma-none"
          style="line-height: 1rem; font-family: 'Courier New',sans-serif"
        >
          <q-icon :name="typeNameData.icon"/>
          {{ typeNameData.label }}
        </p>
      </div>
      <div class="col-5">
        <div class="row justify-end items-center q-gutter-sm">
          <div class="col-shrink">
            <q-badge :color="pumpedUpState.color" class="text-subtitle2">{{ pumpedUpState.label }}</q-badge>
          </div>
          <div class="col-shrink">
            <q-badge :color="pumpState.color" class="text-subtitle2">{{ pumpState.label }}</q-badge>
          </div>
        </div>
      </div>
    </q-card-section>
    <q-linear-progress
      :query="progressBar.query"
      :value="progressBar.value"
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
            {{!!pump.currentIngredient? pump.currentIngredient.name : 'None' }}
          </p>
        </div>
        <div class="col-6">
          <p
            :class="getDisplayAttribute(pump.fillingLevelInMl).class"
          >
            {{getDisplayAttribute(pump.fillingLevelInMl, 'ml').label}}
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
            {{getDisplayAttribute(pump.stepsPerCl, 'steps/cl').label}}
          </p>
        </div>
        <div class="col-6">
          <p
            :class="getDisplayAttribute(pump.maxStepsPerSecond).class"
          >
            {{getDisplayAttribute(pump.maxStepsPerSecond, 'steps/s').label}}
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
            {{getDisplayAttribute(pump.acceleration, 'steps/s²').label}}
          </p>
        </div>
        <div class="col-6">
          <p
            :class="getDisplayAttribute(pump.stepPin).class"
          >
            {{getDisplayAttribute(pump.stepPin).label}}
          </p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p class="text-weight-medium">Enable pin</p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p
            :class="getDisplayAttribute(pump.enablePin).class"
          >
            {{getDisplayAttribute(pump.enablePin).label}}
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
              {{getDisplayAttribute(pump.timePerClInMs, 'ms/cl').label}}
            </p>
          </div>
          <div class="col-6">
            <p
              :class="getDisplayAttribute(pump.pin).class"
            >
              {{getDisplayAttribute(pump.pin).label}}
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
              :class="getDisplayAttribute(pump.powerStateHigh).class"
            >
              {{getDisplayAttribute(pump.powerStateHigh).label}}
            </p>
          </div>
          <div class="col-6">
            <p
              :class="getDisplayAttribute(pump.tubeCapacityInMl).class"
            >
              {{getDisplayAttribute(pump.tubeCapacityInMl, 'ml').label}}
            </p>
          </div>
        </div>
      </div>
    </q-card-section>
    <!--div style="flex-grow: 1"></div-->
    <hr class="text-grey-2 q-ma-none">
    <q-card-section class="q-pa-sm">
      <div class="row q-gutter-sm justify-end">
        <q-btn no-caps class="bg-grey-6 text-white col-shrink">Edit</q-btn>
        <q-btn no-caps class="bg-green text-white col-shrink">Pump Back</q-btn>
        <q-btn no-caps class="bg-green text-white col-shrink">Pump Up</q-btn>
        <q-btn no-caps class="bg-green text-white col-shrink">Run</q-btn>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import { mdiProgressClock, mdiPump } from '@quasar/extras/mdi-v5'
import WebSocketService from '../services/websocket.service'

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
        if (newValue.id === oldValue.id) {
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
        query: this.runningState.running && !this.runningState.inPumpUp
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
