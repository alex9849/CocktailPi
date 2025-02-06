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
import RecipeService from 'src/services/recipe.service'
import { mapGetters } from 'vuex'
import { useRecipeStore } from 'src/stores/recipes'
import { storeToRefs } from 'pinia'

export default {
  name: 'CRecipeSearchList',
  components: { CRecipeList, CRecipeSearchFilterCard },
  props: {
    collectionId: Number,
    onlyOwnRecipes: Boolean,
    categoryId: Number
  },

  setup () {
    const recipeStore = useRecipeStore()
    const { scrollPosition, pagination, recipes } = storeToRefs(recipeStore)
    return {
      recipeStore,
      scrollPosition,
      pagination,
      recipes
    }
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
      color: 'appearance/getNormalColors'
    })
  },

  created () {
    this.mdiAlert = mdiAlert
    this.filter = { ...this.defaultFilter(), ...this.$route.query }
  },

  mounted () {
    this.restoreScrollPosition()
  },

  methods: {
    defaultFilter () {
      return {
        query: '',
        fabricable: '',
        containsIngredients: [],
        orderBy: null
      }
    },

    async onLoad (index, done) {
      if (this.pagination.totalPages < index) {
        done()
        this.$refs.infiniteScroll.stop()
        return
      }

      const response = await RecipeService.getRecipes(
        index - 1,
        this.onlyOwnRecipes ? this.user.id : null,
        this.collectionId,
        this.filter.fabricable,
        this.filter.containsIngredients,
        this.filter.query,
        this.categoryId,
        this.filter.orderBy
      )

      if (index === 1) {
        this.recipeStore.setRecipes(response.content)
      } else {
        this.recipeStore.addRecipes(response.content)
      }
      this.recipeStore.setPagination({ page: response.number, totalPages: response.totalPages })
      done()
    },

    onClickSearch () {
      window.scrollTo({ top: 0 })
      this.updateRoute()
      this.$refs.infiniteScroll.reset()
      this.$refs.infiniteScroll.resume()
      this.$refs.infiniteScroll.trigger()
    },

    saveScrollPosition () {
      this.recipeStore.setScrollPosition(window.scrollY)
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
    categoryId: 'onClickSearch',

    $route (to, from) {
      if (from.name === 'recipedetails') {
        this.saveScrollPosition()
      }

      if (!to.path.includes('/recipes')) {
        this.$refs.infiniteScroll.reset()
        this.$refs.infiniteScroll.stop()
      }
    }
  }
}
</script>

<style scoped>

</style>
