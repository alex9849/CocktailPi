<template>
  <q-card flat bordered>
    <q-card-section class="q-pa-none">
      <q-expansion-item
        v-model:model-value="pumpEditorExpanded"
        class="bg-grey-2"
      >
        <template v-slot:header>
          <q-item-section class="text-left">
            <q-item-label class="text-subtitle2">
              {{ $t('component.make_cocktail_pump_editor.headline') }}
            </q-item-label>
          </q-item-section>
        </template>
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
                  v-if="props.row.type === 'dc'"
                  :name="mdiPump"
                />
                <q-icon
                  v-else
                >
                  <img src="~assets/icons/stepper-motor.svg"/>
                </q-icon>
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
                    {{ printPumpState(props.row.state) }}
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
                @update:selected="setPumpAttr('currentIngredient', props.row.id, props.row.type, $event, attrState.currentIngredient, !$event)"
                :disable="getPumpState(props.row.id).occupied"
                clearable
                dense
                hide-bottom-space
                filter-manual-ingredients
                filter-ingredient-groups
                :bg-color="markPump(props.row)? 'green-3':undefined"
                :no-input-options="missingAutomaticIngredients"
                :loading="attrState.currentIngredient.has(props.row.id)"
              >
                <template
                  v-slot:afterIngredientName="{scope}"
                >
                  <q-item-label
                    v-if="!!missingAutomaticIngredients.some(x => x.id === scope.opt.id)"
                    caption
                    class="text-green"
                  >
                    {{ $t('component.make_cocktail_pump_editor.pump_table.required_ingredient') }}
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
                @update:model-value="setPumpAttr('fillingLevelInMl', props.row.id, props.row.type, $event === '' ? 0 : Number($event), attrState.fillingLevelInMl)"
                debounce="500"
                :loading="attrState.fillingLevelInMl.has(props.row.id)"
                :disable="getPumpState(props.row.id).occupied || attrState.fillingLevelInMlRefill.has(props.row.id)"
                type="number"
                dense
                outlined
              >
                <template v-slot:append>
                  <p>ml</p>
                </template>
                <template v-slot:after>
                  <q-btn
                    @click="setPumpAttr('fillingLevelInMl', props.row.id, props.row.type, Number(props.row.currentIngredient.bottleSize), attrState.fillingLevelInMlRefill)"
                    :loading="attrState.fillingLevelInMlRefill.has(props.row.id)"
                    :disable="getPumpState(props.row.id).occupied || !props.row.currentIngredient || attrState.fillingLevelInMl.has(props.row.id)"
                    dense
                    class="q-ml-xs"
                    color="green"
                  >
                    <div class="q-gutter-xs row">
                      <q-icon class="text-white">
                        <img style="filter: brightness(0) invert(1);" src="~assets/icons/refill-bottle.svg"/>
                      </q-icon>
                      <p>
                        {{ $t('component.make_cocktail_pump_editor.pump_table.refill_btn_label') }}
                      </p>
                    </div>
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
      </q-expansion-item>
    </q-card-section>
  </q-card>
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
        {
          name: 'id',
          label: this.$t('component.make_cocktail_pump_editor.pump_table.columns.nr'),
          field: 'id',
          align: 'left'
        },
        {
          name: 'currentIngredient',
          label: this.$t('component.make_cocktail_pump_editor.pump_table.columns.ingredient'),
          field: 'currentIngredient',
          align: 'center'
        },
        {
          name: 'fillingLevelInMl',
          label: this.$t('component.make_cocktail_pump_editor.pump_table.columns.filling_level'),
          field: 'fillingLevelInMl',
          align: 'center'
        },
        {
          name: 'pumpedUp',
          label: this.$t('component.make_cocktail_pump_editor.pump_table.columns.pumped_up'),
          field: 'pumpedUp',
          align: 'center'
        },
        {
          name: 'state',
          label: this.$t('component.make_cocktail_pump_editor.pump_table.columns.state'),
          align: 'center'
        },
        {
          name: 'actions',
          label: this.$t('component.make_cocktail_pump_editor.pump_table.columns.actions'),
          field: '',
          align: 'center'
        }
      ],
      pumpEditorExpanded: false,
      loadingPumpIdsCurrentIngredient: [],
      loadingPumpIdsFillingLevel: [],
      attrState: {
        currentIngredient: new Set(),
        fillingLevelInMl: new Set(),
        fillingLevelInMlRefill: new Set()
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
    setPumpAttr (attr, pumpId, pumpType, newValue, loadingSet, remove = false) {
      const patch = {
        type: pumpType,
        removeFields: []
      }
      if (remove) {
        patch.removeFields.push(attr)
      } else {
        patch[attr] = newValue
      }
      loadingSet.add(pumpId)
      PumpService.patchPump(pumpId, pumpDtoMapper.toPumpPatchDto(patch), false)
        .finally(() => {
          loadingSet.delete(pumpId)
        })
    },
    printPumpState (state) {
      if (state === 'READY') {
        return this.$t('component.make_cocktail_pump_editor.pump_table.state_ok')
      }
      return this.$t('component.make_cocktail_pump_editor.pump_table.state_incomplete')
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
