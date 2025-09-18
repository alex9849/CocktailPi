<template>
  <q-page class="page-content" padding>
    <div class="row items-center q-mb-md">
      <div class="col-12 col-sm">
        <dic class="text-h5">{{ recipe.name }}</dic>
        <q-breadcrumbs separator="/" class="text-orange q-pb-md" active-color="secondary">
          <q-breadcrumbs-el label="Recipes" :to="lastRecipeListRoute" :disable="!lastRecipeListRoute"/>
          <q-breadcrumbs-el :label="recipe.name" />
        </q-breadcrumbs>
      </div>
      <div class="col-12 col-sm-auto">
        <div class="row q-gutter-sm justify-center justify-sm-end">
          <q-btn
            color="grey"
            :to="{name: 'recipeedit', params: {id: recipe.id}}"
            v-if="isAdminRole || (user?.id === recipe.ownerId && isRecipeCreatorRole)"
            icon="edit"
            flat
          >
            {{ $t('page.recipe_details.edit_btn_label') }}
          </q-btn>
          <q-btn
            color="positive"
            @click="showMakeCocktailDialog = true"
            icon="play_arrow"
            flat
          >
            {{ $t('page.recipe_details.produce_btn_label') }}
          </q-btn>
          <q-btn
            color="red"
            @click="deleteDialog = true"
            :loading="deleting"
            v-if="isAdminRole || (user?.id === recipe.ownerId && isRecipeCreatorRole)"
            icon="delete"
            flat
          >
            {{ $t('page.recipe_details.delete_btn_label') }}
          </q-btn>
        </div>
      </div>
    </div>
    <div class="row q-col-gutter-sm-md">
      <div class="col-12 col-sm-8 col-md-6 col-lg-7">
        <q-card class="shadow-2 bg-card-body text-card-body q-mb-md">
          <q-img
            v-if="recipe.hasImage"
            :src="$store.getters['auth/getFormattedServerAddress'] + '/api/recipe/' + recipe.id + '/image?timestamp=' + recipe.lastUpdate.getTime() + '&width=1500'"
            :ratio="16/9"
            class="rounded-borders"
            placeholder-src="~assets/cocktail-solid.png"
          />
          <q-img
            v-else
            src="~assets/cocktail-solid.png"
            :ratio="16/9"
            class="rounded-borders"
          />
        </q-card>
        <q-card class="md-hide lg-hide xl-hide shadow-2 bg-card-body text-card-body q-mb-md">
          <q-card-section>
            <div class="text-h6 q-mb-sm">
              <q-icon name="restaurant_menu" class="q-mr-xs" />
              {{ $t('page.recipe_details.ingredients_headline') }}
            </div>
            <ingredient-list
              hide-header
              big
              alternateRowColors
              :background-color="color.cardBody"
              v-model:model-value="recipe.productionSteps"
            />
          </q-card-section>
        </q-card>
        <q-card
          v-if="recipe.description"
          class="shadow-2 bg-card-body text-card-body q-mb-md"
        >
          <q-card-section>
            <div class="text-h6 q-mb-xs">
              <q-icon name="description" class="q-mr-xs" /> {{ $t('page.recipe_details.description_headline') }}
            </div>
            <div style="white-space: pre-line">{{ recipe.description }}</div>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-12 col-sm-4 col-md-6 col-lg-5">
        <q-card class="shadow-2 bg-card-body text-card-body q-mb-md sm-hide xs-hide">
          <q-card-section>
            <div class="text-h6 q-mb-sm">
              <q-icon name="restaurant_menu" class="q-mr-xs" />
              {{ $t('page.recipe_details.ingredients_headline') }}
            </div>
            <ingredient-list
              hide-header
              big
              alternateRowColors
              :background-color="color.cardBody"
              v-model:model-value="recipe.productionSteps"
            />
          </q-card-section>
        </q-card>
        <div class="row q-col-gutter-md q-mb-md items-stretch">
          <div class="col-12 col-md-6">
            <property-card
              class="shadow-1 bg-card-body text-card-body full-height"
              icon="wine_bar"
              :text-color="this.recipe.defaultGlass ? 'teal' : 'grey-6'"
              :value="printGlass"
              headline="Glas"
            />
          </div>
          <div class="col-12 col-md-6">
            <property-card
              class="shadow-1 bg-card-body text-card-body full-height"
              icon="local_bar"
              :text-color="this.recipe.minAlcoholContent ? 'teal' : 'grey-6'"
              :value="printAlcoholContent"
              headline="Alkoholgehalt"
            />
          </div>
        </div>
        <div
          class="row q-col-gutter-md q-mb-md items-stretch"
          v-if="recipe.categories.length !== 0"
        >
          <div class="col-12">
            <q-card class="shadow-1 bg-card-body text-card-body">
              <q-card-section>
                <div class="flex items-center text-caption text-grey-7 q-mb-xs">
                  <q-icon name="category" class="q-mr-sm" color="secondary" />
                  {{ $t('page.recipe_details.category_headline') }}
                </div>
                <div class="q-gutter-sm">
                  <q-chip
                    v-for="cat in recipe.categories"
                    :key="cat.id"
                    color="secondary"
                    text-color="white"
                    size="md"
                    class="q-mb-xs"
                  >
                    <div class="flex items-center">
                      <q-icon name="label" class="q-mr-xs" size="16px" />
                      <span>{{ cat.name }}</span>
                    </div>
                  </q-chip>
                </div>
              </q-card-section>
            </q-card>
          </div>
        </div>
        <q-card class="shadow-2 bg-card-body text-card-body q-mb-md">
          <q-card-section>
            <div class="row q-col-gutter-md justify-between">
              <div class="col-12 col-md-auto">
                <div class="text-caption text-grey-7 items-center">
                  <q-icon name="update" class="q-mr-xs" />
                  {{ $t('page.recipe_details.property_last_change') }}
                  {{ new Date(recipe.lastUpdate).toLocaleDateString() }}
                </div>
              </div>
              <div class="col-12 col-md-auto">
                <div class="text-caption text-grey-7 dotted-overflow-1">
                  <q-icon name="person" class="q-mr-xs" />
                  {{ $t('page.recipe_details.property_recipe_owner') }}
                  {{ recipe.ownerName }}
                </div>
              </div>
              <div class="col-12 col-md-auto">
                <div class="text-caption text-grey-7">
                  <q-icon name="info" class="q-mr-xs" />
                  {{ $t('page.recipe_details.property_id') }}
                  {{ recipe.id }}
                </div>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>
    <c-question
      v-model:show="deleteDialog"
      :loading="deleting"
      ok-color="red"
      :ok-button-text="$t('page.recipe_details.delete_dialog.yes_btn_label')"
      :abort-button-text="$t('page.recipe_details.delete_dialog.abort_btn_label')"
      :question="$t('page.recipe_details.delete_dialog.headline')"
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
import category from 'src/store/modules/category'
import PropertyCard from 'components/PropertyCard.vue'

export default {
  name: 'RecipeDetails',
  components: { PropertyCard, CMakeCocktailDialog, CQuestion, IngredientList },
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

    let recipe
    try {
      recipe = await RecipeService.getRecipe(to.params.id)
    } catch (e) {
      if (e.response.status === 404) {
        next({ name: '404Page' })
        return
      }
    }
    next(vm => {
      vm.recipe = recipe
      vm.loaded = true
    })
  },
  async beforeRouteUpdate (to, from, next) {
    if (this.recipe.id !== Number.parseInt(to.params.id)) {
      try {
        this.recipe = await RecipeService.getRecipe(to.params.id)
      } catch (e) {
        if (e.response.status === 404) {
          next({ name: '404Page' })
          return
        }
      }
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
            message: this.$t('page.recipe_details.notifications.recipe_deleted')
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
      lastRecipeListRoute: 'common/getLastRecipeListRoute',
      color: 'appearance/getNormalColors'
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
    printAlcoholContent () {
      if (this.recipe.maxAlcoholContent === 0) {
        return this.$t('page.recipe_details.no_alc_label')
      }
      if (this.recipe.minAlcoholContent === this.recipe.maxAlcoholContent) {
        return this.$t('page.recipe_details.alc_label', { val: this.recipe.maxAlcoholContent })
      } else {
        return this.$t('page.recipe_details.alc_label_range', { min_val: this.recipe.minAlcoholContent, max_val: this.recipe.maxAlcoholContent })
      }
    },
    printGlass () {
      if (!this.recipe.defaultGlass) {
        return this.$t('page.recipe_details.no_glass_label')
      }
      return this.$t('page.recipe_details.glass_label', { ml: this.recipe.defaultGlass.size, glass: this.recipe.defaultGlass.name })
    }
  }
}
</script>

<style scoped>
</style>
