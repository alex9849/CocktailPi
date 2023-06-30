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
          <div class="row justify-center q-col-gutter-lg">
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
        <div class="col-12">
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
        </div>
        <div class="col-12 q-ma-lg">
          <div class="row justify-start">
            <q-stepper-navigation>
              <q-btn @click="stepper++" :disable="!handleComplete" color="primary" label="Continue"/>
              <q-btn flat @click="stepper--" color="primary" label="Back" class="q-ml-sm"/>
            </q-stepper-navigation>
          </div>
        </div>
      </q-step>
      <q-step
        title="Hardware pins"
        :name="2"
        :icon="mdiFlashOutline"
        :header-nav="handleComplete"
        :done="hardwarePinsComplete"
      >
        <div class="col-12">
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
                      Please build your machine in a way that connects that pin with with the direction logic of all
                      your
                      motors.
                    </li>
                    <li>
                      Your motor driver very likely also provides more pins (step resolution/sleep/...). Please
                      configure these statically in hardware!
                    </li>
                  </ul>
                  <p><b>Important:</b> Pin-numbers don't correspond to GPIO numbers, but BCM numbers. BCM refers to the
                    “Broadcom SOC channel” number, which is the numbering inside the chip which is used on the Raspberry
                    Pi.
                    These numbers changed between board versions. These link may help:
                    <a href="https://pi4j.com/getting-started/understanding-the-pins/#overview" target="_blank">Pi4J -
                      Understanding the pins</a>
                  </p>
                </template>
                <template v-slot:fields>
                  <q-input
                    v-model:model-value="pump.stepPin"
                    outlined
                    type="number"
                    filled
                    label="Step BCM-Pin"
                  />
                  <q-input
                    v-model:model-value="pump.enablePin"
                    outlined
                    type="number"
                    filled
                    label="Enable BCM-Pin"
                  />
                </template>
              </c-assistant-container>
            </div>
          </div>
        </div>
        <div class="col-12 q-ma-lg">
          <div class="row justify-start">
            <q-stepper-navigation>
              <q-btn @click="stepper++" :disable="!hardwarePinsComplete" color="primary" label="Continue"/>
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
        <div class="col-12">
          <div class="row justify-center q-col-gutter-lg">
            <div class="col-12 text-center text-bold text-h5">
              <p>Calibrate your pump</p>
            </div>
            <div class="col-12 q-gutter-md">
              <c-assistant-container>
                <template v-slot:explanations>
                  <p>
                    The acceleration field determines how fast your motor should speed up or break down.
                    If the acceleration is too high, the motor might skip steps on speed up or do too many on break
                    down.
                    The acceleration is given in steps per second per second.
                  </p>
                </template>
                <template v-slot:fields>
                  <q-input
                    v-model:model-value="pump.acceleration"
                    outlined
                    type="number"
                    filled
                    label="Acceleration"
                  >
                    <template v-slot:append>
                      st/s²
                    </template>
                  </q-input>
                </template>
              </c-assistant-container>
              <q-splitter
                :model-value="10"
                horizontal
                class="q-pb-md"
              />
              <c-assistant-container>
                <template v-slot:explanations>
                  <p>
                    The "minimal step delta"-field determines how long the controller should wait between steps.
                    If it is too low, the motor will skip steps or not run at all.
                    If it is too high, the motor will run slower than necessary.<br>
                    The delay is given in milliseconds. (1 second = 1000 millisecond)
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
                    v-model:model-value="pump.min_step_delta"
                    outlined
                    type="number"
                    filled
                    label="Minimal step delta (in ms)"
                  >
                    <template v-slot:append>
                      ms
                    </template>
                  </q-input>
                </template>
              </c-assistant-container>
              <q-splitter
                :model-value="10"
                horizontal
                class="q-pb-md"
              />
              <c-assistant-container>
                <template v-slot:explanations>
                  This field determines how many steps the motor must make to produce one cl.
                </template>
                <template v-slot:fields>
                  <q-input
                    v-model:model-value="pump.steps_per_cl"
                    outlined
                    type="number"
                    filled
                    label="Steps per cl"
                  >
                    <template v-slot:append>
                      st/cl
                    </template>
                  </q-input>
                </template>
              </c-assistant-container>
              <q-splitter
                :model-value="10"
                horizontal
                class="q-pb-md"
              />
              <c-assistant-container>
                <template v-slot:explanations>
                  Here you can test your motor and calculate the number of steps that the motor needs to pump one cl.
                  You can run the tester with two metrics:
                  <ul>
                    <li><b>Steps:</b> Tell the motor how many steps it should take.</li>
                    <li><b>Liquid:</b> Tell the motor how much liquid it should pump. For this, the "Steps per cl" field
                      must contain information!
                    </li>
                  </ul>
                  The system will always track how many steps the motor has made. Depending on how you have configured
                  the motor above, it might happen that the motor skips steps. The tester exists to verify your
                  configuration.<br>
                  You can also let the tester calculate the number of steps that the motor took to pump one cl.
                  For this, you have to measure the amount of liquid (in ml) that the pump pumped during your test.
                  You can use a scale for that. Also, make sure that your hoses are filled with liquid since the tester
                  doesn't take that into account!
                </template>
                <template v-slot:fields>
                  <p class="text-subtitle1 text-center">Motor tester</p>
                  <c-pump-tester/>
                </template>
              </c-assistant-container>
              <q-splitter
                :model-value="10"
                horizontal
                class="q-pb-md"
              />
              <c-assistant-container>
                <template v-slot:explanations>
                  <p>
                    The tube capacity determines how much liquid is needed to fill the hose that connects
                    the liquid container with the dispensing part of your cocktail machine. This metric is
                    used to accurately fill your hoses with liquid before the actual production of a new drink.
                    It is also used to empty your hoses (pump the liquid back into the container) if the machine hasn't
                    been used for a while. </p>
                </template>
                <template v-slot:fields>
                  <q-input
                    v-model:model-value="pump.tube_capacity"
                    outlined
                    type="number"
                    filled
                    label="Tube capacity (in ml)"
                  >
                    <template v-slot:append>
                      ml
                    </template>
                  </q-input>
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
      <q-step
        title="State"
        :name="4"
        :icon="mdiPencilOutline"
        header-nav
        :done="handleComplete"
      >
        <div class="col-12">
          <div class="row justify-center q-col-gutter-lg">
            <div class="col-12 text-center text-bold text-h5">
              <p>Pump state</p>
            </div>
            <div class="col-12 q-gutter-md">
              <c-assistant-container>
                <template v-slot:fields>
                  <c-ingredient-selector
                    label="Current ingredient"
                    v-model:selected="pump.currentIngredient"
                    clearable
                    filled
                    filter-manual-ingredients
                    filter-ingredient-groups
                  />
                </template>
                <template v-slot:explanations>
                  Optional: The ingredient that is currently connected to the pump.
                </template>
              </c-assistant-container>
              <q-splitter
                :model-value="10"
                horizontal
                class="q-pb-md"
              />
              <c-assistant-container>
                <template v-slot:fields>
                  <q-input
                    label="Current filling level"
                    type="number"
                    outlined
                    filled
                    hide-bottom-space
                    v-model:model-value="pump.fillingLevelInMl"
                  >
                    <template v-slot:append>
                      ml
                    </template>
                  </q-input>
                </template>
                <template v-slot:explanations>
                  The current filling level of the container that is connected to the pump.
                  This field is used to check if you have enough liquid left to produce a
                  cocktail of a selected size.
                </template>
              </c-assistant-container>
              <q-splitter
                :model-value="10"
                horizontal
                class="q-pb-md"
              />
              <c-assistant-container>
                <template v-slot:fields>
                  <div class="row justify-center">
                    <div class="col-auto">
                      <q-checkbox
                        v-model:model-value="pump.pumpedUp"
                        outlined
                        size="xl"
                        class="text-bold"
                        hide-bottom-space
                        label="Pumped Up"
                      />
                    </div>
                  </div>
                </template>
                <template v-slot:explanations>
                  The "Pumped up"-field holds the information of the filling state of the hoses of a pump.
                  If the hoses are not filled with liquid, the machine will fill them before producing a cocktail.
                  This field is also used to find out from whose pumps the liquid should be pumped back into the
                  container, in case the machine dosen't get used for a specific amount of time.
                </template>
              </c-assistant-container>
            </div>
            <div class="col-12 q-ma-lg">
              <div class="row justify-start">
                <q-stepper-navigation>
                  <q-btn @click="stepper++" color="primary" label="Finish"/>
                  <q-btn flat @click="stepper--" color="primary" label="Back" class="q-ml-sm"/>
                </q-stepper-navigation>
              </div>
            </div>
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
  mdiEqual,
  mdiFlashOutline,
  mdiPencilOutline,
  mdiPlay,
  mdiProgressClock,
  mdiPump,
  mdiStop
} from '@quasar/extras/mdi-v5'
import CAssistantContainer from 'components/CAssistantContainer'
import CPumpTester from 'components/CPumpTester'
import CIngredientSelector from 'components/CIngredientSelector'

export default {
  name: 'SetupPump',
  components: { CPumpTester, CAssistantContainer, CIngredientSelector },
  data () {
    return {
      stepper: 0,
      pump: {
        dtype: '',
        name: '',
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
  },
  created () {
    this.mdiPump = mdiPump
    this.mdiProgressClock = mdiProgressClock
    this.mdiCogs = mdiCogs
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiFlashOutline = mdiFlashOutline
    this.mdiAbTesting = mdiAbTesting
    this.mdiPlay = mdiPlay
    this.mdiStop = mdiStop
    this.mdiEqual = mdiEqual
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
    },
    hardwarePinsComplete () {
      return (!!this.pump.stepPin && !!this.pump.enablePin)
    }
  }
}
</script>

<style scoped>

</style>
