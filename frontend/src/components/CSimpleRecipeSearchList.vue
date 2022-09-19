<template>
  <div>
    <div v-if="dense" class="row flex q-col-gutter-lg justify-evenly">
      <!--q-inner-loading size="80px" :showing="isLoading" dark class="text-white"/-->
      <div class="col-4 col-md-3"
           v-for="recipe in recipes"
           :key="recipe.id"
      >
        <div class="row q-pa-md bg-positive rounded-borders text-center text-weight-medium card justify-center items-center">
          <p>{{ recipe.name }}</p>
        </div>
      </div>
    </div>
    <div v-else class="row justify-center q-col-gutter-lg">
      <div
        class="col-4 col-md-3"
        v-for="recipe in recipes"
        :key="recipe.id"
      >
        <q-card
          style="height: 100%"
          class="row items-end bg-green card"
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
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CSimpleRecipeSearchList',
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
  }
}
</script>

<style scoped>
.card {
  cursor: pointer;
  font-size: large;
  height: 100%;
}
</style>
