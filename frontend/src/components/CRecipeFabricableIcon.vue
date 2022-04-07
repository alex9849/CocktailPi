<template>
  <div>
    <div v-if="asChip">
      <q-chip
        v-if="allIngredientsOnPump"
        color="green"
        outline
        square
        dense
      >
        Full-Automatic
      </q-chip>
      <q-chip
        v-else-if="allIngredientsOwned"
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
        v-if="allIngredientsOnPump"
        :name="mdiCheckBold"
        size="md"
        color="positive">
        <q-tooltip>
          Cocktail can be produced automatically!
        </q-tooltip>
      </q-icon>
      <q-icon
        v-else-if="allIngredientsOwned"
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
import { mdiAlert, mdiCheckBold, mdiClose } from '@quasar/extras/mdi-v5'

export default {
  name: 'CRecipeFabricableIcon',
  props: {
    ingredients: {
      type: Array,
      required: true
    },
    asChip: {
      type: Boolean,
      default: false
    }
  },
  created () {
    this.mdiCheckBold = mdiCheckBold
    this.mdiAlert = mdiAlert
    this.mdiClose = mdiClose
  },
  computed: {
    allIngredientsOwned () {
      for (const ingredient of this.ingredients) {
        if (!ingredient.inBar && !ingredient.onPump) {
          return false
        }
      }
      return true
    },
    allIngredientsOnPump () {
      for (const ingredient of this.ingredients) {
        if (!ingredient.onPump) {
          return false
        }
      }
      return true
    }
  }
}
</script>

<style scoped>

</style>
