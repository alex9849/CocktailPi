<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el v-if="$route.meta.category" label="Public recipes" :to="{name: 'publicrecipes'}"/>
      <q-breadcrumbs-el v-else label="Public recipes"/>
      <q-breadcrumbs-el v-if="$route.meta.category" :label="$route.meta.category.name"/>
    </q-breadcrumbs>
    <h5>Public recipes</h5>
    <top-button-arranger>
      <q-btn
        v-if="isRecipeCreatorRole"
        color="positive"
        label="Create recipe"
        no-caps
        :to="{name: 'recipeadd'}"
      />
      <q-btn
        color="info"
        label="Refresh"
        no-caps
        :disable="refreshing"
        @click="onRefreshButton"
      />
    </top-button-arranger>
    <div class="q-pa-md">
      <c-recipe-search-list
        :category-id="$route.params.cid"
        ref="recipeSearchList"
      />
    </div>
  </q-page>
</template>

<script>
import CRecipeSearchList from "../components/CRecipeSearchList";
import {mapGetters} from "vuex";
import {store} from '../store'
import TopButtonArranger from "components/TopButtonArranger";


export default {
  name: "PublicRecipes",
  components: {TopButtonArranger, CRecipeSearchList},
  data() {
    return {
      refreshing: false
    }
  },
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
  methods: {
    onRefreshButton() {
      this.refreshing = true
      this.$refs.recipeSearchList.updateRecipes()
        .finally(() => this.refreshing = false)
    }
  },
  computed: {
    ...mapGetters({
      recipeCategories: 'category/getCategories',
      isRecipeCreatorRole: 'auth/isRecipeCreator'
    })
  }
}
</script>

<style scoped>
</style>
