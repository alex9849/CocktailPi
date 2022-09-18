<template>
  <q-page padding class="page-content">
    <h6 class="text-white text-center">Recipes</h6>
    <simple-recipes-search-list />
  </q-page>
</template>

<script>
import SimpleRecipesSearchList from 'pages/SimpleRecipesSearchList'
export default {
  name: 'SimpleRecipes',
  components: { SimpleRecipesSearchList }
}
</script>

<style scoped>

</style>
