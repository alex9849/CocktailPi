<template>
  <div
    class="row q-pa-md bg-positive rounded-borders text-center text-weight-medium justify-center items-center"
    v-if="dense"
  >
    <p>{{ recipe.name }}</p>
  </div>
  <q-card
    v-else
    class="row items-end shadow-5"
    :class="bgColor"
    bordered
  >
    <q-inner-loading :showing="loading" style="z-index: 1"/>
    <q-card-section class="q-pa-none col-12">
      <div class="text-h6 text-center text-black dotted-overflow-2">{{ recipe.name }}</div>
    </q-card-section>
    <q-card-section class="q-pa-none col-12">
      <q-img
        :src="imageLink"
        v-if="recipe.hasImage"
        placeholder-src="~assets/cocktail-solid.png"
        :ratio="16/9"
        style="border-radius: 0 0 4px 4px"
      />
      <q-img
        v-else
        :ratio="16/9"
        style="border-radius: 0 0 4px 4px"
        placeholder-src="~assets/cocktail-solid.png"
        src="~assets/cocktail-solid.png"
      />
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: 'CSimpleRecipeCard',
  props: {
    isIngredientRecipe: {
      type: Boolean,
      default: false
    },
    recipe: {
      type: Object,
      required: true
    },
    dense: {
      type: Boolean,
      default: false
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    bgColor () {
      if (this.allIngredientsOnPump) {
        return 'bg-green'
      }
      if (this.allIngredientsOwned) {
        return 'bg-warning'
      }
      return 'bg-grey'
    },
    imageLink () {
      if (this.isIngredientRecipe) {
        return this.$store.getters['auth/getFormattedServerAddress'] + '/api/recipe/' + this.recipe.id + '/image?timestamp=' + this.recipe.lastUpdate.getTime() + '&isIngredient=true'
      } else {
        return this.$store.getters['auth/getFormattedServerAddress'] + '/api/recipe/' + this.recipe.id + '/image?timestamp=' + this.recipe.lastUpdate.getTime()
      }
    },
    allIngredientsOwned () {
      for (const ingredient of this.recipe.ingredients) {
        if (!ingredient.inBar && !ingredient.onPump) {
          return false
        }
      }
      return true
    },
    allIngredientsOnPump () {
      for (const ingredient of this.recipe.ingredients) {
        if (!ingredient.onPump) {
          return false
        }
      }
      return true
    }
  }
}
</script>

<style scoped>

</style>
