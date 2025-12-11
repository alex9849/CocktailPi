<template>
  <c-simple-recipes-filter-drawer
    v-model:filter="filter"
    @clickSearch="updateRecipes"
  />
  <div
    v-touch-swipe.horizontal="handlePageSwipe"
    class="row q-col-gutter-lg"
  >
    <q-pagination
      v-if="pagination.totalPages > 1"
      class="col-12 flex justify-center"
      :model-value="pagination.page"
      @update:model-value="onPageClick($event)"
      :max="pagination.totalPages"
      :max-pages="9"
      :active-color="paginationColors.active"
      :text-color="paginationColors.text"
      :active-text-color="paginationColors.activeText"
      :boundary-numbers="true"
      size="17px"
      outline
      direction-links
    />
    <c-simple-recipe-list
      class="col-12"
      :recipes="recipes"
    />
    <q-pagination
      v-if="pagination.totalPages > 1 && !loading"
      class="col-12 flex justify-center"
      :model-value="pagination.page"
      @update:model-value="onPageClick($event)"
      :max="pagination.totalPages"
      :max-pages="9"
      :active-color="paginationColors.active"
      :text-color="paginationColors.text"
      :active-text-color="paginationColors.activeText"
      :boundary-numbers="true"
      size="17px"
      outline
      direction-links
    />
  </div>
  <q-inner-loading
    dark class="text-white"
    style="position: fixed"
    size="80px"
    :showing="loading"
  />
</template>

<script>
import CSimpleRecipeList from 'components/CSimpleRecipeList'
import CSimpleRecipesFilterDrawer from 'components/CSimpleRecipesFilterDrawer'
import { recipeSearchListLogic } from 'src/mixins/recipeSearchListLogic'
import { mapGetters } from 'vuex'

export default {
  name: 'SimpleRecipesSearchList',
  components: { CSimpleRecipesFilterDrawer, CSimpleRecipeList },
  mixins: [recipeSearchListLogic],
  emits: ['empty'],
  methods: {
    handlePageSwipe ({ evt, ...newInfo }) {
      if (newInfo.direction === 'left' && this.pagination.page !== 0) {
        this.onPageClick(this.pagination.page + 1)
      } else if (newInfo.direction === 'right' && this.pagination.page !== this.pagination.totalPages) {
        this.onPageClick(this.pagination.page - 1)
      }
    }
  },
  watch: {
    recipes (newVal) {
      if (this.loading) {
        return
      }
      if (newVal.length === 0) {
        this.$emit('empty', true)
      } else {
        this.$emit('empty', false)
      }
    }
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getSvColors'
    }),
    paginationColors () {
      if (this.color.backgroundDark) {
        return {
          active: 'yellow',
          text: 'yellow',
          activeText: 'black'
        }
      } else {
        return {
          active: 'deep-orange-9',
          text: 'deep-orange-9',
          activeText: 'white'
        }
      }
    }
  }
}
</script>

<style scoped>
</style>
