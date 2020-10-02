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
      <div class="row">
        <div class="col"/>
        <div class="col q-gutter-sm" style="display: contents; max-width: max-content">
          <q-btn
            color="grey"
            :to="{name: 'recipeedit', params: {id: $route.params.id}}"
            v-if="recipe.owner && user.id === recipe.owner.id"
          >
            Edit
          </q-btn>
          <q-btn
            v-if="!loaded || doPumpsHaveAllIngredients(recipe)"
            color="positive"
            @click="makeCocktailDialog.showDialog = true"
            :loading="!loaded"
          >
            Make cocktail
          </q-btn>
          <q-btn
            v-else-if="areEnoughPumpsAvailable(recipe)"
            color="warning"
            @click="makeCocktailDialog.showDialog = true"
            :disable="!isUserPumpIngredientEditor"
          >
            Change pumplayout & make cocktail
            <q-tooltip
              v-if="!isUserPumpIngredientEditor">
              You are not permitted to change the pumplayout!
            </q-tooltip>
          </q-btn>
          <q-btn
            v-else
            color="positive"
            disable
          >
            Make cocktail
            <q-tooltip>
              Not enough pumps installed!
            </q-tooltip>
          </q-btn>
          <q-btn
            color="red"
            @click.native="deleteDialog = true"
            :loading="deleting"
            v-if="recipe.owner && user.id === recipe.owner.id"
          >
            Delete
          </q-btn>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <q-card bordered class="bg-grey-3 shadow-1">
            <q-card-section>
              <b>Short description:</b> {{ recipe.shortDescription }}
            </q-card-section>
          </q-card>
        </div>
      </div>
      <div class="row q-col-gutter-md">
        <div class="col" style="min-width: max-content">
          <div>
            <q-img
              :src="(!!recipe.id)? ('/api/recipe/' + recipe.id + '/image?nocache=' + new Date().getTime()):null"
              placeholder-src="../assets/cocktail-solid.png"
              :ratio="16/9"
              class="rounded-borders shadow-1"
              style="min-width: 250px"
            />
          </div>
        </div>
        <div style="min-width: 200px" class="col">
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
          <q-card  bordered class="bg-grey-3 shadow-1" style="min-height: 100px">
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
      question="Are you shure, that you want to delete this recipe?"
      @clickOk="deleteRecipe"
      @clickAbort="deleteDialog = false"
    />
    <q-dialog
      v-model="makeCocktailDialog.showDialog"
    >
      <q-card class="text-center" style="width: 500px">
        <q-card-section class="q-gutter-lg">
          <h5>{{ makeCocktailDialogHeadline }}</h5>
          <q-splitter
            horizontal
            :value="10"
          />
          <q-table
            :columns="makeCocktailDialog.columns"
            :data="getPumpLayout"
            :pagination="{rowsPerPage: 0, sortBy: 'id'}"
            hide-bottom
            flat
            :table-style="{margin: '15px'}"
            style="background-color: #f3f3fa"
          >
            <template v-slot:body-cell-currentIngredient="props">
              <q-td
                key="currentIngredient"
                :props="props"
              >
                <c-ingredient-selector
                  :value="props.row.currentIngredient"
                  <!--@input="updatePumpIngredient(props.row, $event)"-->
                  clearable
                  dense
                  :loading="makeCocktailDialog.loadingPumpIds.some(x => x === props.row.id)"
                />
              </q-td>
            </template>
            <template v-slot:body-cell-actions="props">
              <q-td
                key="actions"
                class="q-pa-md q-gutter-x-sm"
                :props="props"
              >
                <q-btn
                  :icon="mdiPlay"
                  color="green"
                  @click="onClickCleanPump(props.row)"
                  dense
                  rounded
                  :loading="isCleaning(props.row.id)"
                >
                  <q-tooltip>
                    Pump up
                  </q-tooltip>
                </q-btn>
              </q-td>
            </template>
          </q-table>
        </q-card-section>
        <q-card-actions
          align="center"
        >
          <q-btn
            color="positive"
            :disable="!loaded || !doPumpsHaveAllIngredients(recipe) || hasCocktailProgress"
          >
            Make cocktail
          </q-btn>
          <q-tooltip
            v-if="loaded && (!doPumpsHaveAllIngredients(recipe) || hasCocktailProgress)"
          >
            {{ hasCocktailProgress? "A cocktail is already being made!" : "Missing ingredients!" }}
          </q-tooltip>
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script>
  import RecipeService from '../services/recipe.service'
  import IngredientList from "../components/IngredientList";
  import CQuestion from "../components/CQuestion";
  import {mapGetters} from "vuex";
  import {mdiPlay} from "@quasar/extras/mdi-v5";
  import PumpService from "../services/pump.service";
  import CIngredientSelector from "../components/CIngredientSelector";

  export default {
    name: "RecipeDetails",
    components: {CIngredientSelector, CQuestion, IngredientList},
    data() {
      return {
        recipe: {},
        deleting: false,
        deleteDialog: false,
        loaded: false,
        makeCocktailDialog: {
          showDialog: false,
          columns: [
            {name: 'id', label: 'Nr', field: 'id', align: 'left'},
            {name: 'currentIngredient', label: 'Current Ingredient', field: 'currentIngredient', align: 'center'},
            {name: 'actions', label: 'Actions', field: '', align: 'center'}
          ],
          loadingPumpIds: []
        }
      }
    },
    created() {
      this.initialize();
      this.mdiPlay = mdiPlay;
    },
    methods: {
      initialize() {
        RecipeService.getRecipe(this.$route.params.id)
          .then(recipe => {
            this.recipe = recipe;
            this.loaded = true;
          });
      },
      updatePumpIngredient(pump, newIngredient) {
        let newPump = Object.assign({}, pump);
        newPump.currentIngredient = newIngredient;
        this.makeCocktailDialog.loadingPumpIds.includes(newPump.id)
        PumpService.updatePump(pump)
          .finally(() => {

          })
      },
      onClickCleanPump(pump) {
        PumpService.cleanPump(pump)
          .catch(error => {
            this.$q.notify({
              type: 'negative',
              message: error.response.data.message
            });
          })
      },
      deleteRecipe() {
        this.deleting = true;
        RecipeService.deleteRecipe(this.recipe)
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
    watch: {
      '$route.params.id': function () {
        this.initialize();
      }
    },
    computed: {
      ...mapGetters({
        user: 'auth/getUser',
        isUserPumpIngredientEditor: 'auth/isPumpIngredientEditor',
        doPumpsHaveAllIngredients: 'pumpLayout/doPumpsHaveAllIngredientsForRecipe',
        areEnoughPumpsAvailable: 'pumpLayout/areEnoughPumpsAvailable',
        hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
        getPumpLayout: 'pumpLayout/getLayout',
        isCleaning: 'pumpLayout/isCleaning'
      }),
      makeCocktailDialogHeadline() {
        if(!this.loaded) {
          return "";
        }
        if(!this.doPumpsHaveAllIngredients(this.recipe)) {
          return "Change pumplayout & make cocktail"
        }
        return "Make cocktail"
      }
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
