<template>
  <q-table
    :columns="columns"
    :rows="sortedPumpLayout"
    :pagination="{rowsPerPage: 0}"
    hide-bottom
    flat
  >
    <template v-slot:body-cell-id="props">
      <q-td
        key="id"
        :props="props"
      >
        <p
          style="max-width: 150px; overflow: hidden"
        >
          <q-icon
            :name="getPumpIcon(props.row.type)"
          />
          {{ props.row.name ? props.row.name : ('#' + String(props.row.id)) }}
        </p>
      </q-td>
    </template>
    <template v-slot:body-cell-state="props">
      <q-td
        key="state"
        :props="props"
        class="text-center"
      >
        <q-badge :color="props.row.state === 'READY' ? 'positive' : 'negative'" class="text-subtitle2">
          <div>
            <p>
              {{ props.row.state === 'READY' ? 'OK' : 'Incomplete' }}
            </p>
          </div>
        </q-badge>
      </q-td>
    </template>
    <template v-slot:body-cell-currentIngredient="props">
      <q-td
        key="currentIngredient"
        :props="props"
      >
        <c-ingredient-selector
          :selected="props.row.currentIngredient"
          @update:selected="setPumpAttr('currentIngredient', props.row.id, props.row.type, $event, !$event)"
          :disable="getPumpState(props.row.id).occupied"
          clearable
          dense
          hide-bottom-space
          filter-manual-ingredients
          filter-ingredient-groups
          :bg-color="markPump(props.row)? 'green-3':undefined"
          :no-input-options="missingAutomaticIngredients"
          :loading="attrState.currentIngredient.includes(props.row.id, 0)"
        >
          <template
            v-slot:afterIngredientName="{scope}"
          >
            <q-item-label
              v-if="!!missingAutomaticIngredients.some(x => x.id === scope.opt.id)"
              caption
              class="text-green"
            >
              Required ingredient
            </q-item-label>
          </template>
        </c-ingredient-selector>
      </q-td>
    </template>
    <template v-slot:body-cell-fillingLevelInMl="props">
      <q-td
        key="fillingLevelInMl"
        :props="props"
      >
        <q-input
          style="min-width: 200px"
          :model-value="props.row.fillingLevelInMl"
          @update:model-value="setPumpAttr('fillingLevelInMl', props.row.id, props.row.type, Number($event), $event === '')"
          debounce="500"
          :loading="attrState.fillingLevelInMl.includes(props.row.id, 0)"
          :disable="getPumpState(props.row.id).occupied"
          type="number"
          dense
          outlined
        >
          <template v-slot:append>
            <p>ml</p>
          </template>
          <template v-slot:after>
            <q-btn
              @click="setPumpAttr('fillingLevelInMl', props.row.id, props.row.type, Number(props.row.currentIngredient.bottleSize), $event === '')"
              :loading="attrState.fillingLevelInMl.includes(props.row.id, 0)"
              :disable="getPumpState(props.row.id).occupied || !props.row.currentIngredient"
              dense
              class="q-ml-xs"
              color="green"
            >
              <svg width="24" height="24" xmlns="http://www.w3.org/2000/svg" class="q-mr-xs">
                <g>
                  <path id="svg_1" fill="#FFFFFF" d="m10.38095,19.2987a0.80952,0.74545 0 0 1 -0.80952,-0.74545l0,-7.45455c0,-1.49091 0.80952,-2.79545 1.61905,-2.98182l0,-3.35455a0.40476,0.37273 0 0 1 0.40476,-0.37273l0.80952,0a0.40476,0.37273 0 0 1 0.40476,0.37273l0,3.35455c0.80952,0.18636 1.61905,1.49091 1.61905,2.98182l0,7.45455a0.80952,0.74545 0 0 1 -0.80952,0.74545l-3.2381,0z"/>
                  <path id="svg_2" fill="#FFFFFF" d="m20.80898,6.44156l-3.49238,5.44156l2.24,0a7.51429,8.16234 0 0 1 -7.51429,8.16234c-1.25238,0 -2.46719,-0.3401 -3.50667,-0.95227l-1.82848,1.98617c1.54043,1.0611 3.36891,1.68688 5.33514,1.68688a10.01905,10.88312 0 0 0 10.01905,-10.88312l1.86072,0m-19.39405,0a7.51429,8.16234 0 0 1 7.51429,-8.16234c1.25238,0 2.46719,0.3401 3.50667,0.95227l1.82848,-1.98617c-1.54043,-1.0611 -3.36891,-1.68688 -5.33514,-1.68688a10.01905,10.88312 0 0 0 -10.01905,10.88312l-2.02327,0l3.27565,5.44156l3.60075,-5.44156"/>
                </g>
              </svg>
              <p>Fill</p>
            </q-btn>
          </template>
        </q-input>
      </q-td>
    </template>
    <template v-slot:body-cell-pumpedUp="props">
      <q-td
        key="pumpedUp"
        class="q-pa-md q-gutter-x-sm"
        :props="props"
      >
        <c-pumped-up-icon-button
          :pump-id="props.row.id"
          :pump-type="props.row.type"
          :disable="getPumpState(props.row.id).occupied"
          :is-pumping-up="getPumpState(props.row.id).inPumpUp"
        />
      </q-td>
    </template>
    <template v-slot:body-cell-actions="props">
      <q-td
        key="actions"
        class="q-pa-md q-gutter-x-sm"
        :props="props"
      >
        <c-pump-up-button
          v-if="isAllowReversePumping"
          :pump-id="props.row.id"
          :current-pump-direction-reversed="!getPumpState(props.row.id).forward"
          :running="getPumpState(props.row.id).inPumpUp"
          :disable="getPumpState(props.row.id).occupied"
          :pump-up-direction-reversed="true"
        />
        <c-pump-up-button
          :pump-id="props.row.id"
          :current-pump-direction-reversed="!getPumpState(props.row.id).forward"
          :running="getPumpState(props.row.id).inPumpUp"
          :disable="getPumpState(props.row.id).occupied"
          :pump-up-direction-reversed="false"
        />
        <c-pump-turn-on-off-button
          :pump-id="props.row.id"
          :running="getPumpState(props.row.id).occupied"
        />
      </q-td>
    </template>
  </q-table>
</template>

<script>
import PumpService, { pumpDtoMapper } from 'src/services/pump.service'
import { mapGetters } from 'vuex'
import CIngredientSelector from 'components/CIngredientSelector'
import CPumpUpButton from 'components/CPumpUpButton'
import CPumpTurnOnOffButton from 'components/CPumpTurnOnOffButton'
import CPumpedUpIconButton from 'components/CPumpedUpIconButton'
import WebsocketService from 'src/services/websocket.service'
import { mdiProgressClock, mdiPump } from '@quasar/extras/mdi-v5'

export default {
  name: 'CMakeCocktailDialogPumpEditor',
  components: { CPumpedUpIconButton, CPumpTurnOnOffButton, CPumpUpButton, CIngredientSelector },
  props: {
    neededIngredients: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      columns: [
        { name: 'id', label: 'Nr', field: 'id', align: 'left' },
        { name: 'currentIngredient', label: 'Current Ingredient', field: 'currentIngredient', align: 'center' },
        { name: 'fillingLevelInMl', label: 'Remaining filling level', field: 'fillingLevelInMl', align: 'center' },
        { name: 'pumpedUp', label: 'Pumped Up', field: 'pumpedUp', align: 'center' },
        { name: 'state', label: 'State', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ],
      loadingPumpIdsCurrentIngredient: [],
      loadingPumpIdsFillingLevel: [],
      attrState: {
        currentIngredient: [],
        fillingLevelInMl: []
      },
      runningStateByPumpId: new Map()
    }
  },
  created () {
    this.mdiPump = mdiPump
    this.mdiProgressClock = mdiProgressClock
  },
  unmounted () {
    for (const id of this.allPumpIds) {
      WebsocketService.unsubscribe(this, '/user/topic/pump/runningstate/' + String(id))
    }
  },
  methods: {
    markPump (pump) {
      if (!pump.currentIngredient || !this.isIngredientNeeded(pump.currentIngredient.id)) {
        return false
      }
      return this.sortedPumpLayout.find(x => {
        return !!x.currentIngredient &&
          pump.currentIngredient.id === x.currentIngredient.id && x.state === 'READY'
      }) === pump
    },
    isIngredientNeeded (ingredientId) {
      return this.neededIngredients.some(x => x.id === ingredientId)
    },
    setPumpAttr (attr, pumpId, pumpType, newValue, remove = false) {
      const patch = {
        type: pumpType,
        removeFields: []
      }
      if (remove) {
        patch.removeFields.push(attr)
      } else {
        patch[attr] = newValue
      }
      this.attrState[attr].push(pumpId)
      PumpService.patchPump(pumpId, pumpDtoMapper.toPumpPatchDto(patch), false)
        .finally(() => {
          this.attrState[attr].splice(this.attrState[attr].indexOf(pumpId), 1)
        })
    },
    getPumpState (id) {
      const abortState = {
        occupied: false,
        forward: false,
        inPumpUp: false
      }
      if (!this.runningStateByPumpId.has(id)) {
        return abortState
      }
      const state = this.runningStateByPumpId.get(id)
      return {
        occupied: !!state.runningState,
        forward: !!state.runningState?.forward,
        inPumpUp: (state.runningState) ? !state.runningState.runInfinity : false
      }
    },
    getPumpIcon (type) {
      if (type === 'stepper') {
        return this.mdiProgressClock
      } else {
        return this.mdiPump
      }
    }
  },
  watch: {
    allPumpIds: {
      immediate: true,
      handler (newVal, oldVal) {
        if (!oldVal) {
          oldVal = []
        }
        const oldValSet = new Set(oldVal)
        const intersectSet = new Set(newVal.filter(x => oldValSet.has(x)))
        const toUnsubscribe = oldVal.filter(x => !intersectSet.has(x))
        const toSubscribe = newVal.filter(x => !intersectSet.has(x))

        for (const id of toUnsubscribe) {
          WebsocketService.unsubscribe(this, '/user/topic/pump/runningstate/' + String(id))
          this.runningStateByPumpId.delete(id)
        }
        for (const id of toSubscribe) {
          WebsocketService.subscribe(this, '/user/topic/pump/runningstate/' + String(id), (data) => {
            this.runningStateByPumpId.set(id, JSON.parse(data.body))
          }, true)
        }
      }
    }
  },
  computed: {
    ...mapGetters({
      getPumpLayout: 'pumpLayout/getLayout',
      getReadyPumpIngredients: 'pumpLayout/getReadyPumpIngredients',
      isAllowReversePumping: 'common/isAllowReversePumping'
    }),
    allPumpIds () {
      return this.getPumpLayout.map(x => x.id)
    },
    sortedPumpLayout () {
      const sorted = []
      sorted.push(...this.getPumpLayout)
      return sorted.sort((a, b) => a.id - b.id)
    },
    unassignedIngredients () {
      return this.neededIngredients.filter(x => !this.getReadyPumpIngredients.some(y => x.id === y.id))
    },
    missingAutomaticIngredients () {
      return this.unassignedIngredients.filter(x => x.type === 'automated')
    }
  }
}
</script>

<style scoped>
</style>
