<template>
  <div class="q-gutter-md">
    <recipe-search-filter-card
      v-model="filter"
      ref="filter"
      class="bg-grey-1"
      @clickSearch="updateRecipes"
    />
    <recipe-list
      :recipes="recipes"
      no-data-message="No recipes found!"
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
    <q-pagination
      class="flex justify-center"
      :value="pagination.page + 1"
      @input="onPageClick($event - 1)"
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
import RecipeList from "components/RecipeList";
import RecipeService from '../services/recipe.service'
import RecipeSearchFilterCard from "components/RecipeSearchFilterCard";
import {mapGetters} from "vuex";
import JsUtils from "src/services/JsUtils";

export default {
  name: "RecipeSearchList",
  components: {RecipeSearchFilterCard, RecipeList},
  props: {
    collectionId: {
      type: Number,
      required: false
    },
    onlyOwnRecipes: {
      type: Boolean,
      required: false
    },
    categoryId: {
      type: Number,
      required: false
    },
  },
  data() {
    return {
      recipes: [],
      loading: false,
      filter: this.routeOptions().filter,
      pagination: {
        page: this.routeOptions().page,
        totalPages: 1
      }
    }
  },
  created() {
    this.updateRecipes();
  },
  methods: {
    routeOptions() {
      const queryParams = this.$route.query;
      return {
        filter: {
          query: !!queryParams.query ? queryParams.query : '',
          automaticallyFabricable: !!queryParams.automaticallyFabricable ?
            (queryParams.automaticallyFabricable === 'true') : false,
          fabricableWithOwnedIngredients: !!queryParams.fabricableWithOwnedIngredients ?
            (queryParams.fabricableWithOwnedIngredients === 'true') : false,
          containsIngredients: !!queryParams.containsIngredients? queryParams.containsIngredients : [],
          orderBy: queryParams.orderBy
        },
        page: !!queryParams.page? Number(queryParams.page) : 0
      }
    },
    updateRecipes() {
      this.loading = true;
      this.recipes = [];
      this.updateRoute();
      return RecipeService.getRecipes(this.pagination.page,
        this.onlyOwnRecipes ? this.user.id : null,
        null,
        this.collectionId,
        this.onlyOwnRecipes ? null : true,
        this.filter.automaticallyFabricable,
        this.filter.fabricableWithOwnedIngredients,
        this.filter.containsIngredients,
        this.filter.query,
        this.categoryId,
        this.filter.orderBy
      ).then(page => {
        this.recipes = page.content;
        this.pagination.totalPages = page.totalPages;
        this.pagination.page = page.number;
        this.loading = false;
      }, error => {
        this.loading = false;
      });
    },
    updateRoute() {
      let query = {
        page: this.pagination.page
      };
      query = Object.assign(query, this.filter);
      query = JsUtils.cleanObject(query);
      this.$router.replace({name: this.$route.name, query});
    },
    onPageClick(page) {
      this.pagination.page = page;
      this.updateRecipes();
    }
  },
  computed: {
    ...mapGetters({
      user: 'auth/getUser'
    })
  }
}
</script>

<style scoped>

</style>
