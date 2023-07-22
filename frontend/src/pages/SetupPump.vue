<template xmlns="http://www.w3.org/1999/html">
  <q-page padding class="page-content">
    <h5>Pump Setup Assistant</h5>
    <q-stepper
      v-model:model-value="stepper"
      active-color="cyan"
      animated
      flat
      bordered
      header-nav
    >
      <q-step
        :caption="handleComplete ? 'Complete' : 'Optional'"
        title="Handle"
        :name="0"
        :icon="mdiPencilOutline"
        header-nav
        done
        :done-icon="mdiPencilOutline"
      >
        <div class="col-12">
          <div class="row justify-center q-col-gutter-lg">
            <div class="col-12 text-center text-bold text-h5">
              <p>How should your pump be called?</p>
            </div>
            <div class="col-6">
              <q-input
                :model-value="pump.name"
                @update:model-value="setPumpAttr('name', pump.name, $event)"
                :error-message="attrState.name.errorMsg"
                :error="!!attrState.name.errorMsg"
                :loading="attrState.name.loading"
                debounce="600"
                :placeholder="'Pump #' + String(pump.id)"
                outlined
                filled
                label="Pump identifier"
              />
            </div>
          </div>
        </div>
        <div class="col-12 q-ma-lg">
          <q-stepper-navigation>
            <q-btn @click="stepper++" color="primary" label="Continue"/>
          </q-stepper-navigation>
        </div>
      </q-step>
      <q-step
        :caption="hardwarePinsComplete ? 'Complete' : null"
        title="Hardware pins"
        :name="1"
        :icon="mdiFlashOutline"
        header-nav
        done
        :done-icon="mdiFlashOutline"
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
                    :model-value="pump.stepPin"
                    @update:model-value="setPumpAttr('stepPin', pump.stepPin, $event)"
                    :error-message="attrState.stepPin.errorMsg"
                    :error="!!attrState.stepPin.errorMsg"
                    :loading="attrState.stepPin.loading"
                    debounce="600"
                    outlined
                    type="number"
                    filled
                    label="Step BCM-Pin"
                  />
                  <q-input
                    :model-value="pump.enablePin"
                    @update:model-value="setPumpAttr('enablePin', pump.enablePin, $event)"
                    :error-message="attrState.enablePin.errorMsg"
                    :error="!!attrState.enablePin.errorMsg"
                    :loading="attrState.enablePin.loading"
                    debounce="600"
                    outlined
                    type="number"
                    filled
                    label="Enable BCM-Pin"
                  >
                  </q-input>
                </template>
              </c-assistant-container>
            </div>
          </div>
        </div>
        <div class="col-12 q-ma-lg">
          <q-stepper-navigation>
            <q-btn @click="stepper++" :disable="!hardwarePinsComplete" color="primary" label="Continue"/>
            <q-btn flat @click="stepper--" color="primary" label="Back" class="q-ml-sm"/>
          </q-stepper-navigation>
        </div>
      </q-step>
      <q-step
        :caption="calibrationComplete ? 'Complete' : null"
        title="Calibrate"
        :name="2"
        :icon="mdiCogs"
        header-nav
        :disable="!hardwarePinsComplete"
        :done="hardwarePinsComplete"
        :done-icon="mdiCogs"
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
                    :model-value="pump.acceleration"
                    @update:model-value="setPumpAttr('acceleration', pump.acceleration, $event)"
                    :error-message="attrState.acceleration.errorMsg"
                    :error="!!attrState.acceleration.errorMsg"
                    :loading="attrState.acceleration.loading"
                    debounce="600"
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
                    The "max steps per second"-field determines fast the motor should spin at max.
                    One revolution is normally divided into 200 steps. This can vary depending on the motor and motor driver settings.
                    If it is too high, the motor might not be able to keep up and skips steps or even doesn't run at all.
                    If it is too low, the motor will run slower than necessary.<br>
                  </p>
                  <p>
                    The rule is:
                  </p>
                  <ul>
                    <li>higher = faster motor</li>
                    <li>lower = slower motor</li>
                  </ul>
                </template>
                <template v-slot:fields>
                  <q-input
                    :model-value="pump.maxStepsPerSecond"
                    @update:model-value="setPumpAttr('maxStepsPerSecond', pump.maxStepsPerSecond, $event)"
                    :error-message="attrState.maxStepsPerSecond.errorMsg"
                    :error="!!attrState.maxStepsPerSecond.errorMsg"
                    :loading="attrState.maxStepsPerSecond.loading"
                    debounce="600"
                    outlined
                    type="number"
                    filled
                    label="Max steps per second"
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
                    :model-value="pump.stepsPerCl"
                    @update:model-value="setPumpAttr('stepsPerCl', pump.stepsPerCl, $event)"
                    :error-message="attrState.stepsPerCl.errorMsg"
                    :error="!!attrState.stepsPerCl.errorMsg"
                    :loading="attrState.stepsPerCl.loading"
                    debounce="600"
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
                    <li><b>Liquid:</b> Tell the motor how much liquid he should pump.</li>
                    <li><b>Steps:</b> Tell the motor how many steps he should take.</li>
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
                  <c-pump-tester
                    :pump="pump"
                    :disable="pump.state === 'INCOMPLETE'"
                    disable-reason="Required parameter missing"
                    @update:perClMetric="setPumpAttr('stepsPerCl', pump.stepsPerCl, $event)"
                  />
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
                    :model-value="pump.tubeCapacityInMl"
                    @update:model-value="setPumpAttr('tubeCapacityInMl', pump.tubeCapacityInMl, $event)"
                    :error-message="attrState.tubeCapacityInMl.errorMsg"
                    :error="!!attrState.tubeCapacityInMl.errorMsg"
                    :loading="attrState.tubeCapacityInMl.loading"
                    debounce="600"
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
          <q-stepper-navigation>
            <q-btn @click="stepper++" :disable="!calibrationComplete" color="primary" label="Continue"/>
            <q-btn flat @click="stepper--" color="primary" label="Back" class="q-ml-sm"/>
          </q-stepper-navigation>
        </div>
      </q-step>
      <q-step
        caption="Optional"
        title="State"
        :name="3"
        :icon="mdiPencilOutline"
        header-nav
        :disable="!calibrationComplete"
        :done="calibrationComplete"
        :done-icon="mdiPencilOutline"
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
                    :model-value="pump.currentIngredient"
                    @update:model-value="setPumpAttr('currentIngredient', pump.currentIngredient, $event)"
                    :error-message="attrState.currentIngredient.errorMsg"
                    :error="!!attrState.currentIngredient.errorMsg"
                    :loading="attrState.currentIngredient.loading"
                    label="Current ingredient"
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
                    :model-value="pump.fillingLevelInMl"
                    @update:model-value="setPumpAttr('fillingLevelInMl', pump.fillingLevelInMl, $event)"
                    :error-message="attrState.fillingLevelInMl.errorMsg"
                    :error="!!attrState.fillingLevelInMl.errorMsg"
                    :loading="attrState.fillingLevelInMl.loading"
                    debounce="600"
                    label="Current filling level"
                    type="number"
                    outlined
                    filled
                    hide-bottom-space
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
                        :model-value="pump.pumpedUp"
                        @update:model-value="setPumpAttr('pumpedUp', pump.pumpedUp, $event)"
                        :error-message="attrState.pumpedUp.errorMsg"
                        :error="!!attrState.pumpedUp.errorMsg"
                        :loading="attrState.pumpedUp.loading"
                        debounce="600"
                        outlined
                        size="xl"
                        class="text-subtitle1"
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
          </div>
        </div>
        <div class="col-12 q-ma-lg">
          <q-stepper-navigation>
            <q-btn @click="stepper++" color="primary" label="Finish"/>
            <q-btn flat @click="stepper--" color="primary" label="Back" class="q-ml-sm"/>
          </q-stepper-navigation>
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
  mdiStop,
  mdiCheckCircle
} from '@quasar/extras/mdi-v5'
import CAssistantContainer from 'components/CAssistantContainer'
import CPumpTester from 'components/CPumpTester'
import CIngredientSelector from 'components/CIngredientSelector'
import PumpService, { pumpDtoMapper } from 'src/services/pump.service'
import UserService from 'src/services/user.service'

export default {
  name: 'SetupPump',
  components: { CPumpTester, CAssistantContainer, CIngredientSelector },
  data () {
    return {
      stepper: 0,
      pump: {
        id: '',
        type: '',
        name: '',
        stepPin: '',
        currentIngredient: '',
        enablePin: '',
        fillingLevelInMl: '',
        tubeCapacityInMl: '',
        stepsPerCl: '',
        maxStepsPerSecond: '',
        acceleration: '',
        pumpedUp: false
      },
      attrState: {
        name: {
          loading: false,
          errorMsg: '',
          saved: false
        },
        currentIngredient: {
          loading: false,
          errorMsg: '',
          saved: false
        },
        stepPin: {
          loading: false,
          errorMsg: '',
          saved: false
        },
        enablePin: {
          loading: false,
          errorMsg: '',
          saved: false
        },
        fillingLevelInMl: {
          loading: false,
          errorMsg: '',
          saved: false
        },
        tubeCapacityInMl: {
          loading: false,
          errorMsg: '',
          saved: false
        },
        stepsPerCl: {
          loading: false,
          errorMsg: '',
          saved: false
        },
        maxStepsPerSecond: {
          loading: false,
          errorMsg: '',
          saved: false
        },
        acceleration: {
          loading: false,
          errorMsg: '',
          saved: false
        },
        pumpedUp: {
          loading: false,
          errorMsg: '',
          saved: false
        }
      }
    }
  },
  async created () {
    this.mdiPump = mdiPump
    this.mdiProgressClock = mdiProgressClock
    this.mdiCogs = mdiCogs
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiFlashOutline = mdiFlashOutline
    this.mdiAbTesting = mdiAbTesting
    this.mdiCheckCircle = mdiCheckCircle
    this.mdiPlay = mdiPlay
    this.mdiStop = mdiStop
    this.mdiEqual = mdiEqual
    this.pump = await PumpService.getPump(this.$route.params.pumpId)
    if (this.handleComplete) {
      this.stepper = 1
    }
    if (this.hardwarePinsComplete) {
      this.stepper = 2
    }
    if (this.hardwarePinsComplete && this.calibrationComplete) {
      this.stepper = 3
    }
  },
  methods: {
    setPumpAttr (attr, currValue, newValue) {
      this.pump[attr] = newValue
      this.attrState[attr].loading = true
      this.attrState[attr].saved = false
      PumpService.updatePump(this.pump.id, pumpDtoMapper.toPumpCreateDto(this.pump), false)
        .then(pump => {
          this.pump = Object.assign(this.pump, pump)
          this.attrState[attr].errorMsg = ''
          this.attrState[attr].saved = true
        }, (err) => {
          this.pump[attr] = currValue
          this.attrState[attr].errorMsg = err?.response?.data?.message
          this.attrState[attr].saved = false
        })
        .finally(() => {
          this.attrState[attr].loading = false
        })
    }
  },
  computed: {
    anyAttrLoading () {
      for (const key of Object.keys(this.attrState)) {
        if (this.attrLoading[key].loading === true) {
          return true
        }
      }
      return false
    },
    handleComplete () {
      return !!this.pump.name
    },
    hardwarePinsComplete () {
      return (!!this.pump.stepPin && !!this.pump.enablePin)
    },
    calibrationComplete () {
      return !!this.pump.acceleration && !!this.pump.maxStepsPerSecond &&
        !!this.pump.stepsPerCl && !!this.pump.tubeCapacityInMl
    },
    pumpTypeStepLabel () {
      if (this.pumpTypeComplete) {
        if (this.pump.dtype === 'dc') {
          return 'Pump type: DC'
        } else {
          return 'Pump type: Stepper'
        }
      }
      return 'Pump type'
    }
  }
}
</script>

<style scoped>

</style>
