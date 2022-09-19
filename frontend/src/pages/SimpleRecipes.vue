<template>
  <q-page padding class="page-content">
    <h4 class="text-white text-center q-pb-lg">Recipes</h4>
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
