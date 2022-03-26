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
          <q-card flat
                  bordered
                  class="bg-info"
          >
            <q-card-section class="q-pa-none">
              <div class="q-pa-sm">
                The following ingredient-groups have to get real existing ingredients assigned:
              </div>
              <q-separator></q-separator>
            </q-card-section>
          </q-card>
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
import CIngredientSelector from '../components/CIngredientSelector'
import { maxValue, minValue, required } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'
import CMakeCocktailDialogInsufficientIngredients from 'components/CMakeCocktailDialogInsufficientIngredients'
import CMakeCocktailDialogPumpEditor from 'components/CMakeCocktailDialogPumpEditor'
import CMakeCocktailDialogIngredientsToAddManually from 'components/CMakeCocktailDialogIngredientsToAddManually'

export default {
  name: 'CMakeCocktailDialog',
  components: { CMakeCocktailDialogIngredientsToAddManually, CMakeCocktailDialogPumpEditor, CMakeCocktailDialogInsufficientIngredients, CIngredientSelector },
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
        missingIngredientGroupReplacements: [],
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
        this.checkFeasibility()
      }
    },
    getPumpLayout: {
      handler () {
        this.checkFeasibility()
      }
    }
  },
  methods: {
    checkFeasibility () {
      this.checkingFeasibility = true
      CocktailService.checkFeasibility(this.recipe.id, this.amountToProduce)
        .then(report => {
          this.feasibilityReport = report
        }).finally(() => {
          this.checkingFeasibility = false
        })
    },
    onMakeCocktail () {
      CocktailService.order(this.recipe.id, this.amountToProduce)
        .then(() => {
          this.$refs.mcDialog.hide()
          this.showProgressDialog = true
          this.checkFeasibility()
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
