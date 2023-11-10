<template>
  <c-simple-recipes-filter-drawer
    v-model:filter="filter"
    @clickSearch="updateRecipes"
  />
  <div class="row q-col-gutter-lg">
    <q-pagination
      class="col-12 flex justify-center"
      :model-value="pagination.page + 1"
      @update:model-value="onPageClick($event - 1)"
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
    <q-inner-loading
      dark class="text-white"
      :showing="loading"
      size="80px"
    />
    <c-simple-recipe-list
      class="col-12"
      :recipes="recipes"
    />
  </div>
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
