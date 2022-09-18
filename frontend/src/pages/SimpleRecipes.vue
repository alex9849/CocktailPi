<template>
  <q-page padding class="page-content">
    <h6 class="text-white text-center">Recipes</h6>
    <q-drawer
      overlay
      behavior="desktop"
      :width="250"
      v-model="leftDrawerOpen"
      class="bg-grey-8 shadow-5"
    >
      <div class="absolute" style="top: 25px; right: -17px">
        <q-btn
          dense
          round
          unelevated
          @click="leftDrawerOpen = false"
          color="accent"
          icon="chevron_left"
        />
      </div>
      <h6 class="text-center text-white">Filter</h6>
      <q-separator size="1" color="darkgrey" />
      <q-list padding>
        <q-item dark>
          <q-item-section>
            <q-input
              dense
              dark
              outlined
              label="Name"
              model-value=""
            />
          </q-item-section>
        </q-item>
        <q-item tag="label" dark v-ripple>
          <q-item-section side top>
            <q-checkbox dark model-value="" />
          </q-item-section>

          <q-item-section>
            <q-item-label>Automatically fabricable</q-item-label>
            <q-item-label caption>
              Only shows recipes that can be produced fully automatic
            </q-item-label>
          </q-item-section>
        </q-item>
        <q-item tag="label" dark v-ripple>
          <q-item-section side top>
            <q-checkbox dark model-value="" />
          </q-item-section>

          <q-item-section>
            <q-item-label>Fabricable</q-item-label>
            <q-item-label caption>
              Only shows recipes that can be produced with owned ingredients
            </q-item-label>
          </q-item-section>
        </q-item>
        <q-item>
          <q-item-section>
            <c-ingredient-selector
              outlined
              dense
              dark
              label="Contains ingredients"
              model-value=""
            />
          </q-item-section>
        </q-item>
        <q-item>
          <q-item-section>
            <q-select
              outlined
              dense
              dark
              label="Order by"
              :options="['Test 1', 'Test 2']"
              model-value=""
            />
          </q-item-section>
        </q-item>
        <q-item>
          <q-item-section>
            <q-btn label="Search" color="info" :icon="mdiMagnify"/>
          </q-item-section>
        </q-item>
        <q-item>
          <q-item-section>
            <q-btn label="Reset" color="red"/>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>
    <div class="absolute" style="top: 25px; left: 0px">
      <q-btn
        dense
        no-caps
        unelevated
        @click="leftDrawerOpen = true"
        color="accent"
        label="Filter"
        icon="chevron_right"
      />
    </div>
    <c-simple-recipe-search-list/>
  </q-page>
</template>

<script>
import CSimpleRecipeSearchList from 'components/CSimpleRecipeSearchList'
import CIngredientSelector from 'components/CIngredientSelector'
import { mdiMagnify } from '@quasar/extras/mdi-v5'

export default {
  name: 'SimpleRecipes',
  components: { CIngredientSelector, CSimpleRecipeSearchList },
  created () {
    this.mdiMagnify = mdiMagnify
  },
  data: () => {
    return {
      leftDrawerOpen: false
    }
  }
}
</script>

<style scoped>
.filter {
  position: absolute;
  padding-inline: 10px;
  padding-block: 5px;
  border: 1px solid white;
  transform: rotate(-90deg);
  top: 70px;
  background-color: gray;
  right: -17px
}
</style>
