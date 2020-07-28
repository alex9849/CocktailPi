<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="Add Recipe"/>
    </q-breadcrumbs>
    <h5>Add Recipe</h5>
    <q-banner v-if="error !== ''" rounded dense class="text-white bg-red-5" style="margin-bottom: 20px">
      {{ error }}
    </q-banner>
    <q-card
      flat
    >
      <recipe-editor-form
        v-model="recipe"
        @valid="isValid = true"
        @invalid="isValid = false"
      >
        <template slot="below">
          <div class="q-pa-md q-gutter-sm">
            <q-btn
              style="width: 100px"
              color="negative"
              label="Abort"
              no-caps
              @click.native="$router.back()"
            />
            <q-btn
              type="submit"
              style="width: 100px"
              color="positive"
              label="Create"
              no-caps
              :disable="loading || !isValid"
              :loading="loading"
              @click="createRecipe"
            />
          </div>
        </template>
      </recipe-editor-form>
    </q-card>
  </q-page>
</template>

<script>
  import RecipeEditorForm from "../components/RecipeEditorForm";
  import RecipeService from "../services/recipe.service"
  import Recipe from "../models/Recipe";

  export default {
    name: "RecipeAdd",
    components: {RecipeEditorForm},
    data() {
      return {
        recipe: new Recipe(0, '',true, null, '', '', [], []),
        error: '',
        isValid: false,
        loading: false
      }
    },
    methods: {
      createRecipe() {
        this.loading = true;
        RecipeService.createRecipe(this.recipe)
          .then((recipe) => {
            this.loading = false;
            this.$q.notify({
              type: 'positive',
              message: 'Recipe created successfully'
            });
            this.$router.push({name: 'recipedetails', params: {id: recipe.id}})
          }, error => {
            this.loading = false;
            this.error = error.response.data.message;
            this.$q.notify({
              type: 'negative',
              message: 'Couldn\'t update user. ' + error.response.data.message
            });
          })
      }
    }
  }
</script>

<style scoped>

</style>
