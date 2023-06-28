<template xmlns="http://www.w3.org/1999/html">
  <q-page padding class="page-content">
    <h5>Pump Setup Assistant</h5>
    <q-stepper
      v-model:model-value="stepper"
      vertical
      animated
      flat
      bordered
      header-nav
    >
      <q-step
        title="Pump type"
        :name="0"
        :icon="mdiAbTesting"
        :done="pumpTypeComplete"
        header-nav
      >
        <div class="col-12  q-col-gutter-md">
          <div class="row">
            <p class="col text-center text-bold text-h5">What type of pump do you want to add?</p>
          </div>
          <div class="row justify-center q-col-gutter-lg">
            <div class="col-4">
              <q-card class="clickable"
                      :class="{'bg-grey-4': pump.dtype === 'dc'}"
                      @click="selectPump('dc')"
              >
                <q-card-section class="text-center">
                  <q-icon size="lg" :name="mdiPump"/>
                  <p class="text-bold">DC-Pump</p>
                </q-card-section>
              </q-card>
            </div>
            <div class="col-4">
              <q-card class="clickable"
                      :class="{'bg-grey-4': pump.dtype === 'stepper'}"
                      @click="selectPump('stepper')"
              >
                <q-card-section class="text-center">
                  <q-icon size="lg" :name="mdiProgressClock"/>
                  <p class="text-bold">Stepper-Pump</p>
                </q-card-section>
              </q-card>
            </div>
          </div>
        </div>
      </q-step>
      <q-step
        title="Handle"
        :name="1"
        :icon="mdiPencilOutline"
        :header-nav="pumpTypeComplete"
        :done="handleComplete"
      >
        <div class="col-12 q-ma-lg">
          <div class="row justify-center q-col-gutter-lg">
            <div class="col-12 text-center text-bold text-h5">
              <p>How should we call your pump?</p>
            </div>
            <div class="col-6">
              <q-input
                v-model:model-value="pump.name"
                outlined
                filled
                label="Pump identifier"
              />
            </div>
          </div>
          <div class="row justify-start">
            <q-stepper-navigation>
              <q-btn @click="stepper++" color="primary" label="Continue"/>
              <q-btn flat @click="stepper--" color="primary" label="Back" class="q-ml-sm"/>
            </q-stepper-navigation>
          </div>
        </div>
      </q-step>
      <q-step
        title="Hardware pins"
        :name="2"
        :icon="mdiFlashOutline"
      >
        <div class="col-12 q-ma-lg">
          <div class="row justify-center q-col-gutter-lg">
            <div class="col-12 text-center text-bold text-h5">
              <p>Select the pins that control the pump</p>
            </div>
            <div class="col-12">
              <c-assistant-container>
                <template v-slot:explanations>
                  <p>
                    A stepper motor driver usually has three important pins, that are used to control the motor.
                  </p>
                  <ul>
                    <li>
                      The step-pin, which gets one pulse for each step, that the motor should do.
                    </li>
                    <li>
                      The enable pin. This pin decides on if the motor should be energized and therefore hold
                      his current position, or not.
                    </li>
                    <li>
                      The direction pin. It decided on the direction that the motor takes. The direction that
                      the motors are running to is decided by one single pim, that controls all motor.
                      Please build your machine in a way that connects that pin with with the direction logic of all your
                      motors.
                    </li>
                    <li>
                      Your motor driver very likely also provides more pins (step resolution/sleep/...). Please configure these statically in hardware!
                    </li>
                  </ul>
                  <p><b>Important:</b> Pin-numbers don't correspond to GPIO numbers, but BCM numbers. BCM refers to the
                    “Broadcom SOC channel” number, which is the numbering inside the chip which is used on the Raspberry Pi.
                    These numbers changed between board versions. These link may help:
                    <a href="https://pi4j.com/getting-started/understanding-the-pins/#overview" target="_blank">Pi4J - Understanding the pins</a>
                  </p>
                </template>
                <template v-slot:fields>
                  <q-input
                    v-model:model-value="pump.name"
                    outlined
                    type="number"
                    filled
                    label="Step BCM-Pin"
                  />
                  <q-input
                    v-model:model-value="pump.name"
                    outlined
                    type="number"
                    filled
                    label="Enable BCM-Pin"
                  />
                </template>
              </c-assistant-container>
            </div>
          </div>
          <div class="row justify-start">
            <q-stepper-navigation>
              <q-btn @click="stepper++" color="primary" label="Continue"/>
              <q-btn flat @click="stepper--" color="primary" label="Back" class="q-ml-sm"/>
            </q-stepper-navigation>
          </div>
        </div>
      </q-step>
      <q-step
        title="Calibrate"
        :name="3"
        :icon="mdiCogs"
      >
        <div class="col-12 q-ma-lg">
          <div class="row justify-center q-col-gutter-lg">
            <div class="col-12 text-center text-bold text-h5">
              <p>Calibrate your pump</p>
            </div>
            <div class="col-12 q-gutter-md">
              <c-assistant-container>
                <template v-slot:explanations>
                  <p>
                    The acceleration field determines how fast your motor should speed up or break down.
                    If the acceleration is too high, the motor might skip steps on speed up or do too many on break down.
                  </p>
                </template>
                <template v-slot:fields>
                  <q-input
                    v-model:model-value="pump.name"
                    outlined
                    type="number"
                    filled
                    label="Acceleration"
                  />
                </template>
              </c-assistant-container>
              <q-splitter horizontal class="q-pb-md" />
              <c-assistant-container>
                <template v-slot:explanations>
                  <p>
                    The "minimal step delta"-field determines how long the controller should wait between steps.
                    If it is too low, the motor will skip steps or not run at all.
                    If it is too high, the motor will run slower than necessary.
                  </p>
                  <p>
                    The rule is:
                  </p>
                  <ul>
                    <li>lower = faster motor</li>
                    <li>higher = slower motor</li>
                  </ul>
                </template>
                <template v-slot:fields>
                  <q-input
                    v-model:model-value="pump.name"
                    outlined
                    type="number"
                    filled
                    label="Minimal step delta"
                  />
                </template>
              </c-assistant-container>
              <q-splitter horizontal class="q-pb-md"/>
              <c-assistant-container>
                <template v-slot:explanations>
                  This field determines how many steps the motor must make to produce one cl.
                </template>
                <template v-slot:fields>
                  <q-input
                    v-model:model-value="pump.name"
                    outlined
                    type="number"
                    filled
                    label="Steps per cl"
                  />
                </template>
              </c-assistant-container>
            </div>
          </div>
        </div>
        <div class="col-12 q-ma-lg">
          <div class="row justify-start">
            <q-stepper-navigation>
              <q-btn @click="stepper++" color="primary" label="Continue"/>
              <q-btn flat @click="stepper--" color="primary" label="Back" class="q-ml-sm"/>
            </q-stepper-navigation>
          </div>
        </div>
      </q-step>
    </q-stepper>
  </q-page>
</template>

<script>
import {
  mdiAbTesting,
  mdiCogs,
  mdiFlashOutline,
  mdiPencilOutline,
  mdiProgressClock,
  mdiPump
} from '@quasar/extras/mdi-v5'
import CAssistantContainer from 'components/CAssistantContainer'

export default {
  name: 'SetupPump',
  components: { CAssistantContainer },
  data () {
    return {
      stepper: 0,
      pump: {
        dtype: '',
        name: ''
      }
    }
  },
  created () {
    this.mdiPump = mdiPump
    this.mdiProgressClock = mdiProgressClock
    this.mdiCogs = mdiCogs
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiFlashOutline = mdiFlashOutline
    this.mdiAbTesting = mdiAbTesting
  },
  methods: {
    selectPump (name) {
      if (name === 'stepper') {
        this.pump.dtype = 'stepper'
      } else if (name === 'dc') {
        this.pump.dtype = 'dc'
      } else {
        return
      }
      this.stepper++
    }
  },
  computed: {
    pumpTypeComplete () {
      return !!this.pump.dtype
    },
    handleComplete () {
      return !!this.pump.name
    }
  }
}
</script>

<style scoped>

</style>
