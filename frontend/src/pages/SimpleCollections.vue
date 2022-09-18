<template>
  <q-page class="page-content">
    <q-page padding>
      <h6 class="text-white text-center">Collections</h6>
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
    </q-page>
  </q-page>
</template>

<script>
import CollectionService from 'src/services/collection.service'
import { mapGetters } from 'vuex'

export default {
  name: 'SimpleCollections',
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

</style>
