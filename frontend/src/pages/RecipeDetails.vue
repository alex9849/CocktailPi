<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="Public recipes" :to="{name: 'publicrecipes'}"/>
      <q-breadcrumbs-el label="Recipe details"/>
    </q-breadcrumbs>
    <div class="row innerpadding" style="display: inline">
      <div class="col vcenter">
        <h5>{{ recipe.name }}</h5>
      </div>
    </div>
    <div class="q-col-gutter-md">
      <TopButtonArranger>
        <q-btn
          color="grey"
          :to="{name: 'recipeedit', params: {id: $route.params.id}}"
          v-if="isAdminRole || (recipe.owner && user.id === recipe.owner.id && isRecipeCreatorRole)"
        >
          Edit
        </q-btn>
        <q-btn
          v-if="doPumpsHaveAllIngredients(recipe)"
          color="positive"
          @click="showMakeCocktailDialog = true"
        >
          Make cocktail
        </q-btn>
        <q-btn
          v-else
          color="warning"
          @click="showMakeCocktailDialog = true"
        >
          Make cocktail (Half-Automatic)
        </q-btn>
        <q-btn
          color="red"
          @click.native="deleteDialog = true"
          :loading="deleting"
          v-if="isAdminRole || (user.id === recipe.owner.id && isRecipeCreatorRole)"
        >
          Delete
        </q-btn>
      </TopButtonArranger>
      <div class="row q-col-gutter-md">
        <div class="col-12 col-sm-6">
          <div>
            <q-img
              :src="(!!recipe.id)? ($store.getters['auth/getFormattedServerAddress'] + '/api/recipe/' + recipe.id + '/image?timestamp=' + recipe.lastUpdate.getMilliseconds()):null"
              placeholder-src="../assets/cocktail-solid.png"
              :ratio="16/9"
              class="rounded-borders shadow-1"
            />
          </div>
        </div>
        <div class="col-12 col-sm-6">
          <div>
            <ingredient-list
              big
              :row1-color="'#f3f3fa'"
              :row2-color="'#fafafa'"
              class="bg-grey-3 shadow-2"
              v-model="recipe.recipeIngredients"
            />
          </div>
        </div>
      </div>
      <div class="row">
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
      v-model="deleteDialog"
      :loading="deleting"
      ok-color="red"
      ok-button-text="Delete"
      question="Are you sure, that you want to delete this recipe?"
      @clickOk="deleteRecipe"
      @clickAbort="deleteDialog = false"
    />
    <c-make-cocktail-dialog
      v-model="showMakeCocktailDialog"
      :recipe="recipe"
    />
  </q-page>
</template>

<script>
import RecipeService from '../services/recipe.service'
import IngredientList from "../components/IngredientList";
import CQuestion from "../components/CQuestion";
import {mapGetters} from "vuex";
import CMakeCocktailDialog from "../components/CMakeCocktailDialog";
import TopButtonArranger from "components/TopButtonArranger";

export default {
  name: "RecipeDetails",
  components: {TopButtonArranger, CMakeCocktailDialog, CQuestion, IngredientList},
  data() {
    return {
      recipe: {
        recipeIngredients: []
      },
      deleting: false,
      deleteDialog: false,
      showMakeCocktailDialog: false
    }
  },
  async beforeRouteEnter(to, from, next) {
    const recipe = await RecipeService.getRecipe(to.params.id);
    next(vm => {
      vm.recipe = recipe;
    });
  },
  async beforeRouteUpdate(to, from, next) {
    this.recipe = await RecipeService.getRecipe(to.params.id);
    next();
  },
  created() {
    //this.recipe = this.$route.meta.recipe;
  },
  methods: {
    deleteRecipe() {
      this.deleting = true;
      RecipeService.deleteRecipe(this.recipe.id)
        .then(() => {
          this.$router.push({name: 'publicrecipes'});
          this.deleting = false;
          this.$q.notify({
            type: 'positive',
            message: 'Recipe deleted successfully'
          });
        })
    }
  },
  computed: {
    ...mapGetters({
      user: 'auth/getUser',
      isAdminRole: 'auth/isAdmin',
      isRecipeCreatorRole: 'auth/isRecipeCreator',
      isPumpIngredientEditorRole: 'auth/isPumpIngredientEditor',
      doPumpsHaveAllIngredients: 'pumpLayout/doPumpsHaveAllIngredientsForRecipe',
      areEnoughPumpsAvailable: 'pumpLayout/areEnoughPumpsAvailable'
    })
  }
}
</script>

<style scoped>
.innerpadding > * {
  padding: 10px;
}

.vcenter {
  display: inline-block;
  vertical-align: middle;
  float: none;
}
</style>
