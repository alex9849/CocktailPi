<template>
  <q-card class="bg-white">
    <q-card-section class="row items-center justify-around bg-cyan-1 q-pa-sm">
      <div class="col-7 q-px-sm">
        <p class="text-h5 q-ma-none dotted-overflow" style="line-height: 1.5rem">{{ pump.name }}</p>
        <p
          class="q-ma-none"
          style="line-height: 1rem; font-family: 'Courier New',sans-serif"
        >
          <q-icon :name="mdiPump"/>
          Stepper Pump
        </p>
      </div>
      <div class="col-5">
        <div class="row justify-end items-center q-gutter-sm">
          <div class="col-shrink">
            <q-badge color="negative" class="text-subtitle2">Pumped Down</q-badge>
          </div>
          <div class="col-shrink">
            <q-badge color="green" class="text-subtitle2">Enabled</q-badge>
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
      v-if="showDetailed"
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
    <q-card-actions align="right">
      <q-btn no-caps class="bg-grey-6 text-white">Edit</q-btn>
      <q-btn no-caps class="bg-green text-white">Pump Back</q-btn>
      <q-btn no-caps class="bg-green text-white">Pump Up</q-btn>
      <q-btn no-caps class="bg-green text-white">Run</q-btn>
    </q-card-actions>
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
  }
}
</script>

<style scoped>

</style>
