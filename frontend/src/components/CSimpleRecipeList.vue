<template>
  <div>
    <c-make-cocktail-dialog
      :recipe="orderDialog.recipe"
      v-if="!!orderDialog.recipe"
      v-model:show="orderDialog.show"
    />
    <div
      class="row justify-center q-col-gutter-lg"
    >
      <div
        class="col-4 col-md-3"
        v-for="recipe in recipes"
        :key="recipe.id"
      >
        <c-simple-recipe-card
          :recipe="recipe"
          :dense="dense"
          class="clickable"
          @click="openOrderDialog(recipe.id)"
        />
      </div>
    </div>
  </div>
</template>

<script>
import CSimpleRecipeCard from 'components/CSimpleRecipeCard'
import CMakeCocktailDialog from 'components/CMakeCocktailDialog'
import RecipeService from 'src/services/recipe.service'
export default {
  name: 'CSimpleRecipeList',
  components: { CMakeCocktailDialog, CSimpleRecipeCard },
  props: {
    recipes: {
      type: Array,
      required: true
    },
    noDataMessage: {
      type: String,
      required: false
    },
    dense: {
      type: Boolean,
      default: false
    }
  },
  data: () => {
    return {
      orderDialog: {
        recipe: '',
        show: false
      }
    }
  },
  methods: {
    openOrderDialog (recipeId) {
      RecipeService.getRecipe(recipeId)
        .then(recipe => {
          this.orderDialog.recipe = Object.assign({}, recipe)
          this.orderDialog.show = true
        })
    }
  }
}
</script>

<style scoped>
.clickable {
  cursor: pointer;
}
</style>
