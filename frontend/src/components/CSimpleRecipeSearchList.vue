<template>
  <div>
    <div class="row flex q-col-gutter-lg justify-evenly">
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
