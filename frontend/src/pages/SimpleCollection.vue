<template>
  <q-page padding class="page-content">
    <h4 class="text-white text-center">{{ collection.name }}</h4>
    <simple-recipes-search-list
      :collection-id="collection.id"
    />
  </q-page>
</template>

<script>
import CollectionService from 'src/services/collection.service'
import SimpleRecipesSearchList from 'pages/SimpleRecipesSearchList'

export default {
  name: 'SimpleCollection',
  components: { SimpleRecipesSearchList },
  async beforeRouteEnter (to, from, next) {
    const collection = await CollectionService.getCollection(to.params.collectionId)
    next(vm => {
      vm.collection = collection
    })
  },
  data () {
    return {
      collection: {
        name: 'Test',
        description: '',
        hasImage: false,
        complete: false
      }
    }
  }
}
</script>

<style scoped>

</style>
