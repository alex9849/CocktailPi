<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="Public recipes" :to="{name: 'publicrecipes'}"/>
      <q-breadcrumbs-el label="Recipe details" :to="{name: 'recipedetails', params: {id: $route.params.id}}"/>
      <q-breadcrumbs-el label="Edit recipe"/>
    </q-breadcrumbs>
    <h5>Edit Recipe</h5>
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
        :disabled="loading"
      >
        <template slot="below">
          <div class="q-pa-md q-gutter-sm">
            <q-btn
              style="width: 100px"
              color="negative"
              label="Abort"
              no-caps
              :to="{name: 'recipedetails', params: {id: $route.params.id}}"
            />
            <q-btn
              type="submit"
              style="width: 100px"
              color="positive"
              label="Save"
              no-caps
              :disable="sending || !isValid"
              :loading="sending"
              @click="updateRecipe"
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

  export default {
    name: "RecipeEdit",
    components: {RecipeEditorForm},
    data() {
      return {
        recipe: {},
        error: '',
        isValid: false,
        sending: false,
        loading: false
      }
    },
    methods: {
      updateRecipe() {
        this.sending = true;
        RecipeService.updateRecipe(this.recipe)
          .then(response => {
            this.sending = false;
            this.$q.notify({
              type: 'positive',
              message: 'Recipe updated successfully'
            });
            this.$router.push({name: 'recipedetails', params: {id: this.$route.params.id}})
          }, error => {
            this.sending = false;
            this.error = error.response.data.message;
            this.$q.notify({
              type: 'negative',
              message: 'Couldn\'t update recipe. ' + error.response.data.message
            });
          })
      }
    },
    created() {
      this.loading = true;
      RecipeService.getRecipe(this.$route.params.id)
        .then(recipe => {
          this.recipe = recipe;
          this.loading = false;
        });
    }
  }
</script>

<style scoped>

</style>
