<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="Public recipes"/>
    </q-breadcrumbs>
    <h5>Public recipes</h5>
    <q-card
      v-for="recipe in recipes"
      @click="$router.push({name: 'recipedetails', params: {id: recipe.id}})"
      style="cursor: pointer; margin-bottom: 10px"
    >
      <q-card-section>
        <div
          class="row"
        >
          <q-img
            src="../assets/cocktail-solid.png"
            :ratio="16/9"
            class="col rounded-borders"
            style="max-width: 225px; max-height: 180px"
          />
          <div class="col" style="padding-left: 10px; position: relative">
            <h5
              style="margin: 0; padding-bottom: 10px;"
            >
              <b>{{ recipe.name }}</b>
            </h5>
            <div>
              {{ recipe.shortDescription }}
            </div>
            <table style="width: auto; right: inherit; left: inherit" class="absolute-bottom">
              <tr>
                <td class="min">
                  Ingredients:
                </td>
                <td>
                  <q-chip v-if="index < 4" v-for="(ingredient, index) in recipe.recipeIngredients">
                    {{ index !== 3?ingredient.ingredient.name:'...' }}
                  </q-chip>
                </td>
                <td style="text-align: right;">
                  by {{ recipe.owner.username }}
                </td>
              </tr>
            </table>
          </div>
        </div>
      </q-card-section>
    </q-card>
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
  td {
    width: auto;
  }

  td.min {
    width: 1%;
    white-space: nowrap;
  }
</style>
