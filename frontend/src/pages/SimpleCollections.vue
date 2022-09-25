<template>
  <q-page padding class="page-content">
    <h4 class="text-white text-center">Collections</h4>
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
  </q-page>
</template>

<script>
import CollectionService from 'src/services/collection.service'
import { mapGetters } from 'vuex'
import CCollectionCard from 'components/CCollectionCard'

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
  },
  methods: {
    fetchCollections () {
      const vm = this
      vm.isLoading = true
      CollectionService.getCollectionsByUser(this.currentUser.id)
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
