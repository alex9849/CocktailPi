<template>
  <q-page padding class="page-content column flex">
    <h4 class="text-white text-center">Collection: {{ collection.name }}</h4>
    <simple-recipes-search-list
      :collection-id="collection.id"
      @empty="showNoData = $event"
    />
    <div class="row items-center"
         style="flex-grow: 1"
         v-if="showNoData"
    >
      <div
        class="col-12 text-h5 text-white"
      >
        <div class="row items-center justify-center">
          <q-icon :name="mdiAlert" color="white" size="lg"/>
          <p>
            No recipes found!
          </p>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script>
import CollectionService from 'src/services/collection.service'
import SimpleRecipesSearchList from 'pages/SimpleRecipesSearchList'
import { mdiAlert } from '@quasar/extras/mdi-v5'

export default {
  name: 'SimpleCollection',
  created () {
    this.mdiAlert = mdiAlert
  },
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
      },
      showNoData: false
    }
  }
}
</script>

<style scoped>

</style>
