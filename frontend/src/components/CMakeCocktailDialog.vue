<template>
  <q-dialog
    :value="value"
    ref="mcDialog"
    @input="$emit('input', $event)"
  >
    <q-card
      class="text-center with-desktop"
    >
      <q-card-section class="q-gutter-md">
        <p class="text-h5">Order Cocktail</p>
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
        <q-card flat
                bordered
                class="bg-warning"
                v-if="unassignedIngredients.length !== 0"
        >
          <q-card-section horizontal>
            <div class="flex items-center col-auto"
                 v-if="!$q.platform.is.mobile"
            >
              <q-icon :name="mdiAlertOutline"
                      size="lg"
                      class="text-orange-14 q-pa-sm"
              ></q-icon>
            </div>
            <q-separator vertical
                         v-if="!$q.platform.is.mobile"
            />
            <div class="col">
              <div class="q-pa-sm">
                The following ingredients have to get added manually or are not assigned to pumps. You will be asked to
                add them during the production progress:
              </div>
              <q-separator></q-separator>
              <ul style="text-align: start">
                <li v-for="ingredient in unassignedIngredients">
                  {{ ingredient.name }}
                  <q-chip :color="isIngredientInBar(ingredient.id)? 'green-4' : 'red-4'"
                          dense
                          square
                  >
                    <div v-if="isIngredientInBar(ingredient.id)">in bar</div>
                    <div v-else>not in bar</div>
                  </q-chip>
                </li>
              </ul>
            </div>
          </q-card-section>
        </q-card>
        <q-table
          v-if="isUserPumpIngredientEditor"
          :columns="columns"
          :data="sortedPumpLayout"
          :pagination="{rowsPerPage: 0}"
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
                filter-manual-ingredients
                :bg-color="markPump(props.row)? 'green-3':undefined"
                :no-input-options="missingAutomaticIngredients"
                :loading="loadingPumpIds.includes(props.row.id, 0)"
              >
                <template
                  slot="afterIngredientName"
                  slot-scope="{scope}"
                >
                  <q-item-label
                    v-if="!!missingAutomaticIngredients.some(x => x.id === scope.opt.id)"
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
          :disable="hasCocktailProgress || $v.amountToProduce.$invalid"
        >
          Make cocktail
        </q-btn>
        <q-tooltip
          v-if="hasCocktailProgress"
        >
          {{ "A cocktail is already being made!" }}
        </q-tooltip>
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script>
import PumpService from "../services/pump.service";
import CocktailService from "../services/cocktail.service"
import {mapGetters} from "vuex";
import {mdiAlertOutline, mdiPlay} from "@quasar/extras/mdi-v5";
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
    this.mdiAlertOutline = mdiAlertOutline;
  },
  watch: {
    recipe: {
      immediate: true,
      handler(newValue) {
        this.amountToProduce = newValue.defaultAmountToFill
      }
    }
  },
  methods: {
    markPump(pump) {
      if (!pump.currentIngredient || !this.isIngredientNeeded(pump.currentIngredient.id)) {
        return false;
      }
      return this.sortedPumpLayout.find(x => {
        return !!x.currentIngredient &&
          pump.currentIngredient.id === x.currentIngredient.id
      }) === pump;
    },
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
    },
    isIngredientInBar(ingredientId) {
      return this.ownedIngredients.some(x => x.id === ingredientId);
    }
  },
  computed: {
    ...mapGetters({
      isUserPumpIngredientEditor: 'auth/isPumpIngredientEditor',
      doPumpsHaveAllIngredients: 'pumpLayout/doPumpsHaveAllIngredientsForRecipe',
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      getPumpLayout: 'pumpLayout/getLayout',
      getPumpIngredients: 'pumpLayout/getPumpIngredients',
      isCleaning: 'pumpLayout/isCleaning',
      ownedIngredients: 'bar/getOwnedIngredients'
    }),
    sortedPumpLayout() {
      let sorted = [];
      sorted.push(...this.getPumpLayout);
      return sorted.sort((a, b) => a.id - b.id);
    },
    neededIngredients() {
      let ingredients = [];
      for (let productionStep of this.recipe.productionSteps) {
        if (productionStep.type !== 'addIngredients') {
          continue
        }
        for (let ingredientStep of productionStep.stepIngredients) {
          if (!ingredients.some(x => x.id === ingredientStep.ingredient.id)) {
            ingredients.push(ingredientStep.ingredient);
          }
        }
      }
      return ingredients;
    },
    unassignedIngredients() {
      return this.neededIngredients.filter(x => !this.getPumpIngredients.some(y => x.id === y.id));
    },
    missingAutomaticIngredients() {
      return this.unassignedIngredients.filter(x => x.type === 'automated');
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

@media screen and (min-width: 600px) {
  .with-desktop {
    width: 500px;
  }
}

</style>
