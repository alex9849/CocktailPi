<template>
  <q-page class="page-content" padding>
    <h5>{{ $t('page.recipes.my_recipes_headline') }}</h5>
    <div>
      <top-button-arranger>
        <q-btn
          v-if="isRecipeCreatorRole"
          color="positive"
          :label="$t('page.recipes.create_recipe_btn_label')"
          no-caps
          :to="{name: 'recipeadd'}"
        />
        <q-btn
          color="info"
          :label="$t('page.recipes.refresh_btn_label')"
          no-caps
          :disable="refreshing"
          @click="onRefreshButton"
        />
      </top-button-arranger>
      <div class="q-py-md">
        <c-recipe-search-list
          :category-id="categoryId"
          ref="recipeSearchList"
        />
      </div>
    </div>
  </q-page>
</template>

<script>
import CRecipeSearchList from '../components/CRecipeSearchList'
import { mapGetters } from 'vuex'
import store from '../store'
import TopButtonArranger from 'components/TopButtonArranger'

export default {
  name: 'PublicRecipes',
  components: { TopButtonArranger, CRecipeSearchList },
  data () {
    return {
      refreshing: false
    }
  },
  async beforeRouteEnter (to, from, next) {
    to.meta.category = store().getters['category/getCategories']
      .find(x => x.id == to.params.cid)
    await store().dispatch('common/fetchDefaultFilter')
    next()
  },
  beforeRouteUpdate (to, from, next) {
    to.meta.category = store().getters['category/getCategories']
      .find(x => x.id == to.params.cid)
    next()
  },
  methods: {
    onRefreshButton () {
      this.refreshing = true
      this.$refs.recipeSearchList.updateRecipes()
        .finally(() => {
          this.refreshing = false
        })
    }
  },
  computed: {
    ...mapGetters({
      recipeCategories: 'category/getCategories',
      isRecipeCreatorRole: 'auth/isRecipeCreator'
    }),
    categoryId () {
      if (this.$route.meta.category) {
        return this.$route.meta.category.id
      }
      return null
    }
  }
}
</script>

<style scoped>
</style>
