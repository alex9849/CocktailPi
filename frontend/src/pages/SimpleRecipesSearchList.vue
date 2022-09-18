<template>
  <c-simple-recipes-filter-drawer
    v-model:filter="filter"
    @clickSearch="updateRecipes"
  />
  <c-simple-recipe-search-list/>
</template>

<script>
import CSimpleRecipeSearchList from 'components/CSimpleRecipeSearchList'
import CSimpleRecipesFilterDrawer from 'components/CSimpleRecipesFilterDrawer'
import { recipeSearchListLogic } from 'src/mixins/recipeSearchListLogic'

export default {
  name: 'SimpleRecipesSearchList',
  components: { CSimpleRecipesFilterDrawer, CSimpleRecipeSearchList },
  mixins: [recipeSearchListLogic]
}
</script>

<style scoped>
</style>
