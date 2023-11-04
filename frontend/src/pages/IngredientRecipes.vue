<template>
  <q-page
    padding
    class="page-content"
  >
    <h5>{{ $t('page.ingredient_recipes.headline') }}</h5>
    <div class="row q-col-gutter-md">
      <div
        class="col-12 col-sm-6 col-lg-4 d-flex items-stretch"
        :key="iRecipe.ingredientId"
        v-for="iRecipe in ingredientRecipes"
      >
        <q-card
          flat
          bordered
          class="bg-grey-1 clickable"
          :class="{'disabled': orderDialog.loadingId !== null}"
          @click="openOrderDialog(iRecipe)"
        >
          <q-inner-loading
            :showing="orderDialog.loadingId === iRecipe.id"
          />
          <q-card-section horizontal>
            <q-card-section class="col-4 q-pa-sm">
              <q-img :ratio="1"
                     class="rounded-borders"
                     placeholder-src="~assets/cocktail-solid.png"
                     src="~assets/cocktail-solid.png"
              />
            </q-card-section>
            <q-card-section class="col-8 q-pa-sm flex column">
              <div class="text-h5" style="margin-bottom: 0">{{ iRecipe.name }}</div>
              <div class="text-caption text-grey">
                <p>{{ $t('page.ingredient_recipes.recipe.ml_left', {nr: iRecipe.ingredient.alcoholContent})}}</p>
                <p>{{ $t('page.ingredient_recipes.recipe.alc_content', {nr: iRecipe.mlLeft})}}</p>
              </div>
            </q-card-section>
          </q-card-section>
        </q-card>
      </div>
      <div
        v-if="ingredientRecipes.length === 0"
        class="col-12"
      >
        <q-card
          flat bordered
          class="bg-card-secondary"
        >
          <div class="row q-pa-md items-center q-gutter-sm">
            <q-icon size="sm" :name="mdiAlert" />
            <p>{{ $t('page.ingredient_recipes.no_ingredients_available')}}</p>
          </div>
        </q-card>
      </div>
    </div>
    <c-make-cocktail-dialog
      :recipe="orderDialog.recipe"
      v-if="!!orderDialog.recipe"
      v-model:show="orderDialog.show"
      @postOrder="onPostOrder"
    />
  </q-page>

</template>

<script>

import RecipeService from 'src/services/recipe.service'
import { mdiAlert } from '@quasar/extras/mdi-v5'
import CMakeCocktailDialog from 'components/CMakeCocktailDialog.vue'
import { mapGetters } from 'vuex'

export default {
  name: 'IngredientRecipes',
  components: { CMakeCocktailDialog },
  async beforeRouteEnter (to, from, next) {
    const ingredientRecipes = await RecipeService.getIngredientRecipes()
    next(vm => {
      vm.ingredientRecipes = ingredientRecipes
    })
  },
  data: () => {
    return {
      ingredientRecipes: [],
      orderDialog: {
        loadingId: null,
        show: false,
        recipe: null
      }
    }
  },
  watch: {
    'orderDialog.show' (newVal) {
      if (newVal) {
        return
      }
      this.loadIngredientRecipes()
    },
    cocktailProgress (newVal) {
      if (newVal) {
        return
      }
      this.loadIngredientRecipes()
    }
  },
  created () {
    this.mdiAlert = mdiAlert
  },
  methods: {
    loadIngredientRecipes () {
      RecipeService.getIngredientRecipes()
        .then(x => {
          this.ingredientRecipes = x
        })
    },
    onPostOrder () {
      this.orderDialog.show = false
      this.orderDialog.recipe = null
      this.orderDialog.loadingId = null
      this.$store.commit('cocktailProgress/setShowProgressDialog', true)
    },
    openOrderDialog (recipe) {
      if (this.orderDialog.loadingId !== null) {
        return
      }
      this.orderDialog.loadingId = recipe.id
      RecipeService.getRecipe(recipe.id, recipe.type === 'ingredientrecipe')
        .then(recipe => {
          this.orderDialog.recipe = Object.assign({}, recipe)
          this.orderDialog.show = true
        }).finally(() => {
          this.orderDialog.loadingId = null
        })
    }
  },
  computed: {
    ...mapGetters({
      cocktailProgress: 'cocktailProgress/hasCocktailProgress'
    })
  }
}

</script>

<style scoped>

</style>
