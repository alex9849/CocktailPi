<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="Public recipes"/>
    </q-breadcrumbs>
    <h5>Public recipes</h5>
    <div class="q-pa-md">
      <c-recipe-search-list
        :isOwnRecipes="false"
      />
    </div>
  </q-page>
</template>

<script>
  import CRecipeSearchList from "../components/CRecipeSearchList";

  export default {
    name: "PublicRecipes",
    components: {CRecipeSearchList}
  }
</script>

<style scoped>
</style>
