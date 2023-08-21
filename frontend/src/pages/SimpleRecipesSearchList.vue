<template>
  <c-simple-recipes-filter-drawer
    v-model:filter="filter"
    @clickSearch="updateRecipes"
  />
  <div class="row q-col-gutter-lg">
    <q-pagination
      class="col-12 flex justify-center"
      :model-value="pagination.page + 1"
      @update:model-value="onPageClick($event - 1)"
      :max="pagination.totalPages"
      :max-pages="9"
      active-color="yellow"
      text-color="yellow"
      active-text-color="black"
      :boundary-numbers="true"
      size="md"
      outline
      direction-links
    />
    <q-inner-loading
      dark class="text-white"
      :showing="loading"
      size="80px"
    />
    <c-simple-recipe-list
      class="col-12"
      :recipes="recipes"
      :no-data-message="noDataMessage"
    />
  </div>
</template>

<script>
import CSimpleRecipeList from 'components/CSimpleRecipeList'
import CSimpleRecipesFilterDrawer from 'components/CSimpleRecipesFilterDrawer'
import { recipeSearchListLogic } from 'src/mixins/recipeSearchListLogic'

export default {
  name: 'SimpleRecipesSearchList',
  components: { CSimpleRecipesFilterDrawer, CSimpleRecipeList },
  mixins: [recipeSearchListLogic]
}
</script>

<style scoped>
</style>
