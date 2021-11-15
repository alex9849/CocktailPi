<template>
  <q-page class="page-content" padding>
    <h5>Public recipes</h5>
    <div>
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
      <div class="q-py-md">
        <c-recipe-search-list
          :category-id="$route.params.cid"
          ref="recipeSearchList"
        />
      </div>
    </div>
  </q-page>
</template>

<script>
import CRecipeSearchList from '../components/CRecipeSearchList'
import {mapGetters} from 'vuex'
import {store} from '../store'
import TopButtonArranger from 'components/TopButtonArranger'

export default {
  name: 'PublicRecipes',
  components: { TopButtonArranger, CRecipeSearchList },
  data () {
    return {
      refreshing: false
    }
  },
  beforeRouteEnter (to, from, next) {
    to.meta.category = store.getters['category/getCategories']
      .find(x => x.id == to.params.cid)
    next()
  },
  beforeRouteUpdate (to, from, next) {
    to.meta.category = store.getters['category/getCategories']
      .find(x => x.id == to.params.cid)
    next()
  },
  methods: {
    onRefreshButton () {
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
