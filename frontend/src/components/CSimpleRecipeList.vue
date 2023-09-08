<template>
  <div>
    <c-make-cocktail-dialog
      :recipe="orderDialog.recipe"
      v-if="!!orderDialog.recipe"
      v-model:show="orderDialog.show"
      @postOrder="onPostOrder"
    />
    <div
      class="row justify-center q-col-gutter-lg"
    >
      <div
        class="col-3 col-lg-2"
        v-for="recipe in recipes"
        :key="recipe.id"
      >
        <c-simple-recipe-card
          style="height: 100%"
          :recipe="recipe"
          :dense="dense"
          :loading="orderDialog.loadingId === recipe.id"
          :class="{'disabled': orderDialog.loadingId !== null}"
          class="clickable"
          @click="openOrderDialog(recipe)"
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
    dense: {
      type: Boolean,
      default: false
    }
  },
  data: () => {
    return {
      orderDialog: {
        recipe: '',
        show: false,
        loadingId: null
      }
    }
  },
  watch: {
    'orderDialog.show': {
      handler (newValue) {
        if (!newValue) {
          this.orderDialog.recipe = ''
        }
      }
    }
  },
  methods: {
    onPostOrder () {
      this.$router.push({ name: 'simpleorderprogress' })
    },
    openOrderDialog (recipe) {
      if (this.orderDialog.loadingId !== null) {
        return
      }
      this.orderDialog.loadingId = recipe.id
      RecipeService.getRecipe(recipe.id, recipe.type === 'ingredientrecipe')
        .then(recipe => {
          this.orderDialog.recipe = Object.assign({}, recipe)
          this.orderDialog.show = true
        }).finally(() => {
          this.orderDialog.loadingId = null
        })
    }
  }
}
</script>

<style scoped>
</style>
