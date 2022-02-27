<template>
  <q-dialog
    :model-value="show"
    ref="mcDialog"
    @update:model-value="$emit('update:show', $event)"
  >
    <q-card
      class="text-center full-width"
    >
      <q-card-section class="q-gutter-md">
        <p class="text-h5">Order Cocktail</p>
        <q-splitter
          horizontal
          :model-value="10"
        />
        <q-input
          v-model.number="v.amountToProduce.$model"
          label="Amount to produce"
          outlined
          input-class="text-center text-weight-medium"
          rounded
          type="number"
          :rules="[
            val => !v.amountToProduce.required.$invalid || 'Required',
            val => !v.amountToProduce.minValue.$invalid || 'Min 50ml',
            val => !v.amountToProduce.maxValue.$invalid || 'Max 1000ml'
          ]"
        >
          <template v-slot:append>
            ml
          </template>
        </q-input>
        <q-card flat
                bordered
                class="bg-warning"
                v-if="unassignedIngredients.length !== 0"
        >
          <q-card-section horizontal>
            <div class="flex items-center col-auto"
                 v-if="!$q.platform.is.mobile"
            >
              <q-icon :name="mdiAlertOutline"
                      size="lg"
                      class="text-orange-14 q-pa-sm"
              ></q-icon>
            </div>
            <q-separator vertical
                         v-if="!$q.platform.is.mobile"
            />
            <div class="col">
              <div class="q-pa-sm">
                The following ingredients have to get added manually or are not assigned to pumps. You will be asked to
                add them during the production progress:
              </div>
              <q-separator></q-separator>
              <ul style="text-align: start">
                <li v-for="ingredient in unassignedIngredients" :key="ingredient.id">
                  {{ ingredient.name }}
                  <q-chip :color="ingredient.inBar? 'green-4' : 'red-4'"
                          dense
                          square
                          :ripple="false"
                  >
                    <div v-if="ingredient.inBar">in bar</div>
                    <div v-else>not in bar</div>
                  </q-chip>
                </li>
              </ul>
            </div>
          </q-card-section>
        </q-card>
        <q-card flat
                bordered
                class="bg-red-5"
                v-if="feasibilityReport.insufficientIngredients.length !== 0"
        >
          <q-card-section horizontal>
            <div class="flex items-center col-auto"
                 v-if="!$q.platform.is.mobile"
            >
              <q-icon :name="mdiAlertOutline"
                      size="lg"
                      class="text-orange-14 q-pa-sm"
              ></q-icon>
            </div>
            <q-separator vertical
                         v-if="!$q.platform.is.mobile"
            />
            <div class="col">
              <div class="q-pa-sm">
                Can't make cocktail! Some pumps don't have enough liquid left:
              </div>
              <q-separator></q-separator>
              <ul style="text-align: start">
                <li v-for="insufficientIngredient in feasibilityReport.insufficientIngredients" :key="insufficientIngredient.ingredient.id">
                  {{ insufficientIngredient.ingredient.name }}:
                  <strong>{{ insufficientIngredient.amountNeeded }} ml</strong> required
                </li>
              </ul>
            </div>
          </q-card-section>
        </q-card>
        <q-table
          v-if="isUserPumpIngredientEditor"
          :columns="columns"
          :rows="sortedPumpLayout"
          :pagination="{rowsPerPage: 0}"
          hide-bottom
          flat
          :table-style="{margin: '15px'}"
          style="background-color: #f3f3fa"
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
      </q-card-section>
      <q-card-actions
        align="center"
      >
        <q-btn
          color="positive"
          @click="onMakeCocktail()"
          :disable="hasCocktailProgress || v.amountToProduce.$invalid || !feasibilityOk"
        >
          Make cocktail
        </q-btn>
        <q-tooltip
          v-if="hasCocktailProgress"
        >
          {{ "A cocktail is already being made!" }}
        </q-tooltip>
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script>
import PumpService, { pumpDtoMapper } from '../services/pump.service'
import CocktailService from '../services/cocktail.service'
import { mapGetters } from 'vuex'
import { mdiAlertOutline, mdiPlay } from '@quasar/extras/mdi-v5'
import CIngredientSelector from '../components/CIngredientSelector'
import { maxValue, minValue, required } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'CMakeCocktailDialog',
  components: { CIngredientSelector },
  props: {
    show: {
      type: Boolean,
      required: false
    },
    recipe: {
      type: Object,
      required: true
    }
  },
  emits: ['update:show'],
  data () {
    return {
      amountToProduce: 250,
      columns: [
        { name: 'id', label: 'Nr', field: 'id', align: 'left' },
        { name: 'currentIngredient', label: 'Current Ingredient', field: 'currentIngredient', align: 'center' },
        { name: 'fillingLevelInMl', label: 'Remaining filling level', field: 'fillingLevelInMl', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ],
      loadingPumpIdsCurrentIngredient: [],
      loadingPumpIdsFillingLevel: [],
      feasibilityReport: {
        insufficientIngredients: []
      },
      checkingFeasibility: true
    }
  },
  setup () {
    return {
      v: useVuelidate(),
      mdiPlay: mdiPlay,
      mdiAlertOutline: mdiAlertOutline
    }
  },
  created () {
    if (this.recipe) {
      this.checkFeasibility()
    }
  },
  watch: {
    recipe: {
      immediate: true,
      handler (newValue) {
        this.amountToProduce = newValue.defaultAmountToFill
      }
    },
    amountToProduce: {
      handler () {
        if (this.recipe) {
          this.checkFeasibility()
        }
      }
    },
    getPumpLayout: {
      handler () {
        if (this.recipe) {
          this.checkFeasibility()
        }
      }
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
    checkFeasibility () {
      this.checkingFeasibility = true
      CocktailService.checkFeasibility(this.recipe.id, this.amountToProduce)
        .then(report => {
          this.feasibilityReport = report
        }).finally(() => {
          this.checkingFeasibility = false
        })
    },
    isIngredientNeeded (ingredientId) {
      return this.neededIngredients.some(x => x.id === ingredientId)
    },
    updatePumpIngredient (pump, newIngredient) {
      const dto = pumpDtoMapper.toPumpCreateDto(pump)
      dto.currentIngredientId = newIngredient?.id
      this.loadingPumpIdsCurrentIngredient.push(pump.id)
      PumpService.updatePump(pump.id, dto)
        .catch(error => {
          this.$q.notify({
            type: 'negative',
            message: error.response.data.message
          })
        })
        .finally(() => {
          const array = this.loadingPumpIdsCurrentIngredient
          array.splice(array.indexOf(pump.id), 1)
          this.checkFeasibility()
        })
    },
    updatePumpFillingLevel (pump, newFillingLevel) {
      if (!newFillingLevel || newFillingLevel < 0) {
        return
      }
      const dto = pumpDtoMapper.toPumpCreateDto(pump)
      dto.fillingLevelInMl = newFillingLevel
      this.loadingPumpIdsFillingLevel.push(pump.id)
      PumpService.updatePump(pump.id, dto)
        .catch(error => {
          this.$q.notify({
            type: 'negative',
            message: error.response.data.message
          })
        })
        .finally(() => {
          const array = this.loadingPumpIdsFillingLevel
          array.splice(array.indexOf(pump.id), 1)
        })
    },
    onClickCleanPump (pump) {
      PumpService.cleanPump(pump)
        .catch(error => {
          this.$q.notify({
            type: 'negative',
            message: error.response.data.message
          })
        })
    },
    onMakeCocktail () {
      CocktailService.order(this.recipe.id, this.amountToProduce)
        .then(() => {
          this.$refs.mcDialog.hide()
          this.showProgressDialog = true
          this.checkFeasibility()
        })
        .catch(error => {
          this.$q.notify({
            type: 'negative',
            message: error.response.data.message
          })
        })
    }
  },
  computed: {
    ...mapGetters({
      isUserPumpIngredientEditor: 'auth/isPumpIngredientEditor',
      doPumpsHaveAllIngredients: 'pumpLayout/doPumpsHaveAllIngredientsForRecipe',
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      getPumpLayout: 'pumpLayout/getLayout',
      getPumpIngredients: 'pumpLayout/getPumpIngredients',
      isCleaning: 'pumpLayout/isCleaning'
    }),
    feasibilityOk () {
      return this.feasibilityReport.insufficientIngredients.length === 0 && !this.checkingFeasibility
    },
    sortedPumpLayout () {
      const sorted = []
      sorted.push(...this.getPumpLayout)
      return sorted.sort((a, b) => a.id - b.id)
    },
    neededIngredients () {
      const ingredients = []
      for (const productionStep of this.recipe.productionSteps) {
        if (productionStep.type !== 'addIngredients') {
          continue
        }
        for (const ingredientStep of productionStep.stepIngredients) {
          if (!ingredients.some(x => x.id === ingredientStep.ingredient.id)) {
            ingredients.push(ingredientStep.ingredient)
          }
        }
      }
      return ingredients
    },
    unassignedIngredients () {
      return this.neededIngredients.filter(x => !this.getPumpIngredients.some(y => x.id === y.id))
    },
    missingAutomaticIngredients () {
      return this.unassignedIngredients.filter(x => x.type === 'automated')
    },
    showProgressDialog: {
      get () {
        return this.$store.getters['cocktailProgress/isShowProgressDialog']
      },
      set (val) {
        return this.$store.commit('cocktailProgress/setShowProgressDialog', val)
      }
    }
  },
  validations () {
    return {
      amountToProduce: {
        required,
        minValue: minValue(50),
        maxValue: maxValue(1000)
      }
    }
  }
}
</script>
