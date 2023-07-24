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
        <c-setup-step
          class="col-12"
          headline="How should your pump be called?"
        >
          <div class="row justify-center">
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
        </c-setup-step>
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
        <c-setup-step
          class="col-12"
          headline="Select the pins that control the pump"
        >
          <c-pump-setup-stepper-hardware-pins
            v-if="pump.type === 'stepper'"
            :enable-pin="pump.enablePin"
            @update:enable-pin="setPumpAttr('enablePin', pump.enablePin, $event)"
            :enable-pin-error-msg="attrState.enablePin.errorMsg"
            :enable-pin-loading="attrState.enablePin.loading"
            :step-pin="pump.stepPin"
            @update:step-pin="setPumpAttr('stepPin', pump.stepPin, $event)"
            :step-pin-error-msg="attrState.stepPin.errorMsg"
            :step-pin-loading="attrState.stepPin.loading"
          />
          <c-pump-setup-dc-hardware-pins
            v-if="pump.type === 'dc'"
            :pin="pump.pin"
            @update:pin="setPumpAttr('pin', pump.pin, $event)"
            :pin-error-msg="attrState.pin.errorMsg"
            :pin-loading="attrState.pin.loading"
            :is-power-state-high="pump.isPowerStateHigh"
            @update:is-power-state-high="setPumpAttr('isPowerStateHigh', pump.isPowerStateHigh, $event)"
            :is-power-state-high-error-msg="attrState.isPowerStateHigh.errorMsg"
            :is-power-state-high-loading="attrState.isPowerStateHigh.loading"
          />
        </c-setup-step>
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
        <c-setup-step
          class="col-12"
          headline="Calibrate your pump"
        >
          <c-pump-setup-stepper-calibration
            v-if="pump.type === 'stepper'"
            :acceleration="pump.acceleration"
            :acceleration-error-msg="attrState.acceleration.errorMsg"
            :acceleration-loading="attrState.acceleration.loading"
            @update:acceleration="setPumpAttr('acceleration', pump.acceleration, $event)"
            :steps-per-cl="pump.stepsPerCl"
            :steps-per-cl-error-msg="attrState.stepsPerCl.errorMsg"
            :steps-per-cl-loading="attrState.stepsPerCl.loading"
            @update:steps-per-cl="setPumpAttr('stepsPerCl', pump.stepsPerCl, $event)"
            :max-steps-per-second="pump.maxStepsPerSecond"
            :max-steps-per-second-error-msg="attrState.maxStepsPerSecond.errorMsg"
            :max-steps-per-second-loading="attrState.maxStepsPerSecond.loading"
            @update:max-steps-per-second="setPumpAttr('maxStepsPerSecond', pump.maxStepsPerSecond, $event)"
          />
          <c-pump-setup-dc-calibration
            v-if="pump.type === 'dc'"
            :time-per-cl-in-ms="pump.timePerClInMs"
            @update:time-per-cl-in-ms="setPumpAttr('timePerClInMs', pump.timePerClInMs, $event)"
            :time-per-cl-in-ms-error-msg-error-msg="attrState.timePerClInMs.errorMsg"
            :time-per-cl-in-ms-loading-loading="attrState.timePerClInMs.loading"
          />
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
                disable-reason="Required pump-config parameter missing!"
                @update:perClMetric="setPerClMetric($event)"
              />
            </template>
          </c-assistant-container>
          <q-splitter
            :model-value="10"
            horizontal
            class="q-py-md"
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
        </c-setup-step>
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
        <c-setup-step
          class="col-12"
          headline="Pump state"
        >
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
        </c-setup-step>
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
import CPumpSetupStepperHardwarePins from 'components/pumpsetup/CPumpSetupStepperHardwarePins.vue'
import CSetupStep from 'components/pumpsetup/CSetupStep.vue'
import CPumpSetupStepperCalibration from 'components/pumpsetup/CPumpSetupStepperCalibration.vue'
import CPumpSetupDcHardwarePins from 'components/pumpsetup/CPumpSetupDcHardwarePins.vue'
import CPumpSetupDcCalibration from 'components/pumpsetup/CPumpSetupDcCalibration.vue'

export default {
  name: 'SetupPump',
  components: {
    CPumpSetupDcCalibration,
    CPumpSetupDcHardwarePins,
    CPumpSetupStepperCalibration,
    CSetupStep,
    CPumpSetupStepperHardwarePins,
    CPumpTester,
    CAssistantContainer,
    CIngredientSelector
  },
  data () {
    return {
      stepper: 0,
      pump: {
        id: '',
        type: 'dc',
        name: '',
        currentIngredient: '',
        fillingLevelInMl: '',
        tubeCapacityInMl: '',
        pumpedUp: false,

        // Stepper-Motor
        stepPin: '',
        enablePin: '',
        stepsPerCl: '',
        maxStepsPerSecond: '',
        acceleration: '',

        // DC-Motor
        pin: '',
        isPowerStateHigh: '',
        timePerClInMs: ''
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
        },
        pin: {
          loading: false,
          errorMsg: '',
          saved: false
        },
        timePerClInMs: {
          loading: false,
          errorMsg: '',
          saved: false
        },
        isPowerStateHigh: {
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
    },
    setPerClMetric (value) {
      switch (this.pump.type) {
        case 'dc':
          this.setPumpAttr('timePerClInMs', this.pump.timePerClInMs, value)
          return
        case 'stepper':
          this.setPumpAttr('stepsPerCl', this.pump.stepsPerCl, value)
          return
      }
      throw new Error('Unknown pump type: ' + this.pump.type)
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
      switch (this.pump.type) {
        case 'dc':
          return (!!this.pump.pin)
        case 'stepper':
          return (!!this.pump.stepPin && !!this.pump.enablePin)
      }
      throw new Error('Unknown pump type: ' + this.pump.type)
    },
    calibrationComplete () {
      let val = !!this.pump.tubeCapacityInMl
      switch (this.pump.type) {
        case 'dc':
          val &&= !!this.pump.timePerClInMs && (this.pump.isPowerStateHigh !== null)
          break
        case 'stepper':
          val &&= !!this.pump.acceleration && !!this.pump.maxStepsPerSecond &&
            !!this.pump.stepsPerCl
          break
        default:
          throw new Error('Unknown pump type: ' + this.pump.type)
      }
      return val
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
