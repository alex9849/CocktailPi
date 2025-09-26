<template>
  <q-page padding class="page-content">
    <div class="row q-gutter-sm justify-around items-center q-ma-sm">
      <p class="col-grow text-h5">{{ $t('page.pump_setup.headline', { pumpName: pump.printName }) }}</p>
      <q-btn
        color="negative"
        class="col-auto"
        @click="deleteDialog.show = true"
      >
        {{ $t('page.pump_setup.delete_btn_label') }}
      </q-btn>
    </div>
    <q-stepper
      v-model:model-value="stepper"
      active-color="cyan"
      class="bg-card-body text-card-body"
      animated
      flat
      bordered
      header-nav
      :dark="color.cardHeaderDark"
    >
      <q-step
        :caption="handleComplete ? $t('page.pump_setup.caption_complete') : $t('page.pump_setup.caption_optional')"
        :title="$t('page.pump_setup.name.handle')"
        :name="0"
        :icon="mdiPencilOutline"
        header-nav
        done
        :done-icon="mdiPencilOutline"
      >
        <c-setup-step
          class="col-12"
          :headline="$t('page.pump_setup.name.headline')"
        >
          <div class="row justify-center">
            <div class="col-12 col-sm-10 col-md-7 col-lg-6 q-gutter-md">
              <q-input
                :dark="color.cardBodyDark"
                :model-value="pump.name"
                @update:model-value="setPumpAttr('name', pump.name, $event, $event === '')"
                :error-message="attrState.name.errorMsg"
                :error="!!attrState.name.errorMsg"
                :loading="attrState.name.loading"
                debounce="600"
                :placeholder="pump.printName"
                outlined
                :label="$t('page.pump_setup.name.pump_identifier_label')"
              />
            </div>
          </div>
        </c-setup-step>
        <div class="col-12">
          <q-stepper-navigation class="q-gutter-sm">
            <q-btn
              @click="onClickFinish"
              color="primary"
              :label="$t('page.pump_setup.finish_setup_btn_label')"
            />
            <q-btn
              @click="stepper++"
              color="primary"
              :label="$t('page.pump_setup.continue_step_btn_label')"
            />
          </q-stepper-navigation>
        </div>
      </q-step>
      <q-step
        :caption="hardwarePinsComplete ? $t('page.pump_setup.caption_complete') : null"
        :title="$t('page.pump_setup.hw_pins.handle')"
        :name="1"
        :icon="mdiFlashOutline"
        header-nav
        done
        :done-icon="mdiFlashOutline"
      >
        <c-setup-step
          class="col-12"
          :headline="$t('page.pump_setup.hw_pins.headline')"
        >
          <c-pump-setup-stepper-hardware-pins
            v-if="pump.type === 'stepper'"
            :enable-pin="pump.enablePin"
            @update:enable-pin="setPumpAttr('enablePin', pump.enablePin, $event, !$event)"
            :enable-pin-error-msg="attrState.enablePin.errorMsg"
            :enable-pin-loading="attrState.enablePin.loading"
            :step-pin="pump.stepPin"
            @update:step-pin="setPumpAttr('stepPin', pump.stepPin, $event, !$event)"
            :step-pin-error-msg="attrState.stepPin.errorMsg"
            :step-pin-loading="attrState.stepPin.loading"
          />
          <c-pump-setup-dc-hardware-pins
            v-if="pump.type === 'dc'"
            :pin="pump.pin"
            @update:pin="setPumpAttr('pin', pump.pin, $event, !$event)"
            :pin-error-msg="attrState.pin.errorMsg"
            :pin-loading="attrState.pin.loading"
            :is-power-state-high="pump.isPowerStateHigh"
            @update:is-power-state-high="setPumpAttr('isPowerStateHigh', pump.isPowerStateHigh, $event, $event === null)"
            :is-power-state-high-error-msg="attrState.isPowerStateHigh.errorMsg"
            :is-power-state-high-loading="attrState.isPowerStateHigh.loading"
          />
          <c-pump-setup-valve-hardware-pins
            v-if="pump.type === 'valve'"
            :pin="pump.pin"
            @update:pin="setPumpAttr('pin', pump.pin, $event, !$event)"
            :pin-error-msg="attrState.pin.errorMsg"
            :pin-loading="attrState.pin.loading"
            :is-power-state-high="pump.isPowerStateHigh"
            @update:is-power-state-high="setPumpAttr('isPowerStateHigh', pump.isPowerStateHigh, $event, $event === null)"
            :is-power-state-high-error-msg="attrState.isPowerStateHigh.errorMsg"
            :is-power-state-high-loading="attrState.isPowerStateHigh.loading"
          />
        </c-setup-step>
        <div class="col-12">
          <q-stepper-navigation class="q-gutter-sm">
            <q-btn
              @click="onClickFinish"
              color="primary"
              :label="$t('page.pump_setup.finish_setup_btn_label')"
            />
            <q-btn
              @click="stepper++"
              :disable="!hardwarePinsComplete"
              color="primary"
              :label="$t('page.pump_setup.continue_step_btn_label')"
            />
            <q-btn
              flat
              @click="stepper--"
              color="primary"
              :label="$t('page.pump_setup.go_back_step_btn_label')"
            />
          </q-stepper-navigation>
        </div>
      </q-step>
      <q-step
        :caption="calibrationComplete ? $t('page.pump_setup.caption_complete') : null"
        :title="$t('page.pump_setup.calibration.handle')"
        :name="2"
        :icon="mdiCogs"
        header-nav
        :disable="!hardwarePinsComplete"
        :done="hardwarePinsComplete"
        :done-icon="mdiCogs"
      >
        <c-setup-step
          class="col-12"
          :headline="$t('page.pump_setup.calibration.headline')"
        >
          <c-pump-setup-stepper-calibration
            v-if="pump.type === 'stepper'"
            :acceleration="pump.acceleration"
            :acceleration-error-msg="attrState.acceleration.errorMsg"
            :acceleration-loading="attrState.acceleration.loading"
            @update:acceleration="setPumpAttr('acceleration', pump.acceleration, $event, $event === '')"
            :steps-per-cl="pump.stepsPerCl"
            :steps-per-cl-error-msg="attrState.stepsPerCl.errorMsg"
            :steps-per-cl-loading="attrState.stepsPerCl.loading"
            @update:steps-per-cl="setPumpAttr('stepsPerCl', pump.stepsPerCl, $event, $event === '')"
            :max-steps-per-second="pump.maxStepsPerSecond"
            :max-steps-per-second-error-msg="attrState.maxStepsPerSecond.errorMsg"
            :max-steps-per-second-loading="attrState.maxStepsPerSecond.loading"
            @update:max-steps-per-second="setPumpAttr('maxStepsPerSecond', pump.maxStepsPerSecond, $event, $event === '')"
          />
          <c-pump-setup-dc-calibration
            v-if="pump.type === 'dc'"
            :time-per-cl-in-ms="pump.timePerClInMs"
            @update:time-per-cl-in-ms="setPumpAttr('timePerClInMs', pump.timePerClInMs, $event, $event === '')"
            :time-per-cl-in-ms-error-msg-error-msg="attrState.timePerClInMs.errorMsg"
            :time-per-cl-in-ms-loading-loading="attrState.timePerClInMs.loading"
          />
          <c-pump-setup-valve-calibration
            v-if="pump.type === 'valve'"
            :has-load-cell="pump.loadCell"
            :has-load-cell-calibrated="pump.loadCellCalibrated"
          />
          <q-separator
            class="q-my-md"
            :dark="color.cardBodyDark"
          />
          <c-assistant-container>
            <template v-slot:explanations>
              <div
                v-if="pump.type === 'stepper'"
                v-html="$t('page.pump_setup.calibration.motor_tester.stepper_desc')"
              />
              <div
                v-if="pump.type === 'dc'"
                v-html="$t('page.pump_setup.calibration.motor_tester.dc_desc')"
              />
              <div
                v-if="pump.type === 'valve'"
              >
                <p>
                  Here you can test your valve and load cell. Note that the amount of liquid pumped is measured by the load cell.
                  This means that only liquid that has been poured into the glass counts towards the amount dispensed.
                  Unlike stepper or DC pumps, liquid that remains in the tubing isn't counted.
                </p>
              </div>
            </template>
            <template v-slot:fields>
              <p class="text-subtitle1 text-center">
                {{ $t('page.pump_setup.calibration.motor_tester.headline') }}
              </p>
              <c-pump-tester
                :pump="pump"
                class="text-black"
                :disable="pump.state === 'INCOMPLETE'"
                :disable-reason="$t('page.pump_setup.calibration.motor_tester.disable_reason_parameter_missing')"
                @update:perClMetric="setPerClMetric($event)"
              />
            </template>
          </c-assistant-container>
          <q-separator
            class="q-my-md"
            :dark="color.cardBodyDark"
            v-if="pump.type !== 'valve'"
          />
          <c-assistant-container
            v-if="pump.type !== 'valve'"
          >
            <template v-slot:explanations>
              {{ $t('page.pump_setup.calibration.tube_capacity_desc') }}
            </template>
            <template v-slot:fields>
              <q-input
                :dark="color.cardBodyDark"
                :model-value="pump.tubeCapacityInMl"
                @update:model-value="setPumpAttr('tubeCapacityInMl', pump.tubeCapacityInMl, $event, $event === '')"
                :error-message="attrState.tubeCapacityInMl.errorMsg"
                :error="!!attrState.tubeCapacityInMl.errorMsg"
                :loading="attrState.tubeCapacityInMl.loading"
                debounce="600"
                outlined
                type="number"
                :label="$t('page.pump_setup.calibration.tube_capacity_label')"
              >
                <template v-slot:append>
                  ml
                </template>
              </q-input>
            </template>
          </c-assistant-container>
          <q-separator
            class="q-my-md"
            :dark="color.cardBodyDark"
          />
          <c-assistant-container>
            <template v-slot:explanations>
              <p>{{ $t('page.pump_setup.calibration.power_consumption_desc') }}</p>
              <div class="row justify-center q-py-md">
                <div class="col-auto">
                  <q-btn
                    class="text-white bg-info"
                    dense
                    no-caps
                    @click="$router.push({name: 'powerlimitsettings'})"
                  >
                    {{ $t('page.pump_setup.calibration.power_consumption_link_text') }}
                  </q-btn>
                </div>
              </div>
            </template>
            <template v-slot:fields>
              <q-input
                :dark="color.cardBodyDark"
                :model-value="pump.powerConsumption"
                @update:model-value="setPumpAttr('powerConsumption', pump.powerConsumption, $event === '' ? 0 : Number($event))"
                :error-message="attrState.powerConsumption.errorMsg"
                :error="!!attrState.powerConsumption.errorMsg"
                :loading="attrState.powerConsumption.loading"
                debounce="600"
                outlined
                type="number"
                :label="$t('page.pump_setup.calibration.power_consumption_label')"
              >
                <template v-slot:append>
                  mW
                </template>
              </q-input>
            </template>
          </c-assistant-container>
        </c-setup-step>
        <div class="col-12">
          <q-stepper-navigation class="q-gutter-sm">
            <q-btn
              @click="onClickFinish"
              color="primary"
              :label="$t('page.pump_setup.finish_setup_btn_label')"
            />
            <q-btn
              @click="stepper++"
              :disable="!calibrationComplete"
              color="primary"
              :label="$t('page.pump_setup.continue_step_btn_label')"
            />
            <q-btn
              flat @click="stepper--"
              color="primary"
              :label="$t('page.pump_setup.go_back_step_btn_label')"
              class="q-ml-sm"
            />
          </q-stepper-navigation>
        </div>
      </q-step>
      <q-step
        caption="Optional"
        :title="$t('page.pump_setup.state.handle')"
        :name="3"
        :icon="mdiPencilOutline"
        header-nav
        :done="calibrationComplete"
        :disable="!calibrationComplete || !hardwarePinsComplete"
        :done-icon="mdiPencilOutline"
      >
        <c-setup-step
          class="col-12"
          :headline="$t('page.pump_setup.state.headline')"
        >
          <c-assistant-container>
            <template v-slot:fields>
              <c-ingredient-selector
                outlined
                :dark="color.cardBodyDark"
                :model-value="pump.currentIngredient"
                @update:model-value="setPumpAttr('currentIngredient', pump.currentIngredient, $event, !$event)"
                :error-message="attrState.currentIngredient.errorMsg"
                :error="!!attrState.currentIngredient.errorMsg"
                :loading="attrState.currentIngredient.loading"
                :label="$t('page.pump_setup.state.ingredient_label')"
                clearable
                filter-manual-ingredients
                filter-ingredient-groups
                hide-bottom-space
              />
            </template>
            <template v-slot:explanations>
              {{ $t('page.pump_setup.state.ingredient_desc') }}
            </template>
          </c-assistant-container>
          <q-separator
            :dark="color.cardBodyDark"
            class="q-my-md"
          />
          <c-assistant-container>
            <template v-slot:fields>
              <q-input
                :dark="color.cardBodyDark"
                :model-value="pump.fillingLevelInMl"
                @update:model-value="setPumpAttr('fillingLevelInMl', pump.fillingLevelInMl, $event === '' ? 0 : Number($event))"
                :error-message="attrState.fillingLevelInMl.errorMsg"
                :error="!!attrState.fillingLevelInMl.errorMsg"
                :loading="attrState.fillingLevelInMl.loading"
                debounce="600"
                :label="$t('page.pump_setup.state.filling_level_label')"
                type="number"
                outlined
                hide-bottom-space
              >
                <template v-slot:append>
                  ml
                </template>
              </q-input>
            </template>
            <template v-slot:explanations>
              {{ $t('page.pump_setup.state.filling_level_desc') }}
            </template>
          </c-assistant-container>
          <q-separator
            :dark="color.cardBodyDark"
            class="q-my-md"
          />
          <c-assistant-container>
            <template v-slot:fields>
              <div class="row justify-center">
                <div class="col-auto">
                  <q-checkbox
                    :dark="color.cardBodyDark"
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
                    :label="$t('page.pump_setup.state.pumped_up_label')"
                  />
                </div>
              </div>
            </template>
            <template v-slot:explanations>
              {{ $t('page.pump_setup.state.pumped_up_desc') }}
            </template>
          </c-assistant-container>
        </c-setup-step>
        <div class="col-12">
          <q-stepper-navigation>
            <q-btn
              @click="onClickFinish"
              color="primary"
              :label="$t('page.pump_setup.finish_setup_btn_label')"
            />
            <q-btn
              flat @click="stepper--"
              color="primary"
              :label="$t('page.pump_setup.go_back_step_btn_label')"
              class="q-ml-sm"
            />
          </q-stepper-navigation>
        </div>
      </q-step>
    </q-stepper>
    <c-question
      :question="$t('page.pump_setup.delete_dialog.headline', { name: this.pumpName })"
      :ok-button-text="$t('page.pump_setup.delete_dialog.yes_btn_label')"
      ok-color="negative"
      :loading="deleteDialog.loading"
      @clickAbort="deleteDialog.show = false"
      @clickOk="deletePump"
      v-model:show="deleteDialog.show"
    />
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
import CQuestion from 'components/CQuestion.vue'
import { mapGetters } from 'vuex'
import CPumpSetupValveHardwarePins from 'components/pumpsetup/CPumpSetupValveHardwarePins.vue'
import CPumpSetupValveCalibration from 'components/pumpsetup/CPumpSetupValveCalibration.vue'

export default {
  name: 'SetupPump',
  components: {
    CPumpSetupValveCalibration,
    CPumpSetupValveHardwarePins,
    CQuestion,
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
      deleteDialog: {
        show: false,
        loading: false
      },
      pump: {
        id: '',
        type: 'dc',
        name: '',
        currentIngredient: '',
        fillingLevelInMl: '',
        tubeCapacityInMl: '',
        powerConsumption: '',
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
        timePerClInMs: '',

        // Valve
        loadCellCalibrated: false,
        loadCell: false
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
        powerConsumption: {
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
  async beforeRouteEnter (to, from, next) {
    let pump
    try {
      pump = await PumpService.getPump(to.params.pumpId)
    } catch (e) {
      if (e.response.status === 404) {
        next({ name: '404Page' })
        return
      }
    }
    let stepper = pump.setupStage
    const stepperQuery = to.query?.stage
    if (stepperQuery) {
      stepper = Math.min(stepperQuery - 1, stepper)
    }
    next(vm => {
      vm.pump = pump
      vm.stepper = stepper
    })
  },
  created () {
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
  },
  methods: {
    onClickFinish () {
      this.$router.push({ name: 'pumpmanagement' })
    },
    deletePump () {
      this.deleteDialog.loading = true
      PumpService.deletePump(this.pump.id)
        .then(() => {
          this.$q.notify({
            type: 'positive',
            message: this.$t('page.pump_setup.notifications.pump_deleted', { name: this.pumpName })
          })
          this.$router.push({ name: 'pumpmanagement' })
        })
        // eslint-disable-next-line no-return-assign
        .finally(() => {
          this.deleteDialog.loading = false
          this.deleteDialog.show = false
        })
    },
    setPumpAttr (attr, currValue, newValue, remove = false) {
      this.pump[attr] = newValue
      this.attrState[attr].loading = true
      this.attrState[attr].saved = false
      const patch = {
        type: this.pump.type,
        removeFields: []
      }
      if (remove) {
        patch.removeFields.push(attr)
      } else {
        patch[attr] = newValue
      }
      PumpService.patchPump(this.pump.id, pumpDtoMapper.toPumpPatchDto(patch), false)
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
    ...mapGetters({
      color: 'appearance/getNormalColors'
    }),
    pumpName () {
      if (this.pump.name) {
        return this.pump.name
      }
      return this.$t('common.pump_fallback_name', { id: this.pump.id })
    },
    handleComplete () {
      return !!this.pump.name
    },
    hardwarePinsComplete () {
      return this.pump.setupStage > 1
    },
    calibrationComplete () {
      return this.pump.setupStage > 2
    }
  },
  watch: {
    stepper (val) {
      this.$router.push({ name: this.$route.name, query: { stage: val + 1 } })
    }
  }
}
</script>

<style scoped>

</style>
