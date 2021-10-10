<template>
  <div class="q-gutter-md">
    <recipe-search-filter-card
      v-model="filter"
      @clickSearch="updateRecipes"
    />
    <recipe-list
      :recipes="[]"
    >
      <template slot="firstItem"
                v-if="!!$slots.firstItem"
      >
        <slot name="firstItem"></slot>
      </template>
      <template slot="recipeTopRight"
                slot-scope="{recipe}"
      >
        <slot name="recipeTopRight"
              v-if="!!$scopedSlots.recipeTopRight"
              :recipe="recipe"
        />
      </template>
      <template slot="recipeHeadline"
                slot-scope="{recipe}"
      >
        <slot name="recipeHeadline"
              v-if="!!$scopedSlots.recipeHeadline"
              :recipe="recipe"
        />
      </template>
      <template slot="lastItem"
                v-if="!!$slots.lastItem"
      >
        <slot name="lastItem"></slot>
      </template>
    </recipe-list>
  </div>
</template>

<script>
import RecipeList from "components/RecipeList";
import RecipeSearchFilterCard from "components/RecipeSearchFilterCard";

export default {
  name: "RecipeSearchList",
  components: {RecipeSearchFilterCard, RecipeList},
  props: {

  },
  data() {
    return {
      filter: {

      }
    }
  },
  methods: {
    updateRecipes() {

    }
  }
}
</script>

<style scoped>

</style>
