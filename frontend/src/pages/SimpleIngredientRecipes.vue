<template>
  <q-page padding class="page-content column flex">
    <h4 class="text-white text-center">{{ $t('page.simple_ingredient_recipes.headline') }}</h4>
    <div class="row">
      <div class="col-12 justify-center">
        <div>
          <c-simple-recipe-list
            :recipes="recipes"
          />
          <q-inner-loading
            dark class="text-white"
            :showing="loading"
            size="80px"
          />
        </div>
      </div>
    </div>
    <div class="row items-center"
         style="flex-grow: 1"
         v-if="recipes.length === 0 && !loading"
    >
      <div
        class="col-12 text-h5 text-white"
      >
        <div class="row items-center justify-center">
          <q-icon :name="mdiAlert" color="white" size="lg"/>
          <p>
            {{ $t('page.simple_ingredient_recipes.no_data_msg') }}
          </p>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script>

import CSimpleRecipeList from 'components/CSimpleRecipeList.vue'
import RecipeService from 'src/services/recipe.service'
import { mdiAlert } from '@quasar/extras/mdi-v5'

export default {
  name: 'SimpleIngredientRecipes',
  components: { CSimpleRecipeList },
  data: () => {
    return {
      recipes: [],
      loading: true
    }
  },
  created () {
    this.fetchIngredientRecipes()
    this.mdiAlert = mdiAlert
  },
  methods: {
    fetchIngredientRecipes () {
      this.loading = true
      RecipeService.getIngredientRecipes()
        .then(x => {
          this.recipes = x
        })
        .finally(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style scoped>

</style>
