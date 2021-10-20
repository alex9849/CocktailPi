<template>
  <div>
    <div v-if="asChip">
      <q-chip
        v-if="doPumpsHaveAllIngredients(recipe)"
        color="green"
        outline
        square
        dense
      >
        Full-Automatic
      </q-chip>
      <q-chip
        v-else-if="allIngredientsOwned(recipe)"
        color="warning"
        outline
        square
        dense
      >
        Half-Automatic
      </q-chip>
      <q-chip
        v-else
        color="negative"
        outline
        square
        dense
      >
        Missing ingredients
      </q-chip>
    </div>
    <div v-else>
      <q-icon
        v-if="doPumpsHaveAllIngredients(recipe)"
        :name="mdiCheckBold"
        size="md"
        color="positive">
        <q-tooltip>
          Cocktail can be produced automatically!
        </q-tooltip>
      </q-icon>
      <q-icon
        v-else-if="allIngredientsOwned(recipe)"
        :name="mdiCheckBold"
        size="md"
        color="warning">
        <q-tooltip>
          Some ingredients have to get added manually!
        </q-tooltip>
      </q-icon>
      <q-icon
        v-else
        :name="mdiClose"
        size="md"
        color="negative">
        <q-tooltip>
          Missing ingredients!
        </q-tooltip>
      </q-icon>
    </div>
  </div>
</template>

<script>
import {mapGetters} from "vuex";
import {mdiAlert, mdiCheckBold, mdiClose} from "@quasar/extras/mdi-v5";

export default {
  name: "CRecipeFabricableIcon",
  props: {
    recipe: {
      type: Object,
      required: true
    },
    asChip: {
      type: Boolean,
      default: false
    }
  },
  created() {
    this.mdiCheckBold = mdiCheckBold;
    this.mdiAlert = mdiAlert;
    this.mdiClose = mdiClose;
  },
  computed: {
    ...mapGetters({
      doPumpsHaveAllIngredients: 'pumpLayout/doPumpsHaveAllIngredientsForRecipe',
      ownedIngredients: 'bar/getOwnedIngredients'
    })
  },
  methods: {
    allIngredientsOwned(recipe) {
      for (let productionstep of recipe.productionSteps) {
        if (productionStep.type !== 'addIngredients') {
          continue
        }
        for (let ingredientstep of productionstep.stepIngredients) {
          if (!this.ownedIngredients.some(x => x.id === ingredientstep.ingredient.id)) {
            return false;
          }
        }
      }
      return true;
    }
  }
}
</script>

<style scoped>

</style>
