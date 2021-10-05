<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="My collections"/>
    </q-breadcrumbs>
    <div class="q-pa-md row items-start">
      <div class="col-12 col-md-6 col-lg-4"
           v-for="collection of collections"
      >
        <router-link :to="{name: 'collection', params: {id: collection.id}}">
          <c-collection-card
            :value="collection"
            class="q-ma-xs"
          />
        </router-link>
      </div>
    </div>
  </q-page>
</template>

<script>
import CCollectionCard from "components/CCollectionCard";

export default {
  name: "MyCollections",
  components: {CCollectionCard},
  data() {
    return {
      collections: [{
        id: 1,
        name: 'Test',
        description: 'Test',
        size: 4,
        complete: true
      }, {
        id: 1,
        name: 'Test',
        description: 'Test',
        size: 4,
        complete: true
      }, {
        id: 1,
        name: 'Test',
        description: 'Test',
        size: 4
      }, {
        id: 1,
        name: 'Test',
        description: 'Test',
        size: 4,
        complete: true
      }, {
        id: 1,
        name: 'Test',
        description: 'Test',
        size: 4,
        complete: true
      }]
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
