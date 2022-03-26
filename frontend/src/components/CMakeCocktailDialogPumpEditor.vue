<template>
  <q-table
    :columns="columns"
    :rows="sortedPumpLayout"
    :pagination="{rowsPerPage: 0}"
    hide-bottom
    flat
  >
    <template v-slot:body-cell-currentIngredient="props">
      <q-td
        key="currentIngredient"
        :props="props"
      >
        <c-ingredient-selector
          :selected="props.row.currentIngredient"
          @update:selected="updatePumpIngredient(props.row, $event)"
          clearable
          dense
          filter-manual-ingredients
          filter-ingredient-groups
          :bg-color="markPump(props.row)? 'green-3':undefined"
          :no-input-options="missingAutomaticIngredients"
          :loading="loadingPumpIdsCurrentIngredient.includes(props.row.id, 0)"
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
          :model-value="props.row.fillingLevelInMl"
          @update:model-value="updatePumpFillingLevel(props.row, $event)"
          debounce="500"
          :loading="loadingPumpIdsFillingLevel.includes(props.row.id, 0)"
          type="number"
          dense
          outlined
        >
          <template v-slot:append>
            ml
          </template>
        </q-input>
      </q-td>
    </template>
    <template v-slot:body-cell-actions="props">
      <q-td
        key="actions"
        class="q-pa-md q-gutter-x-sm"
        :props="props"
      >
        <q-btn
          :icon="mdiPlay"
          color="green"
          @click="onClickCleanPump(props.row)"
          dense
          rounded
          :loading="isCleaning(props.row.id)"
        >
          <q-tooltip>
            Pump up
          </q-tooltip>
        </q-btn>
      </q-td>
    </template>
  </q-table>
</template>

<script>
import PumpService, { pumpDtoMapper } from 'src/services/pump.service'
import { mapGetters } from 'vuex'
import { mdiPlay } from '@quasar/extras/mdi-v5'
import CIngredientSelector from 'components/CIngredientSelector'

export default {
  name: 'CMakeCocktailDialogPumpEditor',
  components: { CIngredientSelector },
  props: {
    neededIngredients: {
      type: Array,
      required: true
    },
    unassignedIngredients: {
      type: Array,
      required: true
    }
  },
  setup () {
    return {
      mdiPlay: mdiPlay
    }
  },
  data () {
    return {
      columns: [
        { name: 'id', label: 'Nr', field: 'id', align: 'left' },
        { name: 'currentIngredient', label: 'Current Ingredient', field: 'currentIngredient', align: 'center' },
        { name: 'fillingLevelInMl', label: 'Remaining filling level', field: 'fillingLevelInMl', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ],
      loadingPumpIdsCurrentIngredient: [],
      loadingPumpIdsFillingLevel: []
    }
  },
  methods: {
    markPump (pump) {
      if (!pump.currentIngredient || !this.isIngredientNeeded(pump.currentIngredient.id)) {
        return false
      }
      return this.sortedPumpLayout.find(x => {
        return !!x.currentIngredient &&
          pump.currentIngredient.id === x.currentIngredient.id
      }) === pump
    },
    isIngredientNeeded (ingredientId) {
      return this.neededIngredients.some(x => x.id === ingredientId)
    },
    onClickCleanPump (pump) {
      PumpService.cleanPump(pump.id)
    },
    updatePumpFillingLevel (pump, newFillingLevel) {
      if (!newFillingLevel || newFillingLevel < 0) {
        return
      }
      const dto = pumpDtoMapper.toPumpCreateDto(pump)
      dto.fillingLevelInMl = newFillingLevel
      this.loadingPumpIdsFillingLevel.push(pump.id)
      PumpService.updatePump(pump.id, dto)
        .finally(() => {
          const array = this.loadingPumpIdsFillingLevel
          array.splice(array.indexOf(pump.id), 1)
        })
    },
    updatePumpIngredient (pump, newIngredient) {
      const dto = pumpDtoMapper.toPumpCreateDto(pump)
      dto.currentIngredientId = newIngredient?.id
      this.loadingPumpIdsCurrentIngredient.push(pump.id)
      PumpService.updatePump(pump.id, dto)
        .finally(() => {
          const array = this.loadingPumpIdsCurrentIngredient
          array.splice(array.indexOf(pump.id), 1)
        })
    }
  },
  computed: {
    ...mapGetters({
      isCleaning: 'pumpLayout/isCleaning',
      getPumpLayout: 'pumpLayout/getLayout',
      getPumpIngredients: 'pumpLayout/getPumpIngredients'
    }),
    sortedPumpLayout () {
      const sorted = []
      sorted.push(...this.getPumpLayout)
      return sorted.sort((a, b) => a.id - b.id)
    },
    missingAutomaticIngredients () {
      return this.unassignedIngredients.filter(x => x.type === 'automated')
    }
  }
}
</script>

<style scoped>

</style>
