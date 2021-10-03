<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el v-if="$route.meta.category" label="Public recipes" :to="{name: 'publicrecipes'}"/>
      <q-breadcrumbs-el v-else label="Public recipes"/>
      <q-breadcrumbs-el v-if="$route.meta.category" :label="$route.meta.category.name"/>
    </q-breadcrumbs>
    <h5>Public recipes</h5>
    <div class="q-pa-md">
      <c-recipe-search-list
        :isOwnRecipes="false"
        :categoryId="$route.params.cid"
      />
    </div>
  </q-page>
</template>

<script>
import CRecipeSearchList from "../components/CRecipeSearchList";
import {mapGetters} from "vuex";
import {store} from '../store'


export default {
    name: "PublicRecipes",
    components: {CRecipeSearchList},
    beforeRouteEnter(to, from, next) {
      to.meta['category'] = store.getters['category/getCategories']
        .find(x => x.id == to.params.cid);
      next();
    },
    beforeRouteUpdate(to, from, next) {
      to.meta['category'] = store.getters['category/getCategories']
        .find(x => x.id == to.params.cid);
      next();
    },
    computed: {
      ...mapGetters({
        recipeCategories: 'category/getCategories'
      })
    }
  }
</script>

<style scoped>
</style>
