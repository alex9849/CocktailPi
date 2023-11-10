<template>
  <q-page padding class="page-content column flex">
    <h4 class="text-center">{{ $t('page.simple_collection.headline', { name: collection.name }) }}</h4>
    <simple-recipes-search-list
      v-if="collectionLoaded"
      :collection-id="collection.id"
      @empty="showNoData = $event"
    />
    <div class="row items-center"
         style="flex-grow: 1"
         v-if="showNoData"
    >
      <div
        class="col-12 text-h5"
      >
        <div class="row items-center justify-center">
          <q-icon :name="mdiAlert" size="lg"/>
          <p>
            {{ $t('page.simple_collection.no_data_msg') }}
          </p>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script>
import CollectionService from 'src/services/collection.service'
import SimpleRecipesSearchList from 'components/SimpleRecipesSearchList'
import { mdiAlert } from '@quasar/extras/mdi-v5'
import store from '../store'

export default {
  name: 'SimpleCollection',
  created () {
    this.mdiAlert = mdiAlert
  },
  components: { SimpleRecipesSearchList },
  async beforeRouteEnter (to, from, next) {
    await store().dispatch('common/fetchDefaultFilter')
    let collection
    try {
      collection = await CollectionService.getCollection(to.params.collectionId)
    } catch (e) {
      if (e.response.status === 404) {
        next({ name: '404Page' })
        return
      }
    }
    next(vm => {
      vm.collection = collection
      vm.collectionLoaded = true
    })
  },
  data () {
    return {
      collectionLoaded: false,
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
