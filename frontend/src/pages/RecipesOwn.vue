<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="My recipes"/>
    </q-breadcrumbs>
    <h5>My Recipes</h5>

    <div class="q-pa-md">
      <c-recipe-search-list
        :only-own-recipes="true"
        :show-delete-if-permitted="true"
      />
    </div>
  </q-page>
</template>

<script>
  import CRecipeSearchList from "../components/CRecipeSearchList";

  export default {
    name: "OwnRecipes",
    components: {CRecipeSearchList}
  }
</script>

<style scoped>

</style>
