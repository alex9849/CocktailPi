<template>
  <q-dialog
    :value="value"
    ref="mcDialog"
    @input="$emit('input', $event)"
  >
    <q-card
      class="text-center"
      style="width: 500px"
    >
      <q-card-section class="q-gutter-md">
        <p class="text-h5">{{ makeCocktailDialogHeadline }}</p>
        <q-splitter
          horizontal
          :value="10"
        />
        <q-input
          v-model.number="amountToProduce"
          @input="$v.amountToProduce.$touch()"
          label="Amount to produce"
          outlined
          input-class="text-center text-weight-medium"
          rounded
          type="number"
          :rules="[
            val => $v.amountToProduce.required || 'Required',
            val => $v.amountToProduce.minValue || 'Min 50ml',
            val => $v.amountToProduce.maxValue || 'Max 1000ml'
          ]"
        >
          <template slot="append">
            ml
          </template>
        </q-input>
        <q-table
          v-if="isUserPumpIngredientEditor"
          :columns="columns"
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
                @input="updatePumpIngredient(props.row, $event)"
                clearable
                dense
                :bg-color="(!!props.row.currentIngredient && isIngredientNeeded(props.row.currentIngredient.id))? 'green-3':undefined"
                :no-input-options="missingIngredients"
                :loading="loadingPumpIds.includes(props.row.id, 0)"
              >
                <template
                  slot="afterIngredientName"
                  slot-scope="{scope}"
                >
                  <q-item-label
                    v-if="!!missingIngredients.some(x => x.id === scope.opt.id)"
                    caption
                    class="text-green"
                  >
                    Required ingredient
                  </q-item-label>
                </template>
              </c-ingredient-selector>
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
          @click="onMakeCocktail()"
          :disable="!doPumpsHaveAllIngredients(recipe) || hasCocktailProgress || $v.amountToProduce.$invalid"
        >
          Make cocktail
        </q-btn>
        <q-tooltip
          v-if="!doPumpsHaveAllIngredients(recipe) || hasCocktailProgress"
        >
          {{ hasCocktailProgress? "A cocktail is already being made!" : "Missing ingredients!" }}
        </q-tooltip>
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script>
  import PumpService from "../services/pump.service";
  import CocktailService from "../services/cocktail.service"
  import {mapGetters} from "vuex";
  import {mdiPlay} from "@quasar/extras/mdi-v5";
  import CIngredientSelector from "../components/CIngredientSelector";
  import {maxValue, minValue, required} from "vuelidate/lib/validators";

  export default {
    name: "CMakeCocktailDialog",
    components: {CIngredientSelector},
    props: {
      value: {
        type: Boolean,
        required: false
      },
      recipe: {
        type: Object,
        required: true
      }
    },
    data() {
      return {
        amountToProduce: 250,
        columns: [
          {name: 'id', label: 'Nr', field: 'id', align: 'left'},
          {name: 'currentIngredient', label: 'Current Ingredient', field: 'currentIngredient', align: 'center'},
          {name: 'actions', label: 'Actions', field: '', align: 'center'}
        ],
        loadingPumpIds: []
      }
    },
    created() {
      this.mdiPlay = mdiPlay;
    },
    methods: {
      isIngredientNeeded(ingredientId) {
        return this.neededIngredients.some(x => x.id === ingredientId);
      },
      updatePumpIngredient(pump, newIngredient) {
        let newPump = Object.assign({}, pump);
        newPump.currentIngredient = newIngredient;
        this.loadingPumpIds.push(newPump.id)
        PumpService.updatePump(newPump)
          .catch(error => {
            this.$q.notify({
              type: 'negative',
              message: error.response.data.message
            });
          })
          .finally(() => {
            let array = this.loadingPumpIds;
            array.splice(array.indexOf(newPump.id), 1);
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
      onMakeCocktail() {
        CocktailService.order(this.recipe.id, this.amountToProduce)
          .then(() => {
            this.$refs.mcDialog.hide();
            this.showProgressDialog = true;
          })
          .catch(error => {
          this.$q.notify({
            type: 'negative',
            message: error.response.data.message
          });
        })
      }
    },
    computed: {
      ...mapGetters({
        isUserPumpIngredientEditor: 'auth/isPumpIngredientEditor',
        doPumpsHaveAllIngredients: 'pumpLayout/doPumpsHaveAllIngredientsForRecipe',
        hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
        getPumpLayout: 'pumpLayout/getLayout',
        getPumpIngredients: 'pumpLayout/getPumpIngredients',
        isCleaning: 'pumpLayout/isCleaning'
      }),
      missingIngredients() {
        return this.neededIngredients.filter(x => !this.getPumpIngredients.some(y => x.id === y.id));
      },
      neededIngredients() {
          let ingredients = [];
          for(let productionstep of this.recipe.recipeIngredients) {
            for(let ingredientstep of productionstep) {
              if(!ingredients.some(x => x.id === ingredientstep.ingredient.id)) {
                ingredients.push(ingredientstep.ingredient);
              }
            }
          }
          return ingredients;
      },
      makeCocktailDialogHeadline() {
        if (!this.doPumpsHaveAllIngredients(this.recipe)) {
          return "Change pumplayout & make cocktail"
        }
        return "Make cocktail"
      },
      showProgressDialog: {
        get() {
          return this.$store.getters['cocktailProgress/isShowProgressDialog']
        },
        set(val) {
          return this.$store.commit('cocktailProgress/setShowProgressDialog', val)
        }
      }
    },
    validations() {
      return {
        amountToProduce: {
          required,
          minValue: minValue(50),
          maxValue: maxValue(1000)
        }
      };
    }
  }
</script>

<style scoped>

</style>
