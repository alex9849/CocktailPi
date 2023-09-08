<template>
  <div class="q-gutter-md">
    <c-recipe-search-filter-card
      v-model:filter="filter"
      ref="filter"
      class="bg-grey-1"
      @clickSearch="updateRecipes"
    />
    <c-recipe-list
      :recipes="recipes"
      :showNoData="recipes.length === 0 && !loading"
    >
      <template v-slot:firstItem
      >
        <div class="col-12 q-col-gutter-y-md"
             v-if="!!$slots.firstItem || loading"
        >
          <slot name="firstItem"></slot>
          <div v-if="loading">
            <q-card flat
                    bordered
            >
              <q-card-section class="q-pa-md">
                <q-icon :name="mdiAlert" size="sm"/>
                Loading...
              </q-card-section>
              <q-inner-loading showing>
                <q-spinner size="40px" color="info" />
              </q-inner-loading>
            </q-card>
          </div>
        </div>
      </template>
      <template v-slot:recipeTopRight="{recipe}"

      >
        <slot name="recipeTopRight"
              v-if="!!$slots.recipeTopRight"
              :recipe="recipe"
        />
      </template>
      <template v-slot:recipeHeadline="{recipe}"

      >
        <slot name="recipeHeadline"
              v-if="!!$slots.recipeHeadline"
              :recipe="recipe"
        />
      </template>
      <template v-slot:lastItem
                v-if="!!$slots.lastItem"
      >
        <slot name="lastItem"></slot>
      </template>
    </c-recipe-list>
    <q-pagination
      class="flex justify-center"
      :model-value="pagination.page + 1"
      @update:model-value="onPageClick($event - 1)"
      color="grey-8"
      :max="pagination.totalPages"
      :max-pages="9"
      :boundary-numbers="true"
      size="md"
      outline
      direction-links
    />
  </div>
</template>

<script>
import CRecipeSearchFilterCard from 'components/CRecipeSearchFilterCard'
import CRecipeList from 'components/CRecipeList'
import { recipeSearchListLogic } from 'src/mixins/recipeSearchListLogic'

export default {
  name: 'CRecipeSearchList',
  components: { CRecipeList, CRecipeSearchFilterCard },
  mixins: [recipeSearchListLogic]
}
</script>

<style scoped>

</style>
