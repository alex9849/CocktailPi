<template>
  <q-card
    bordered
    class="bg-card-body text-card-body"
    style="display: flex; flex-direction: column"
  >
    <q-card-section
      class="row items-center justify-around bg-card-header text-card-header q-pa-sm"
    >
      <div class="col q-px-sm">
        <p class="text-h5 q-ma-none dotted-overflow-1" style="line-height: 1.5rem">
          {{ displayName }}</p>
        <p
          class="q-ma-none dotted-overflow-1"
          style="line-height: 1rem; font-family: 'Courier New',sans-serif"
        >
          <q-icon
            v-if="pump.type === 'dc'"
            :name="mdiPump"
          />
          <q-icon
            v-else-if="pump.type === 'stepper'"
            :name="stepperMotor"
          />
          <q-icon
            v-else
            :name="mdiPipeValve"
          />
          {{ printPumpType }}
        </p>
      </div>
      <div class="col-shrink">
        <div class="row q-gutter-sm">
          <q-badge
            v-if="pump.state !== 'READY' || this.pumpJobState.runningState"
            :color="pumpState.color"
            class="text-subtitle2">
            <div
              class="row q-col-gutter-xs items-center"
            >
              <q-icon
                v-if="pumpState.icon"
                :name="pumpState.icon"
              />
              <p>
                {{ pumpState.label }}
              </p>
            </div>
          </q-badge>
          <q-badge
            v-else
            :color="pumpedUpState.color"
            class="text-subtitle2"
          >
            <div class="row q-col-gutter-sm">
              <p>
                {{ pumpedUpState.label }}
              </p>
            </div>
          </q-badge>
        </div>
      </div>
    </q-card-section>
    <q-linear-progress
      :query="progressBar.query"
      :value="progressBar.value"
      :reverse="progressBar.reverse"
      animation-speed="1000"
      :color="progressBar.color"
    />
    <q-card-section class="q-py-sm">
      <div class="row">
        <div class="col-6">
          <p class="text-weight-medium">
            {{ $t('component.pump_card.attr.ingredient') }}
          </p>
        </div>
        <div class="col-6">
          <p class="text-weight-medium">
            {{ $t('component.pump_card.attr.filling_level') }}
          </p>
        </div>
      </div>
      <div class="row">
        <div class="col-6">
          <p>
            {{ printIngredient }}
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
      class="q-py-sm row items-center"
      style="flex-grow: 1"
    >
      <div class="col">
        <div class="row">
          <div class="col-6">
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.steps_per_cl') }}
            </p>
          </div>
          <div class="col-6">
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.max_steps_per_second') }}
            </p>
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
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.acceleration') }}
            </p>
          </div>
          <div class="col-6">
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.step_pin') }}
            </p>
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
              :class="getDisplayPin(pump.stepPin).class"
            >
              {{ getDisplayPin(pump.stepPin).label }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.enable_pin') }}
            </p>
          </div>
          <div class="col-6">
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.tube_capacity') }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <p
              :class="getDisplayPin(pump.enablePin).class"
            >
              {{ getDisplayPin(pump.enablePin).label }}
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
    <q-card-section
      v-if="showDetailed && pump.type === 'dc'"
      class="q-py-sm row items-center"
      style="flex-grow: 1"
    >
      <div class="col">
        <div class="row">
          <div class="col-6">
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.time_per_cl') }}
            </p>
          </div>
          <div class="col-6">
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.enable_pin') }}
            </p>
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
              :class="getDisplayPin(pump.pin).class"
            >
              {{ getDisplayPin(pump.pin).label }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.running_state') }}
            </p>
          </div>
          <div class="col-6">
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.tube_capacity') }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <p
              :class="getDisplayAttribute(pump.isPowerStateHigh == null ? null : (pump.isPowerStateHigh ? 'HIGH' : 'LOW')).class"
            >
              {{ getDisplayAttribute(pump.isPowerStateHigh == null ? null : (pump.isPowerStateHigh ? 'HIGH' : 'LOW')).label }}
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
    <q-card-section
      v-if="showDetailed && pump.type === 'valve'"
      class="q-py-sm row items-center"
      style="flex-grow: 1"
    >
      <div class="col">
        <div class="row">
          <div class="col-6">
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.load_cell') }}
            </p>
          </div>
          <div class="col-6">
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.enable_pin') }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <p
              :class="getDisplayAttribute(pump.loadCellCalibrated ? 'OK' : null).class"
            >
              {{ getDisplayAttribute(pump.loadCellCalibrated ? 'OK' : $t('component.pump_card.load_cell_not_calibrated')).label }}
            </p>
          </div>
          <div class="col-6">
            <p
              :class="getDisplayPin(pump.pin).class"
            >
              {{ getDisplayPin(pump.pin).label }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <p class="text-weight-medium">
              {{ $t('component.pump_card.attr.running_state') }}
            </p>
          </div>
        </div>
        <div class="row">
          <div class="col-6">
            <p
              :class="getDisplayAttribute(pump.isPowerStateHigh == null ? null : (pump.isPowerStateHigh ? 'HIGH' : 'LOW')).class"
            >
              {{ getDisplayAttribute(pump.isPowerStateHigh == null ? null : (pump.isPowerStateHigh ? 'HIGH' : 'LOW')).label }}
            </p>
          </div>
        </div>
      </div>
    </q-card-section>
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
              v-if="pump.canControlDirection"
              no-caps
              round
              :icon="mdiReply"
              class="bg-green text-white"
              :loading="pumpDownBtnLoading"
              @click="onClickPumpUp(true)"
              :disable="!!pumpJobState.runningState"
            >
            </q-btn>
            <q-btn
              no-caps
              round
              :icon="mdiShare"
              :loading="pumpUpBtnLoading"
              class="bg-green text-white"
              @click="onClickPumpUp(false)"
              :disable="!!pumpJobState.runningState"
            >
            </q-btn>
            <q-btn
              no-caps
              round
              :color="pumpJobState.runningState ? 'negative' : 'positive'"
              :icon="pumpJobState.runningState? mdiStop : mdiPlay"
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
  mdiSync,
  mdiClockOutline,
  mdiAlert,
  mdiPipeValve
} from '@quasar/extras/mdi-v6'
import { stepperMotor } from 'src/services/svg.service'
import WebSocketService from '../services/websocket.service'
import PumpService from 'src/services/pump.service'
import { mapGetters } from 'vuex'

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
      pumpJobState: {
        lastJobId: null,
        runningState: null
      }
    }
  },
  watch: {
    pump: {
      immediate: true,
      handler (newValue, oldValue) {
        if (oldValue !== undefined && newValue.id !== oldValue.id) {
          WebSocketService.unsubscribe('/user/topic/runningstate/' + String(oldValue.id))
        }
        if (newValue.id !== oldValue?.id) {
          WebSocketService.subscribe(this, '/user/topic/pump/runningstate/' + String(newValue.id), (data) => {
            this.pumpJobState = Object.assign({}, JSON.parse(data.body))
          }, true)
        }
      }
    }
  },
  methods: {
    onClickTurnOnOrOffPump () {
      const vm = this
      this.runningBtnLoading = true
      if (this.pumpJobState.runningState) {
        PumpService.stopPump(this.pump.id).then(() => {
          vm.$q.notify({
            type: 'positive',
            message: vm.$t('component.pump_card.notifications.pump_stopped', { name: vm.displayName })
          })
        }).finally(this.runningBtnLoading = false)
      } else {
        PumpService.startPump(this.pump.id).then(() => {
          vm.$q.notify({
            type: 'positive',
            message: vm.$t('component.pump_card.notifications.pump_started', { name: vm.displayName })
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
          label: this.$t('component.pump_card.option_missing')
        }
      } else {
        return {
          class: 'text-card-body',
          label: String(attr) + suffix
        }
      }
    },
    getDisplayPin (pin) {
      if (!pin) {
        return {
          class: 'text-red',
          label: this.$t('component.pump_card.option_missing')
        }
      } else {
        return {
          class: 'text-card-body',
          label: pin.boardName + ' / ' + pin.pinName
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
    this.mdiAlert = mdiAlert
    this.mdiPipeValve = mdiPipeValve
    this.stepperMotor = stepperMotor
    this.mdiClockOutline = mdiClockOutline
  },
  unmounted () {
    WebSocketService.unsubscribe(this, '/user/topic/pump/runningstate/' + String(this.pump.id))
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    }),
    displayName () {
      if (this.pump.name) {
        return this.pump.name
      }
      return this.$t('common.pump_fallback_name', { id: this.pump.id })
    },
    printPumpType () {
      switch (this.pump.type) {
        case 'dc':
          return this.$t('component.pump_card.dc_pump')
        case 'stepper':
          return this.$t('component.pump_card.stepper_pump')
        case 'valve':
          return this.$t('component.pump_card.valve')
      }
      return null
    },
    progressBar () {
      const abortVal = {
        value: this.pump.pumpedUp ? 1 : 0,
        query: false,
        reverse: false,
        color: 'cyan-4'
      }
      if (!this.pumpJobState.runningState) {
        return abortVal
      }
      const runningState = this.pumpJobState.runningState
      let value = runningState.forward ? runningState.percentage : (100 - runningState.percentage)
      value = value / 100
      let color = 'cyan-4'
      let query = runningState.runInfinity
      if (runningState.state === 'SUSPENDED') {
        color = 'warning'
        if (runningState.runInfinity) {
          value = 1
        }
        query = false
      }
      return {
        value,
        query,
        reverse: runningState.forward && runningState.runInfinity,
        color
      }
    },
    printIngredient () {
      if (this.pump.currentIngredient) {
        return this.pump.currentIngredient.name
      }
      return this.$t('component.pump_card.no_ingredient_placeholder')
    },
    pumpedUpState () {
      const state = {
        color: '',
        label: ''
      }
      if (this.pump.pumpedUp) {
        state.color = 'positive'
        state.label = this.$t('component.pump_card.pumpUpStates.pumped_up')
      } else {
        state.color = 'negative'
        state.label = this.$t('component.pump_card.pumpUpStates.pumped_down')
      }
      return state
    },
    pumpState () {
      const state = {
        color: '',
        label: '',
        icon: null
      }
      switch (this.pump.state) {
        case 'READY':
          state.color = 'positive'
          state.label = this.$t('component.pump_card.pumpStates.ready')
          break
        case 'INCOMPLETE':
        case 'TESTABLE':
          state.color = 'negative'
          state.label = this.$t('component.pump_card.pumpStates.incomplete')
          state.icon = this.mdiAlert
          break
      }
      if (this.pumpJobState.runningState) {
        const runningState = this.pumpJobState.runningState
        if (runningState.state === 'SUSPENDED' || runningState.state === 'READY') {
          state.color = 'warning'
          state.label = 'Suspended'
          state.icon = this.mdiClockOutline
        } else {
          state.color = 'positive'
          state.label = this.$t('component.pump_card.pumpStates.running')
        }
      }
      return state
    }
  }
}
</script>

<style scoped>

</style>
