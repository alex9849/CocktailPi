<template>
  <div>
    <div class="row q-col-gutter-md justify-evenly">
      <q-inner-loading size="80px" :showing="isLoading" dark class="text-white"/>
      <div class="col-4 col-md-3"
           v-for="collection in collections"
           :key="collection.id"
      >
        <div class="bg-yellow q-pa-sm rounded-borders text-center text-weight-medium"
             style="cursor: pointer; font-size: large"
        >
          {{ collection.name }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CSimpleRecipeSearchList',
  data: () => {
    return {
      collections: [],
      isLoading: false
    }
  }
}
</script>

<style scoped>

</style>
