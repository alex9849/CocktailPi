<template>
  <q-page padding class="page-content column flex">
    <h4 class="text-white text-center">{{ $t('page.simple_collections.headline') }}</h4>
    <div class="row q-col-gutter-md justify-evenly">
      <q-inner-loading size="80px" :showing="isLoading" dark class="text-white"/>
      <div class="col-12 col-sm-6 col-lg-4"
           v-for="collection in collections"
           :key="collection.id"
      >
        <router-link
          :to="{name: 'simplecollection', params: {collectionId: collection.id}}"
        >
          <c-collection-card
            :model-value="collection"
            class="full-height"
            style="max-height: 300px"
          />
        </router-link>
      </div>
    </div>
    <div class="row items-center"
         style="flex-grow: 1"
         v-if="collections.length === 0 && !isLoading"
    >
      <div
        class="col-12 text-h5 text-white"
      >
        <div class="row items-center justify-center">
          <q-icon :name="mdiAlert" color="white" size="lg"/>
          <p>
            {{ $t('page.simple_collections.no_collections_msg') }}
          </p>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script>
import CollectionService from 'src/services/collection.service'
import { mapGetters } from 'vuex'
import CCollectionCard from 'components/CCollectionCard'
import { mdiAlert } from '@quasar/extras/mdi-v5'

export default {
  name: 'SimpleCollections',
  components: { CCollectionCard },
  data: () => {
    return {
      collections: [],
      isLoading: true
    }
  },
  computed: {
    ...mapGetters({
      currentUser: 'auth/getUser'
    })
  },
  created () {
    this.fetchCollections()
    this.mdiAlert = mdiAlert
  },
  methods: {
    fetchCollections () {
      const vm = this
      vm.isLoading = true
      CollectionService.getCollections()
        .then(collections => {
          vm.collections = collections
        })
        .finally(() => {
          vm.isLoading = false
        })
    }
  }
}
</script>

<style scoped>
a {
  text-decoration: none;
  color: inherit;
}
</style>
