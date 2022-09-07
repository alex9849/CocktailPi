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
          <c-make-cocktail-dialog-pumps-in-use />
        </div>
        <c-make-cocktail-dialog-pump-editor
          v-if="isUserPumpIngredientEditor"
          :needed-ingredients="feasibilityReport.requiredIngredients"
        />
      </q-card-section>
      <q-card-actions
        align="center"
      >
        <q-btn
          color="positive"
          @click="onMakeCocktail()"
          :disable="!cocktailOrderable"
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
import CMakeCocktailDialogPumpsInUse from 'components/CMakeCocktailDialogPumpsInUse'

export default {
  name: 'CMakeCocktailDialog',
  components: {
    CMakeCocktailDialogPumpsInUse,
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
        requiredIngredients: [],
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
      this.checkFeasibility(this.getCurrentOrderConfigurationDto())
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
      handler (value) {
        const config = this.getCurrentOrderConfigurationDto()
        config.amountOrderedInMl = value
        this.checkFeasibility(config)
      }
    },
    getPumpLayout: {
      handler () {
        this.checkFeasibility(this.getCurrentOrderConfigurationDto())
      }
    }
  },
  methods: {
    onReplacementUpdate (prodStepNr, toReplaceId, replacement) {
      const config = this.getCurrentOrderConfigurationDto()
      let currentProdStepNr = 0
      for (const prodStep of config.productionStepReplacements) {
        for (const ingredientGroupReplacement of prodStep) {
          if (currentProdStepNr === prodStepNr && ingredientGroupReplacement.ingredientGroupId === toReplaceId) {
            ingredientGroupReplacement.replacementId = replacement?.id
          }
        }
        currentProdStepNr++
      }
      this.checkFeasibility(config)
    },
    getCurrentOrderConfigurationDto () {
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
      CocktailService.order(this.recipe.id, this.getCurrentOrderConfigurationDto())
        .then(() => {
          this.$router.push({
            name: 'recipedetails',
            params: { id: this.$route.params.id }
          })
            .then(() => {
              this.$store.commit('cocktailProgress/setShowProgressDialog', true)
            })
        })
    }
  },
  computed: {
    ...mapGetters({
      isUserPumpIngredientEditor: 'auth/isPumpIngredientEditor',
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      getPumpLayout: 'pumpLayout/getLayout',
      getPumpIngredients: 'pumpLayout/getPumpIngredients',
      isAnyPumpOccupied: 'pumpLayout/anyOccupied'
    }),
    feasibilityOk () {
      return this.feasibilityReport.feasible && !this.checkingFeasibility
    },
    cocktailOrderable () {
      return this.feasibilityOk &&
        !this.isAnyPumpOccupied &&
        !this.hasCocktailProgress &&
        !this.v.amountToProduce.$invalid
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
