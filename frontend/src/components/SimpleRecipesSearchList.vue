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
    <c-simple-recipe-list
      class="col-12"
      :recipes="recipes"
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
