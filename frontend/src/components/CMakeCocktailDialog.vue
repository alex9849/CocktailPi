<template>
  <q-dialog
    :value="value"
    ref="mcDialog"
    @input="$emit('input', $event)"
  >
    <q-card class="text-center" style="width: 500px">
      <q-card-section class="q-gutter-md">
        <p class="text-h5">{{ makeCocktailDialogHeadline }}</p>
        <q-splitter
          horizontal
          :value="10"
        />
        <q-input
          v-model.number="amountToProduce"
          label="Amount to produce"
          outlined
          input-class="text-center text-weight-medium"
          rounded
          type="number"
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
                :loading="loadingPumpIds.includes(props.row.id, 0)"
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
          @click="onMakeCocktail()"
          :disable="!doPumpsHaveAllIngredients(recipe) || hasCocktailProgress"
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
      updatePumpIngredient(pump, newIngredient) {
        let newPump = Object.assign({}, pump);
        newPump.currentIngredient = newIngredient;
        this.loadingPumpIds.push(newPump.id)
        PumpService.updatePump(newPump)
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
      }
    },
    computed: {
      ...mapGetters({
        isUserPumpIngredientEditor: 'auth/isPumpIngredientEditor',
        doPumpsHaveAllIngredients: 'pumpLayout/doPumpsHaveAllIngredientsForRecipe',
        hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
        getPumpLayout: 'pumpLayout/getLayout',
        isCleaning: 'pumpLayout/isCleaning'
      }),
      makeCocktailDialogHeadline() {
        if(!this.doPumpsHaveAllIngredients(this.recipe)) {
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
    }
  }
</script>

<style scoped>

</style>
