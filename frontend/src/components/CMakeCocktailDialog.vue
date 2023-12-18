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
      class="text-center q-pb-lg bg-background text-background"
    >
      <q-card-section class="q-gutter-md">
        <q-toolbar>
          <q-toolbar-title>
            <div class="text-weight-medium">{{ $t('component.make_cocktail_dialog.headline') }}</div>
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
        <q-separator />
      </q-card-section>
      <q-card-section class="page-content q-gutter-md">
        <c-make-cocktail-dialog-amount-to-produce
          v-model:model-value="v.amountToProduce.$model"
          :recipe-default-glass="recipe.defaultGlass"
          :default-value-no-glass="250"
        />
        <div class="q-gutter-y-sm">
          <c-make-cocktail-dialog-ingredient-group-replacements
            v-if="feasibilityReportValid"
            :ingredient-group-replacements="feasibilityReport.ingredientGroupReplacements"
            :all-ingredient-groups-replaced="feasibilityReport.allIngredientGroupsReplaced"
            @ReplacementUpdate="onReplacementUpdate($event.prodStepNr, $event.toReplaceId, $event.replacement)"
          />
          <c-make-cocktail-dialog-ingredients-to-add-manually
            :unassigned-ingredients="ingredientsToAddManually"
          />
          <c-make-cocktail-dialog-pumps-in-use
            :pumps-occupied="anyPumpOccupied"
          />
          <c-make-cocktail-dialog-insufficient-ingredients
            v-if="feasibilityReportValid"
            :required-ingredients="feasibilityReport.requiredIngredients"
          />
          <c-make-cocktail-dialog-recipe-customiser
            :customisations="customisations"
            :disable-boosting="!recipe.boostable"
            @update:customisations="onCustomisationsUpdate($event)"
          />
          <c-make-cocktail-dialog-pump-editor
            v-if="isUserPumpIngredientEditor"
            :needed-ingredients="feasibilityReport.requiredIngredients.map(x => x.ingredient)"
          />
        </div>
      </q-card-section>
      <q-card-actions
        align="center"
      >
        <q-btn
          color="positive"
          size="lg"
          no-caps
          @click="onMakeCocktail()"
          :disable="!cocktailOrderable"
        >
          {{ $t('component.make_cocktail_dialog.order_btn_label', {nr: feasibilityReport.totalAmountInMl}) }}
        </q-btn>
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script>
import CocktailService from '../services/cocktail.service'
import { mapGetters } from 'vuex'
import { mdiAlertOutline, mdiPlay, mdiPlusThick, mdiPlus, mdiMinusThick, mdiMinus, mdiClose } from '@quasar/extras/mdi-v5'
import { maxValue, minValue, required } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'
import CMakeCocktailDialogInsufficientIngredients from 'components/CMakeCocktailDialogInsufficientIngredients'
import CMakeCocktailDialogPumpEditor from 'components/CMakeCocktailDialogPumpEditor'
import CMakeCocktailDialogIngredientsToAddManually from 'components/CMakeCocktailDialogIngredientsToAddManually'
import CMakeCocktailDialogIngredientGroupReplacements from 'components/CMakeCocktailDialogIngredientGroupReplacements'
import CMakeCocktailDialogPumpsInUse from 'components/CMakeCocktailDialogPumpsInUse'
import CMakeCocktailDialogRecipeCustomiser from 'components/CMakeCocktailDialogRecipeCustomiser'
import WebsocketService from 'src/services/websocket.service'
import CMakeCocktailDialogAmountToProduce from 'components/CMakeCocktailDialogAmountToProduce.vue'

export default {
  name: 'CMakeCocktailDialog',
  components: {
    CMakeCocktailDialogAmountToProduce,
    CMakeCocktailDialogRecipeCustomiser,
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
  emits: ['update:show', 'postOrder'],
  data () {
    return {
      amountToProduce: '',
      feasibilityReportValid: false,
      feasibilityReport: {
        ingredientGroupReplacements: [],
        requiredIngredients: [],
        feasible: false,
        totalAmountInMl: 0
      },
      checkingFeasibility: true,
      pumpEditorExpanded: false,
      customisations: {
        boost: 100,
        additionalIngredients: []
      },
      runningStateByPumpId: new Map()
    }
  },
  setup () {
    return {
      v: useVuelidate(),
      mdiPlay,
      mdiPlusThick,
      mdiPlus,
      mdiMinusThick,
      mdiMinus,
      mdiAlertOutline,
      mdiClose
    }
  },
  unmounted () {
    for (const id of this.allPumpIds) {
      WebsocketService.unsubscribe(this, '/user/topic/pump/runningstate/' + String(id))
    }
  },
  watch: {
    recipe: {
      immediate: true,
      handler (newValue) {
        if (!newValue) {
          return
        }
        this.customisations.boost = 100
        this.customisations.additionalIngredients.slice(0, this.customisations.additionalIngredients.length)
        this.tryCheckFeasibility()
      }
    },
    amountToProduce: {
      handler (value) {
        const config = this.getCurrentOrderConfigurationDto()
        config.amountOrderedInMl = value
        this.tryCheckFeasibility(config)
      }
    },
    getPumpLayout: {
      handler () {
        this.tryCheckFeasibility()
      }
    },
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
  methods: {
    tryCheckFeasibility (orderConfig = this.getCurrentOrderConfigurationDto()) {
      if (!this.recipe || !this.getPumpLayout || !this.amountToProduce) {
        return
      }
      this.checkFeasibility(orderConfig)
    },
    onCustomisationsUpdate (customisations) {
      this.customisations = customisations
      this.tryCheckFeasibility()
    },
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
        amountOrderedInMl: this.amountToProduce,
        customisations: {
          boost: this.customisations.boost,
          additionalIngredients: []
        }
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
      for (const additionalIngredient of this.customisations.additionalIngredients) {
        if (additionalIngredient.amount > 0) {
          config.customisations.additionalIngredients.push({
            ingredientId: additionalIngredient.ingredient.id,
            amount: additionalIngredient.amount
          })
        }
      }
      config.productionStepReplacements = newReplacements
      return config
    },
    checkFeasibility (orderConfig) {
      this.checkingFeasibility = true
      CocktailService.checkFeasibility(this.recipe.id, orderConfig, this.isIngredientRecipe)
        .then(report => {
          this.feasibilityReport = report
          this.feasibilityReportValid = true

          const additionalIngredientIds = new Set()
          this.customisations.additionalIngredients.forEach(x => additionalIngredientIds.add(x.ingredient.id))
          for (const requiredIngredient of report.requiredIngredients) {
            const ingredient = requiredIngredient.ingredient
            if (ingredient.type !== 'automated') {
              continue
            }
            if (additionalIngredientIds.has(ingredient.id)) {
              additionalIngredientIds.delete(ingredient.id)
              continue
            }
            if (ingredient.unit !== 'ml') {
              continue
            }
            this.customisations.additionalIngredients.push({
              ingredient,
              amount: 0,
              manualAdd: false
            })
          }
          this.customisations.additionalIngredients = this.customisations.additionalIngredients.filter(x => !additionalIngredientIds.has(x.ingredient.id) || x.manualAdd)
        }).finally(() => {
          this.checkingFeasibility = false
        })
    },
    onMakeCocktail () {
      CocktailService.order(this.recipe.id, this.getCurrentOrderConfigurationDto(), this.isIngredientRecipe)
        .then(() => {
          this.$emit('postOrder')
        })
    }
  },
  computed: {
    ...mapGetters({
      isUserPumpIngredientEditor: 'auth/isPumpIngredientEditor',
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      getPumpLayout: 'pumpLayout/getLayout',
      getPumpIngredients: 'pumpLayout/getPumpIngredients',
      color: 'appearance/getNormalColors'
    }),
    allPumpIds () {
      return this.getPumpLayout.map(x => x.id)
    },
    feasibilityOk () {
      return this.feasibilityReport.feasible && !this.checkingFeasibility
    },
    isIngredientRecipe () {
      return this.recipe.type === 'ingredientrecipe'
    },
    anyPumpOccupied () {
      let anyRunning = false
      for (const state of this.runningStateByPumpId.values()) {
        anyRunning ||= !!state.runningState
      }
      return anyRunning
    },
    cocktailOrderable () {
      return this.feasibilityOk &&
        !this.anyPumpOccupied &&
        !this.hasCocktailProgress &&
        !this.v.amountToProduce.$invalid
    },
    ingredientsToAddManually () {
      return this.feasibilityReport.requiredIngredients
        .filter(x => !x.ingredient.onPump).map(x => x.ingredient)
    }
  },
  validations () {
    return {
      amountToProduce: {
        required,
        minValue: minValue(10),
        maxValue: maxValue(5000)
      }
    }
  }
}
</script>
