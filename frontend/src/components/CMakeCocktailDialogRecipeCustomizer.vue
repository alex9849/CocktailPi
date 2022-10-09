<template>
  <q-card flat bordered>
    <q-card-section class="q-pa-none">
      <q-expansion-item
        class="text-left bg-grey-2"
        v-model:model-value="expanded"
      >
        <template v-slot:header>
          <q-item-section>
            <q-item-label class="text-subtitle2">Order customizer</q-item-label>
          </q-item-section>
        </template>
        <q-card class="">
          <q-card-section>
            <p class="text-bold">Boosting:</p>
            <p class="text-italic">Increases (or decreases) the ml of reported boostable ingredients in the base recipe. (Usually spirits) Non-boostable ingredients are decreased (or increased). The amount of liquid dispensed remains the same!</p>
            <q-slider :model-value="customisations.boost"
                      @update:model-value="onUpdateBoost($event)"
                      color="orange"
                      label
                      :label-value="(customisations.boost - 100) + '%'"
                      label-always
                      switch-label-side
                      :min="0"
                      :max="200"
                      :step="10"
                      track-size="10px"
                      thumb-size="25px"
                      thumb-color="black"
                      snap
                      markers
            />
          </q-card-section>
          <q-card-section>
            <p class="text-bold">Additional ingredients:</p>
            <p class="text-italic">Ingredients will be added as last production-step. The dispensed amount of liquid will be increased by the amount of ordered additional ingredients.</p>
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-6 col-md-3 col-lg-2"
                   v-for="additionalIngredient in customisations.additionalIngredients"
                   :key="additionalIngredient.ingredient.id"
              >
                <c-ingredient-additional-ml-card
                  :ingredient-name="additionalIngredient.ingredient.name"
                  :amount="additionalIngredient.amount"
                  @update:amount="onChangeAdditionalIngredientAmount(additionalIngredient.ingredient.id, $event)"
                />
              </div>
              <div class="col-12 col-sm-6 col-md-3 col-lg-2">
                <q-card class="bg-grey-2 text-center full-height row items-center content-center" flat bordered>
                  <q-card-section
                    v-if="addIngredient.clicked"
                    class="q-gutter-sm col-12"
                  >
                    <p class="text-subtitle2">Add ingredient</p>
                    <c-ingredient-selector
                      rounded
                      filter-manual-ingredients
                      filter-ingredient-groups
                      :selected="addIngredient.selected"
                      @update:selected="onSelectAddIngredient"
                    />
                  </q-card-section>
                  <q-btn
                    v-else
                    unelevated
                    class="text-grey-8 full-height col-12"
                    no-caps
                    @click="onClickAddIngredient"
                  >
                    <div>
                      <q-icon :name="mdiPlusCircleOutline" size="xl" />
                      <p>Add new ingredient</p>
                    </div>
                  </q-btn>
                </q-card>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </q-expansion-item>
    </q-card-section>
  </q-card>
</template>

<script>

import { mdiPlusCircleOutline } from '@quasar/extras/mdi-v5'
import CIngredientSelector from 'components/CIngredientSelector'
import CIngredientAdditionalMlCard from 'components/CIngredientAdditionalMlCard'

export default {
  name: 'CMakeCocktailDialogRecipeCustomizer',
  components: { CIngredientAdditionalMlCard, CIngredientSelector },
  props: {
    customisations: {
      type: Object,
      required: true
    }
  },
  emits: ['update:customisations'],
  created () {
    this.mdiPlusCircleOutline = mdiPlusCircleOutline
  },
  data: () => {
    return {
      expanded: false,
      addIngredient: {
        clicked: false,
        selected: null
      },
      config: {
        slider: 100,
        ingredientValue: 0
      }
    }
  },
  methods: {
    onClickAddIngredient () {
      this.addIngredient.clicked = true
    },
    onSelectAddIngredient (ingredient) {
      this.addIngredient.clicked = false
      this.addIngredient.selected = null
      const customisationsCopy = Object.assign({}, this.customisations)
      customisationsCopy.additionalIngredients.push({
        ingredient: ingredient,
        amount: 0,
        manualAdd: true
      })
      this.$emit('update:customisations', customisationsCopy)
    },
    onUpdateBoost (value) {
      const customisationsCopy = Object.assign({}, this.customisations)
      customisationsCopy.boost = value
      this.$emit('update:customisations', customisationsCopy)
    },
    onChangeAdditionalIngredientAmount (ingredientId, amount) {
      const customisationsCopy = Object.assign({}, this.customisations)
      for (const additionalIngredient of customisationsCopy.additionalIngredients) {
        if (additionalIngredient.ingredient.id === ingredientId) {
          additionalIngredient.amount = amount
        }
      }
      this.$emit('update:customisations', customisationsCopy)
    }
  }
}
</script>

<style scoped>
</style>
