<template>
  <div>
    <div class="row q-col-gutter-md">
      <slot name="firstItem"/>
      <div
        v-for="recipe of recipes"
        :key="recipe.id"
        class="col-12"
      >
        <router-link
          :to="{name: 'recipedetails', params: {id: recipe.id}}"
        >
          <c-recipe-card
            :recipe="recipe"
            show-ingredients
            class="bg-grey-2 q-card--bordered q-card--flat no-shadow"
          >
            <template v-slot:headline>
              <slot
                v-if="!!$scopedSlots.recipeHeadline"
                :recipe="recipe"
                name="recipeHeadline"
              />
            </template>
            <template slot="topRight">
              <slot
                v-if="!!$scopedSlots.recipeTopRight"
                :recipe="recipe"
                name="recipeTopRight"
              />
            </template>
          </c-recipe-card>
        </router-link>
      </div>
      <slot name="lastItem"/>
    </div>
  </div>
</template>

<script>
import CRecipeCard from "components/CRecipeCard";

export default {
  name: "RecipeList",
  components: {CRecipeCard},
  props: {
    recipes: {
      type: Array,
      required: true
    }
  }
}
</script>

<style scoped>

</style>
