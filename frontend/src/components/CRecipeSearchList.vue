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
          :dark="color.cardHeaderDark"
          bordered
        >
          <q-card-section
            class="q-pa-md"
          >
            <q-icon :name="mdiAlert" size="sm"/>
            {{ $t('component.recipe_search_list.loading') }}
          </q-card-section>
          <q-inner-loading
            :dark="color.cardHeaderDark"
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
import { mdiAlert } from '@quasar/extras/mdi-v5'
import JsUtils from 'src/services/JsUtils'
import { mapGetters, mapMutations, mapActions } from 'vuex'

export default {
  name: 'CRecipeSearchList',
  components: { CRecipeList, CRecipeSearchFilterCard },
  props: {
    collectionId: Number,
    onlyOwnRecipes: Boolean,
    categoryId: Number
  },
  data () {
    return {
      loading: false,
      filter: this.defaultFilter()
    }
  },
  computed: {
    ...mapGetters({
      user: 'auth/getUser',
      color: 'appearance/getNormalColors',
      pagination: 'recipes/pagination',
      scrollPosition: 'recipes/scrollPosition',
      cachedRecipes: 'recipes/cachedRecipes',
      getApplicableRoute: 'recipes/getApplicableRoute'
    }),
    recipes () {
      return this.cachedRecipes
    }
  },
  created () {
    this.mdiAlert = mdiAlert
    this.filter = { ...this.defaultFilter(), ...this.$route.query }
  },
  mounted () {
    if (this.getApplicableRoute === this.$route.name) {
      this.restoreScrollPosition()
    } else {
      this.resetCache()
      this.setApplicableRoute(this.$route.name)
    }
    this.$refs.infiniteScroll.setIndex(this.pagination.page)
    this.$refs.infiniteScroll.resume()
  },
  beforeUnmount () {
    this.saveScrollPosition()
  },
  methods: {
    ...mapMutations('recipes', ['setRecipes', 'setScrollPosition', 'setApplicableRoute']),
    ...mapMutations({
      resetCache: 'recipes/reset'
    }),
    ...mapActions('recipes', ['fetchRecipes']),

    defaultFilter () {
      return {
        query: '',
        fabricable: '',
        containsIngredients: [],
        orderBy: null
      }
    },

    onLoad (index, done) {
      this.loading = true
      if (this.pagination.totalPages < index) {
        done()
        this.$refs.infiniteScroll.stop()
        return
      }
      this.fetchRecipes({
        page: index,
        filter: this.filter,
        userId: this.onlyOwnRecipes ? this.user.id : null,
        collectionId: this.collectionId,
        categoryId: this.categoryId,
        orderBy: this.filter.orderBy
      }).then(() => done())
        .finally(() => {
          this.loading = false
        })
    },

    onClickSearch () {
      window.scrollTo({ top: 0 })
      this.resetCache()
      this.updateRoute()
      this.$refs.infiniteScroll.reset()
      this.$refs.infiniteScroll.resume()
    },

    saveScrollPosition () {
      this.setScrollPosition(window.scrollY)
    },

    restoreScrollPosition () {
      this.$nextTick(() => {
        window.scrollTo({ top: this.scrollPosition || 0, behavior: 'instant' })
      })
    },

    updateRoute (filter = this.filter) {
      const query = JsUtils.cleanObject({ ...filter })
      this.$router.replace({ name: this.$route.name, query }).catch(() => {})
    }
  },
  watch: {
    collectionId: 'onClickSearch',
    onlyOwnRecipes: 'onClickSearch',
    categoryId: 'onClickSearch'
  }
}
</script>

<style scoped>

</style>
