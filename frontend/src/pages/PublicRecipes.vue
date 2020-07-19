<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="Public recipes"/>
    </q-breadcrumbs>
    <h5>Public recipes</h5>
    <q-table
      :columns="columns"
      :data="recipes"
    >

    </q-table>
  </q-page>
</template>

<script>
  import RecipeService from '../services/recipe.service'

  export default {
    name: "PublicRecipes",
    data() {
      return {
        recipes: [],
        columns: [
          {name: 'name', label: 'Name', field: 'name', align: 'left'},
          {name: 'description', label: 'Description', field: 'description', align: 'left'},
          {name: 'recipeIngredients', label: 'recipeIngredients', field: 'recipeIngredients', align: 'left'},
          {name: 'tags', label: 'Tags', field: 'tags', align: 'left'}
        ]
      }
    },
    methods: {
      fetchRecipes() {
        RecipeService.getRecipes(null, true, null)
          .then(recipes => this.recipes = recipes)
      }
    },
    created() {
      this.fetchRecipes();
    }
  }
</script>

<style scoped>

</style>
