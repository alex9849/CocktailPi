<template>
  <q-page class="page-content" padding>
    <h5>Edit Recipe</h5>
    <q-banner v-if="error !== ''" rounded dense class="text-white bg-red-5" style="margin-bottom: 20px">
      {{ error }}
    </q-banner>
    <q-card
      flat
    >
      <recipe-editor-form
        v-model:model-value="editRecipe"
        @valid="isValid = true"
        @invalid="isValid = false"
      >
        <template v-slot:below>
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
import RecipeEditorForm from '../components/RecipeEditorForm'
import RecipeService from '../services/recipe.service'

export default {
  name: 'RecipeEdit',
  components: { RecipeEditorForm },
  data () {
    return {
      editRecipe: {
        recipe: {},
        image: null,
        removeImage: false
      },
      error: '',
      isValid: false,
      sending: false,
      loading: true
    }
  },
  async beforeRouteEnter (to, from, next) {
    const recipe = await RecipeService.getRecipe(to.params.id)
    next(vm => {
      vm.editRecipe.recipe = recipe
    })
  },
  methods: {
    updateRecipe () {
      this.sending = true
      const copy = Object.assign({}, this.editRecipe.recipe)
      for (const pStep of copy.productionSteps) {
        if (pStep.type !== 'addIngredients') {
          continue
        }
        for (const stepIngredient of pStep.stepIngredients) {
          stepIngredient.ingredientId = stepIngredient.ingredient.id
        }
      }
      RecipeService.updateRecipe(copy, this.editRecipe.image, this.editRecipe.removeImage)
        .then(response => {
          this.sending = false
          this.$q.notify({
            type: 'positive',
            message: 'Recipe updated successfully'
          })
          this.$router.push({name: 'recipedetails', params: {id: this.$route.params.id}})
        }, error => {
          this.sending = false
          this.error = error.response.data.message
          this.$q.notify({
            type: 'negative',
            message: 'Couldn\'t update recipe. ' + error.response.data.message
          })
        })
    }
  }
}
</script>

<style scoped>

</style>
