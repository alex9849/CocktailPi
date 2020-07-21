<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="Public recipes"/>
    </q-breadcrumbs>
    <h5>Public recipes</h5>
    <q-card v-for="recipe in recipes" >
      <q-card-section>
        <div
          class="row"
        >
          <q-img
            src="../assets/triangle.jpg"
            :ratio="9/12"
            class="col"
            style="max-height: 300px; max-width: 225px"
          />
          <div class="col" style="padding-left: 10px; position: relative">
            <h5
              style="margin: 0; padding-bottom: 10px;"
            >
              <b>{{ recipe.name }}</b>
            </h5>
            <div>
              {{ recipe.description }}
            </div>
            <table style="width: auto; right: inherit; left: inherit" class="absolute-bottom">
              <tr>
                <td class="min">
                  Ingredients:
                </td>
                <td>
                  <q-chip v-for="ingredient in recipe.recipeIngredients">
                    {{ ingredient.ingredient.name }}
                  </q-chip>
                </td>
                <td/>
              </tr>
              <tr>
                <td class="min">
                  Tags:
                </td>
                <td>
                  <q-chip v-for="tag in recipe.tags">
                    {{ tag }}
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
