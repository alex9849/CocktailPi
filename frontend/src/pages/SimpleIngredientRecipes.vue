<template>
  <q-page padding class="page-content">
    <h4 class="text-white text-center">Pumpable Ingredients</h4>
    <div>
      <c-simple-recipe-list
        :recipes="recipes"
        :no-data-message="noDataMessage"
      />
      <q-inner-loading
        dark class="text-white"
        :showing="loading"
        size="80px"
      />
    </div>
  </q-page>
</template>

<script>

import CSimpleRecipeList from 'components/CSimpleRecipeList.vue'
import RecipeService from 'src/services/recipe.service'

export default {
  name: 'SimpleIngredientRecipes',
  components: { CSimpleRecipeList },
  data: () => {
    return {
      recipes: [],
      loading: false,
      noDataMessage: 'No ingredients assigned to pumps!'
    }
  },
  created () {
    this.fetchIngredientRecipes()
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
