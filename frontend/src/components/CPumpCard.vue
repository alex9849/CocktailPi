<template>
  <q-card class="bg-white" style="display: flex; flex-direction: column">
    <q-card-section class="row items-center justify-around bg-cyan-1 q-pa-sm">
      <div class="col-7 q-px-sm">
        <p class="text-h5 q-ma-none dotted-overflow" style="line-height: 1.5rem">{{ pump.name }}</p>
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
    <q-linear-progress reverse color="cyan-4" />
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
          <p>None</p>
        </div>
        <div class="col-6">
          <p>None</p>
        </div>
      </div>
      <div v-if="showDetailed" class="row">
        <div class="col-6">
          <p class="text-weight-medium">Tube capacity</p>
        </div>
      </div>
      <div v-if="showDetailed" class="row">
        <div class="col-6">
          <p>None</p>
        </div>
      </div>
    </q-card-section>
    <hr
      v-if="showDetailed"
      class="text-grey-2 q-ma-none"
    >
    <q-card-section
      v-if="showDetailed && pump.dtype === 'StepperPump'"
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
          <p>None</p>
        </div>
        <div class="col-6">
          <p>None</p>
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
          <p>None</p>
        </div>
        <div class="col-6">
          <p>None</p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p class="text-weight-medium">Enable pin</p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p>None</p>
        </div>
      </div>
    </q-card-section>
    <q-card-section
      v-if="showDetailed && pump.dtype === 'DcPump'"
      class="q-py-sm"
    >
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
          <p>None</p>
        </div>
        <div class="col-6">
          <p>None</p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p class="text-weight-medium">Running state</p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p>None</p>
        </div>
      </div>
    </q-card-section>
    <div style="flex-grow: 1"></div>
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
    return {}
  },
  created () {
    this.mdiPump = mdiPump
    this.mdiProgressClock = mdiProgressClock
  },
  computed: {
    typeNameData () {
      if (this.pump.dtype === 'StepperPump') {
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
        case 'running':
          state.color = 'positive'
          state.label = 'Running'
          break
        case 'ready':
          state.color = 'positive'
          state.label = 'Ready'
          break
        case 'incomplete':
          state.color = 'negative'
          state.label = 'Incomplete Configuration'
          break
        case 'disabled':
        default:
          state.color = 'negative'
          state.label = 'Disabled'
      }
      return state
    }
  }
}
</script>

<style scoped>

</style>
