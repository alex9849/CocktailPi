<template>
  <q-page class="page-content" padding>
    <div class="row">
      <div class="col">
        <h5>{{ recipe.name }}</h5>
      </div>
    </div>
    <div class="q-col-gutter-md">
      <q-breadcrumbs separator="/" class="text-orange" active-color="secondary">
        <q-breadcrumbs-el label="Recipes" :to="lastRecipeListRoute" :disable="!lastRecipeListRoute"/>
        <q-breadcrumbs-el :label="recipe.name" />
      </q-breadcrumbs>
      <TopButtonArranger>
        <q-btn
          color="grey"
          :to="{name: 'recipeedit', params: {id: recipe.id}}"
          v-if="isAdminRole || (user?.id === recipe.ownerId && isRecipeCreatorRole)"
        >
          Edit
        </q-btn>
        <q-btn
          color="positive"
          @click="showMakeCocktailDialog = true"
        >
          Make cocktail
        </q-btn>
        <q-btn
          color="red"
          @click="deleteDialog = true"
          :loading="deleting"
          v-if="isAdminRole || (user?.id === recipe.ownerId && isRecipeCreatorRole)"
        >
          Delete
        </q-btn>
      </TopButtonArranger>
      <div class="row q-col-gutter-md">
        <div class="col-12 col-sm-6">
          <div>
            <q-img
              v-if="recipe.hasImage"
              :ratio="16/9"
              :src="$store.getters['auth/getFormattedServerAddress'] + '/api/recipe/' + recipe.id + '/image?timestamp=' + recipe.lastUpdate.getMilliseconds()"
              class="rounded-borders shadow-1"
              placeholder-src="~assets/cocktail-solid.png"
            />
            <q-img
              v-else
              placeholder-src="~assets/cocktail-solid.png"
              src="~assets/cocktail-solid.png"
              :ratio="16/9"
              class="rounded-borders shadow-1"
            />
          </div>
        </div>
        <div class="col-12 col-sm-6">
          <div>
            <ingredient-list
              big
              alternateRowColors
              class="bg-grey-3 shadow-2"
              v-model:model-value="recipe.productionSteps"
            />
          </div>
        </div>
      </div>
      <div>
        <q-card bordered class="bg-grey-3 shadow-1">
          <q-card-section>
            <div style="display: block ruby">
              <b>Categories: </b>
              <div
                v-if="recipe.categories.length !== 0"
              >
                <q-badge
                  class="q-mx-xs"
                  v-for="category in recipe.categories"
                  :key="category.id"
                  :label="category.name"
                />
              </div>
              <div
                v-else
              >
                None
              </div>
            </div>
            <p><b>Default glass:</b> {{ printGlass }}</p>
          </q-card-section>
        </q-card>
      </div>
      <div
        v-if="recipe.description"
        class="row"
      >
        <div class="col">
          <q-card bordered class="bg-grey-3 shadow-1" style="min-height: 100px">
            <q-card-section>
              <b>Description:</b>
              <div style="min-width: 200px; white-space: pre-line" class="col">
                {{ recipe.description }}
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </div>
    <c-question
      v-model:show="deleteDialog"
      :loading="deleting"
      ok-color="red"
      ok-button-text="Delete"
      question="Are you sure, that you want to delete this recipe?"
      @clickOk="deleteRecipe"
      @clickAbort="deleteDialog = false"
    />
    <c-make-cocktail-dialog
      v-if="loaded && showMakeCocktailDialog"
      show
      v-model:show="showMakeCocktailDialog"
      :recipe="recipe"
      @postOrder="onPostOrder"
    />
  </q-page>
</template>

<script>
import RecipeService from '../services/recipe.service'
import IngredientList from '../components/IngredientList'
import CQuestion from '../components/CQuestion'
import { mapGetters } from 'vuex'
import CMakeCocktailDialog from '../components/CMakeCocktailDialog'
import TopButtonArranger from 'components/TopButtonArranger'
import category from 'src/store/modules/category'

export default {
  name: 'RecipeDetails',
  components: { TopButtonArranger, CMakeCocktailDialog, CQuestion, IngredientList },
  data () {
    return {
      recipe: {
        owner: {},
        productionSteps: [],
        categories: []
      },
      loaded: false,
      deleting: false,
      deleteDialog: false
    }
  },
  async beforeRouteEnter (to, from, next) {
    if ((from?.name === 'recipedetails' ||
        from?.name === 'recipeorder') && from?.params?.id === to?.params?.id) {
      next()
      return
    }
    const recipe = await RecipeService.getRecipe(to.params.id)
    next(vm => {
      vm.recipe = recipe
      vm.loaded = true
    })
  },
  async beforeRouteUpdate (to, from, next) {
    if (this.recipe.id !== Number.parseInt(to.params.id)) {
      this.recipe = await RecipeService.getRecipe(to.params.id)
    }
    next()
  },
  created () {
    // this.recipe = this.$route.meta.recipe;
  },
  methods: {
    onPostOrder () {
      this.$router.push({
        name: 'recipedetails',
        params: { id: this.$route.params.id }
      })
        .then(() => {
          this.$store.commit('cocktailProgress/setShowProgressDialog', true)
        })
    },
    deleteRecipe () {
      this.deleting = true
      RecipeService.deleteRecipe(this.recipe.id)
        .then(() => {
          this.$router.push({ name: 'publicrecipes' })
          this.deleting = false
          this.$q.notify({
            type: 'positive',
            message: 'Recipe deleted successfully'
          })
        })
    }
  },
  computed: {
    category () {
      return category
    },
    ...mapGetters({
      user: 'auth/getUser',
      isAdminRole: 'auth/isAdmin',
      isRecipeCreatorRole: 'auth/isRecipeCreator',
      isPumpIngredientEditorRole: 'auth/isPumpIngredientEditor',
      lastRecipeListRoute: 'common/getLastRecipeListRoute'
    }),
    showMakeCocktailDialog: {
      get () {
        return this.$route.name === 'recipeorder' && this.loaded
      },
      set (value) {
        const params = {
          id: this.$route.params.id
        }
        if (value) {
          this.$router.replace({
            name: 'recipeorder', params
          })
        } else {
          this.$router.replace({
            name: 'recipedetails', params
          })
        }
      }
    },
    printGlass () {
      if (!this.recipe.defaultGlass) {
        return 'None'
      }
      return this.recipe.defaultGlass.name + ' (' + this.recipe.defaultGlass.size + ' ml)'
    }
  }
}
</script>

<style scoped>
</style>
