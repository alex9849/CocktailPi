<template>
  <div
    class="row q-pa-md bg-positive rounded-borders text-center text-weight-medium justify-center items-center"
    v-if="dense"
  >
    <p>{{ recipe.name }}</p>
  </div>
  <q-card
    v-else
    class="row items-end"
    :class="bgColor"
    flat
    bordered
  >
    <q-card-section class="q-pa-none col-12">
      <div class="text-h6 text-center text-black">{{ recipe.name }}</div>
    </q-card-section>
    <q-card-section class="q-pa-none col-12">
      <q-img
        :src="$store.getters['auth/getFormattedServerAddress'] + '/api/recipe/' + recipe.id + '/image?timestamp=' + recipe.lastUpdate.getMilliseconds()"
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
    recipe: {
      type: Object,
      required: true
    },
    dense: {
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
