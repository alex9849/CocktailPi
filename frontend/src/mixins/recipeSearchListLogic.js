import RecipeService from '../services/recipe.service'
import { mapGetters } from 'vuex'
import { mdiAlert } from '@quasar/extras/mdi-v5'
import JsUtils from 'src/services/JsUtils'

export const recipeSearchListLogic = {
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
            this.collectionId,
            this.filter.fabricable,
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
    updateRoute (filter = this.filter) {
      let query = {
        page: this.pagination?.page
      }
      if (query.page === 1) {
        delete query.page
      }
      query = Object.assign(query, filter)
      query = JsUtils.cleanObject(query)
      this.$router.replace({ name: this.$route.name, query }).catch(() => {})
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
    })
  }
}
