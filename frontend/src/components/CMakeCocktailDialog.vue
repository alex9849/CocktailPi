<template>
  <q-dialog
    :model-value="show"
    ref="mcDialog"
    maximized
    persistent
    :transition-duration="400"
    transition-show="slide-up"
    transition-hide="slide-down"
    @update:model-value="$emit('update:show', $event)"
  >
    <q-card
      class="text-center"
    >
      <q-card-section class="q-gutter-md">
        <q-toolbar>
          <q-toolbar-title>
            <div class="text-weight-medium">Order Cocktail</div>
            <div class="text-weight-light">{{ recipe.name }}</div>
          </q-toolbar-title>
          <q-btn flat
                 round
                 size="lg"
                 dense
                 icon="close"
                 v-close-popup
          />
        </q-toolbar>
        <q-splitter
          horizontal
          :model-value="10"
        />
      </q-card-section>
      <q-card-section class="page-content q-gutter-md">
        <div class="flex justify-center">
          <q-input
            v-model.number="v.amountToProduce.$model"
            label="Amount to produce"
            outlined
            hide-bottom-space
            input-class="text-center text-weight-medium"
            style="width: 400px"
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
        </div>
        <div class="q-gutter-y-sm">
          <c-make-cocktail-dialog-ingredient-group-replacements
            :ingredient-group-replacements="feasibilityReport.ingredientGroupReplacements"
            :all-ingredient-groups-replaced="feasibilityReport.allIngredientGroupsReplaced"
            @ReplacementUpdate="onReplacementUpdate($event.prodStepNr, $event.toReplaceId, $event.replacement)"
          />
          <c-make-cocktail-dialog-insufficient-ingredients
            :insufficient-ingredients="feasibilityReport.insufficientIngredients"
          />
          <c-make-cocktail-dialog-ingredients-to-add-manually
            :unassigned-ingredients="feasibilityReport.ingredientsToAddManually"
          />
        </div>
        <c-make-cocktail-dialog-pump-editor
          v-if="isUserPumpIngredientEditor"
          :needed-ingredients="neededIngredients"
          :unassigned-ingredients="unassignedIngredients"
        />
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
import CocktailService from '../services/cocktail.service'
import { mapGetters } from 'vuex'
import { mdiAlertOutline, mdiPlay } from '@quasar/extras/mdi-v5'
import { maxValue, minValue, required } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'
import CMakeCocktailDialogInsufficientIngredients from 'components/CMakeCocktailDialogInsufficientIngredients'
import CMakeCocktailDialogPumpEditor from 'components/CMakeCocktailDialogPumpEditor'
import CMakeCocktailDialogIngredientsToAddManually from 'components/CMakeCocktailDialogIngredientsToAddManually'
import CMakeCocktailDialogIngredientGroupReplacements from 'components/CMakeCocktailDialogIngredientGroupReplacements'

export default {
  name: 'CMakeCocktailDialog',
  components: {
    CMakeCocktailDialogIngredientGroupReplacements,
    CMakeCocktailDialogIngredientsToAddManually,
    CMakeCocktailDialogPumpEditor,
    CMakeCocktailDialogInsufficientIngredients
  },
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
      feasibilityReport: {
        insufficientIngredients: [],
        ingredientGroupReplacements: [],
        ingredientsToAddManually: [],
        feasible: false
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
      this.checkFeasibility(this.currentOrderConfigurationDto)
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
        this.checkFeasibility(this.currentOrderConfigurationDto)
      }
    },
    getPumpLayout: {
      handler () {
        this.checkFeasibility(this.currentOrderConfigurationDto)
      }
    }
  },
  methods: {
    onOrderedAmountUpdate (newAmount) {
      const config = this.currentOrderConfigurationDto
      config.amountOrderedInMl = newAmount
      this.checkFeasibility(config)
    },
    onReplacementUpdate (prodStepNr, toReplaceId, replacement) {
      const config = this.currentOrderConfigurationDto
      let currentProdStepNr = 0
      for (const prodStep of config.productionStepReplacements) {
        for (const ingredientGroupReplacement of prodStep) {
          if (currentProdStepNr === prodStepNr && ingredientGroupReplacement.ingredientGroup.id === toReplaceId) {
            ingredientGroupReplacement.replacementId = replacement?.id
          }
        }
        currentProdStepNr++
      }
      this.checkFeasibility(config)
    },
    checkFeasibility (orderConfig) {
      this.checkingFeasibility = true
      CocktailService.checkFeasibility(this.recipe.id, orderConfig)
        .then(report => {
          this.feasibilityReport = report
        }).finally(() => {
          this.checkingFeasibility = false
        })
    },
    onMakeCocktail () {
      CocktailService.order(this.recipe.id, this.currentOrderConfigurationDto)
        .then(() => {
          this.$router.push({
            name: 'recipedetails',
            params: { id: this.$route.params.id }
          })
            .then(() => {
              this.$store.commit('cocktailProgress/setShowProgressDialog', true)
            })
          this.checkFeasibility(this.currentOrderConfigurationDto)
        })
    }
  },
  computed: {
    ...mapGetters({
      isUserPumpIngredientEditor: 'auth/isPumpIngredientEditor',
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      getPumpLayout: 'pumpLayout/getLayout',
      getPumpIngredients: 'pumpLayout/getPumpIngredients'
    }),
    feasibilityOk () {
      return this.feasibilityReport.insufficientIngredients.length === 0 && !this.checkingFeasibility
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
    currentOrderConfigurationDto () {
      const config = {
        amountOrderedInMl: this.amountToProduce
      }
      const newReplacements = []
      for (const prodStep of this.feasibilityReport.ingredientGroupReplacements) {
        const prodStepReplacements = []
        for (const ingredientGroupReplacement of prodStep) {
          prodStepReplacements.push({
            ingredientGroupId: ingredientGroupReplacement.ingredientGroup.id,
            replacementId: ingredientGroupReplacement?.selectedReplacement?.id
          })
        }
        newReplacements.push(prodStepReplacements)
      }
      config.productionStepReplacements = newReplacements
      return config
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
