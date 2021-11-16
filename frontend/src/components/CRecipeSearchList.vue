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
      :no-data-message="noDataMessage"
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
import RecipeService from '../services/recipe.service'
import CRecipeSearchFilterCard from 'components/CRecipeSearchFilterCard'
import { mapGetters } from 'vuex'
import { mdiAlert } from '@quasar/extras/mdi-v5'
import JsUtils from 'src/services/JsUtils'
import CRecipeList from 'components/CRecipeList'

export default {
  name: 'CRecipeSearchList',
  components: { CRecipeList, CRecipeSearchFilterCard },
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
    }
  },
  data () {
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
  created () {
    this.updateRecipes()
    this.mdiAlert = mdiAlert
  },
  methods: {
    routeOptions () {
      const queryParams = this.$route.query
      let containsIngredients = queryParams.containsIngredients ? queryParams.containsIngredients : []
      if (!Array.isArray(containsIngredients)) {
        containsIngredients = [containsIngredients]
      }

      return {
        filter: {
          query: queryParams.query ? queryParams.query : '',
          automaticallyFabricable: queryParams.automaticallyFabricable
            ? (queryParams.automaticallyFabricable === 'true')
            : false,
          fabricableWithOwnedIngredients: queryParams.fabricableWithOwnedIngredients
            ? (queryParams.fabricableWithOwnedIngredients === 'true')
            : false,
          containsIngredients: containsIngredients,
          orderBy: queryParams.orderBy
        },
        page: queryParams.page ? (Number(queryParams.page) - 1) : 0
      }
    },
    updateRecipes (withLoadingAnimation = true) {
      this.loading = withLoadingAnimation
      if (withLoadingAnimation) {
        this.recipes = []
      }
      this.updateRoute()
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          RecipeService.getRecipes(this.pagination.page,
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
            this.recipes = page.content
            this.pagination.totalPages = page.totalPages
            this.pagination.page = page.number
            this.loading = false
            resolve(page.content)
          }, error => {
            this.loading = false
            reject(error)
          })
        }, withLoadingAnimation ? 500 : 0)
      })
    },
    updateRoute () {
      let query = {
        page: this.pagination.page + 1
      }
      if (query.page === 1) {
        delete query.page
      }
      query = Object.assign(query, this.filter)
      query = JsUtils.cleanObject(query)
      this.$router.replace({ name: this.$route.name, query: query }).catch(() => {})
    },
    onPageClick (page) {
      if (this.pagination.page !== page) {
        this.pagination.page = page
        this.updateRecipes()
      }
    }
  },
  watch: {
    collectionId () {
      this.updateRecipes()
    },
    onlyOwnRecipes () {
      this.updateRecipes()
    },
    categoryId () {
      this.updateRecipes()
    }
  },
  computed: {
    ...mapGetters({
      user: 'auth/getUser'
    }),
    noDataMessage () {
      if (this.loading) {
        return ''
      }
      return 'No recipes found!'
    }
  }
}
</script>

<style scoped>

</style>
