<template>
  <q-card flat bordered>
    <q-card-section class="q-pa-none">
      <q-expansion-item
        class="text-left bg-grey-2"
        label="Order customizer"
        v-model:model-value="expanded"
      >
        <q-card>
          <q-card-section>
            <p class="text-bold">Spirit boosting:</p>
            <p class="text-italic">Increases (or decreases) the ml of reported boostable ingredients in the base recipe. (Normally spirits) Not boosted ingredients will be decreased (or increased). The dispensed amount of liquid will stay the same!</p>
            <q-slider v-model:model-value="config.slider"
                      color="orange"
                      label
                      :label-value="(config.slider - 100) + '%'"
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
              <div class="col-3">
                <q-card class="bg-grey-2 text-center full-height" flat bordered>
                  <q-card-section class="q-gutter-sm">
                    <p class="text-subtitle2">Ingredient</p>
                    <q-input v-model:model-value.number="config.ingredientValue"
                             readonly
                             rounded
                             outlined
                             suffix="ml"
                    >
                      <template v-slot:prepend >
                        <q-btn :icon="mdiMinus" round />
                      </template>
                      <template v-slot:append >
                        <q-btn :icon="mdiPlus" round />
                      </template>
                    </q-input>
                  </q-card-section>
                </q-card>
              </div>
              <div class="col-3">
                <q-card class="bg-grey-2 text-center full-height row" flat bordered>
                  <q-btn
                    unelevated
                    class="text-grey-8 col-12"
                    no-caps
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

import { mdiPlusCircleOutline, mdiPlus, mdiMinus } from '@quasar/extras/mdi-v5'

export default {
  name: 'CMakeCocktailDialogRecipeCustomizer',
  created () {
    this.mdiPlusCircleOutline = mdiPlusCircleOutline
    this.mdiPlus = mdiPlus
    this.mdiMinus = mdiMinus
  },
  data: () => {
    return {
      expanded: false,
      config: {
        slider: 100,
        ingredientValue: 0
      }
    }
  }
}
</script>

<style scoped>
</style>
