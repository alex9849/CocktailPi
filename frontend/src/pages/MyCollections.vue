<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="My collections"/>
    </q-breadcrumbs>
    <h5>My collections</h5>
    <div class="q-pa-md row q-gutter-y-sm">
        <router-link v-for="collection of collections"
                     class="col-12 col-md-6 col-lg-4 d-flex items-stretch"
                     :to="{name: 'collection', params: {id: collection.id}}"
        >
          <c-collection-card
            :value="collection"
            class="q-ma-xs full-height"
          />
        </router-link>
        <router-link class="col-12 col-md-6 col-lg-4 d-flex items-stretch"
                     :to="{name: 'collection'}"
        >
          <q-card
            :value="collection"
            class="q-ma-xs full-height bg-grey-3"
            flat
            bordered
          >
            <q-card-actions align="center" class="full-height" >
              <q-icon class="full-height" size="100px" :name="mdiPlusCircleOutline"></q-icon>
            </q-card-actions>
          </q-card>
        </router-link>
    </div>
  </q-page>
</template>

<script>
import CCollectionCard from "components/CCollectionCard";
import CollectionService from "../services/collection.service";
import {store} from '../store';
import {mdiPlusCircleOutline} from "@quasar/extras/mdi-v5";

export default {
  name: "MyCollections",
  components: {CCollectionCard},
  async beforeRouteEnter(to, from, next) {
    let userId = store.getters['auth/getUser'].id;
    let collections = await CollectionService.getCollectionsByUser(userId);
    next(vm => {
      vm.collections = collections;
    })
  },
  data() {
    return {
      collections: []
    }
  },
  created() {
    this.mdiPlusCircleOutline = mdiPlusCircleOutline;
  }
}
</script>

<style scoped>
a {
  text-decoration: none;
  color: inherit;
}
</style>
