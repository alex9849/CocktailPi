<template>
  <q-page class="page-content" padding>
    <div class="row q-col-gutter-xl">
      <!-- Linke Spalte: Bild, Zutaten, Glas -->
      <div class="col-12 col-md-5">
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
        <q-card class="shadow-2 bg-card-body text-card-body q-mb-md">
          <q-card-section>
            <div class="text-h6 q-mb-sm">
              <q-icon name="restaurant_menu" class="q-mr-xs" /> Zutaten
            </div>
            <ingredient-list
              big
              alternateRowColors
              :background-color="color.cardBody"
              v-model:model-value="recipe.productionSteps"
            />
          </q-card-section>
        </q-card>
        <q-card class="shadow-2 bg-card-body text-card-body">
          <q-card-section>
            <div class="text-h6 q-mb-xs">
              <q-icon name="wine_bar" class="q-mr-xs" /> Glas
            </div>
            <div>
              {{ printGlass }}
            </div>
          </q-card-section>
        </q-card>
      </div>
      <!-- Rechte Spalte: Name, Info-Card, Beschreibung, Aktionen -->
      <div class="col-12 col-md-7">
        <div class="row items-center q-mb-md">
          <div class="col">
            <div class="text-h4">{{ recipe.name }}</div>
            <div class="text-subtitle2 text-grey">
              {{ $t('page.recipe_details.by') }} {{ recipe.owner?.username || 'Unbekannt' }}
            </div>
          </div>
          <div class="col-auto">
            <TopButtonArranger>
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
            </TopButtonArranger>
          </div>
        </div>
        <q-card class="shadow-2 bg-card-body text-card-body q-mb-md">
          <q-card-section>
            <div class="text-h6 q-mb-xs">
              <q-icon name="description" class="q-mr-xs" /> {{ $t('page.recipe_details.description_headline') }}
            </div>
            <div style="white-space: pre-line">{{ recipe.description }}</div>
          </q-card-section>
        </q-card>

        <div class="row q-col-gutter-md q-mb-md">
          <div class="col-12 col-sm-6 col-md-4">
            <q-card class="shadow-1 bg-card-body text-card-body">
              <q-card-section>
                <div class="text-caption text-grey-7 q-mb-xs">
                  <q-icon name="bolt" class="q-mr-xs" /> Boostbar
                </div>
                <div class="flex items-center" style="min-height: 32px;">
                  <q-icon name="bolt" color="orange" size="32px" v-if="recipe.boostable" />
                  <span class="text-h6 text-orange" v-if="recipe.boostable">Boostbar verfügbar</span>
                  <span class="text-h6 text-grey-6" v-else>Keine Boostbar</span>
                </div>
              </q-card-section>
            </q-card>
          </div>
          <div class="col-12 col-sm-6 col-md-4">
            <q-card class="shadow-1 bg-card-body text-card-body">
              <q-card-section>
                <div class="text-caption text-grey-7 q-mb-xs">
                  <q-icon name="local_bar" class="q-mr-xs" /> Alkoholgehalt
                </div>
                <div class="flex items-center" style="min-height: 32px;">
                  <q-icon name="local_bar" color="teal" size="32px" v-if="recipe.minAlcoholContent" />
                  <span class="text-h6 text-teal" v-if="recipe.minAlcoholContent">{{ recipe.minAlcoholContent }}% Vol.</span>
                  <span class="text-h6 text-grey-6" v-else>Kein Alkohol</span>
                </div>
              </q-card-section>
            </q-card>
          </div>
          <div class="col-12 col-sm-12 col-md-4">
            <q-card class="shadow-1 bg-card-body text-card-body">
              <q-card-section>
                <div class="text-caption text-grey-7 q-mb-xs">
                  <q-icon name="category" class="q-mr-xs" /> Kategorien
                </div>
                <div>
                  <q-badge v-for="cat in recipe.categories" :key="cat.id" color="secondary" class="q-mr-xs q-mb-xs">
                    {{ cat.name }}
                  </q-badge>
                </div>
              </q-card-section>
            </q-card>
          </div>
        </div>

        <q-card class="shadow-2 bg-card-body text-card-body q-mb-md">
          <q-card-section>
            <div class="row q-col-gutter-md">
              <div class="col-auto">
                <div class="text-caption text-grey-7">
                  <q-icon name="update" class="q-mr-xs" /> {{ $t('page.recipe_details.last_update') || 'Letzte Änderung' }}:
                  {{ new Date(recipe.lastUpdate).toLocaleDateString() }}
                </div>
              </div>
              <div class="col-auto">
                <div class="text-caption text-grey-7">
                  <q-icon name="person" class="q-mr-xs" /> {{ $t('page.recipe_details.by') }} {{ recipe.owner?.username || 'Unbekannt' }}
                </div>
              </div>
              <div class="col-auto">
                <div class="text-caption text-grey-7">
                  <q-icon name="info" class="q-mr-xs" /> ID: {{ recipe.id }}
                </div>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>
    <!-- Dialoge -->
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
