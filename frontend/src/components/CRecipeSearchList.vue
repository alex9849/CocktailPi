<template>
  <div class="q-gutter-md">
    <c-recipe-search-filter-card
      v-model:filter="filter"
      ref="filter"
      @clickSearch="onClickSearch"
    />
    <q-infinite-scroll
      @load="onLoad"
      :offset="300"
      ref="infiniteScroll"
    >
      <template v-slot:loading>
        <q-card
          class="q-my-md bg-card-header text-card-header"
          flat
          bordered
        >
          <q-card-section
            class="q-pa-md"
          >
            <q-icon :name="mdiAlert" size="sm"/>
            {{ $t('component.recipe_search_list.loading') }}
          </q-card-section>
          <q-inner-loading
            :dark="color.cardBodyDark"
            showing
          >
            <q-spinner
              size="40px"
              color="info"
            />
          </q-inner-loading>
        </q-card>
      </template>
      <template v-slot:default>
        <c-recipe-list
          :recipes="recipes"
          :showNoData="recipes.length === 0 && !loading"
        >
          <template v-slot:firstItem>
            <div class="col-12 q-col-gutter-y-md"
                 v-if="!!$slots.firstItem"
            >
              <slot name="firstItem"></slot>
            </div>
          </template>
          <template v-slot:recipeTopRight="{recipe}">
            <slot name="recipeTopRight"
                  v-if="!!$slots.recipeTopRight"
                  :recipe="recipe"
            />
          </template>
          <template v-slot:recipeHeadline="{recipe}">
            <slot name="recipeHeadline"
                  v-if="!!$slots.recipeHeadline"
                  :recipe="recipe"
            />
          </template>
          <template
            v-slot:lastItem
            v-if="!!$slots.lastItem"
          >
            <slot name="lastItem"></slot>
          </template>
        </c-recipe-list>
      </template>
    </q-infinite-scroll>
  </div>
</template>

<script>
import CRecipeSearchFilterCard from 'components/CRecipeSearchFilterCard'
import CRecipeList from 'components/CRecipeList'
import RecipeService from 'src/services/recipe.service'
import { mdiAlert } from '@quasar/extras/mdi-v5'
import JsUtils from 'src/services/JsUtils'
import { mapGetters } from 'vuex'

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
        page: 0,
        totalPages: 1
      }
    }
  },
  created () {
    this.mdiAlert = mdiAlert
  },
  methods: {
    onLoad (index, done) {
      if (this.pagination.totalPages < index) {
        done()
        this.$refs.infiniteScroll.stop()
        return
      }
      this.updateRecipes(false, index - 1)
        .then(x => {
          done()
        })
    },
    routeOptions () {
      const queryParams = this.$route.query
      let containsIngredients = queryParams.containsIngredients ? queryParams.containsIngredients : []
      if (!Array.isArray(containsIngredients)) {
        containsIngredients = [containsIngredients]
      }
      const filter = {
        query: queryParams.query ? queryParams.query : '',
        fabricable: queryParams.fabricable ? queryParams.fabricable : '',
        containsIngredients,
        orderBy: queryParams.orderBy
      }
      const filterSet = filter.query || filter.fabricable || filter.containsIngredients.length !== 0 || filter.orderBy
      const defaultFilter = this.$store.getters['common/getDefaultFilter']
      if (defaultFilter.enable && !filterSet) {
        filter.fabricable = defaultFilter.filter.fabricable
        this.updateRoute(filter)
      }
      return {
        filter
      }
    },
    updateRoute (filter = this.filter) {
      let query = Object.assign({}, filter)
      query = JsUtils.cleanObject(query)
      this.$router.replace({ name: this.$route.name, query }).catch(() => {
      })
    },
    updateRecipes (withLoadingAnimation = true, page) {
      this.loading = true
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          RecipeService.getRecipes(page,
            this.onlyOwnRecipes ? this.user.id : null,
            this.collectionId,
            this.filter.fabricable,
            this.filter.containsIngredients,
            this.filter.query,
            this.categoryId,
            this.filter.orderBy
          ).then(page => {
            this.recipes.push(...page.content)
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
    onClickSearch () {
      window.scrollTo({ top: 0 })
      this.updateRoute()
      this.recipes = []
      this.$refs.infiniteScroll.reset()
      this.$refs.infiniteScroll.resume()
      this.$refs.infiniteScroll.trigger()
    }
  },
  watch: {
    collectionId () {
      this.onClickSearch()
    },
    onlyOwnRecipes () {
      this.onClickSearch()
    },
    categoryId () {
      this.onClickSearch()
    }
  },
  computed: {
    ...mapGetters({
      user: 'auth/getUser',
      color: 'appearance/getNormalColors'
    })
  }
}
</script>

<style scoped>

</style>
