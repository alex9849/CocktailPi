<template>
  <c-q-headlined-card
    :headline="headline"
    :card-class="cardClass"
    :icon="icon"
    :icon-background-class="iconBackgroundClass"
    :icon-class="iconClass"
  >
    <template v-slot:content>
      <div class="row justify-center items-center q-pa-sm">
        <q-table
          :columns="columns"
          :rows="tableRows"
          hide-bottom
          dense
          flat
          class="q-ma-none"
          table-class="q-ma-none"
        >
          <thead>
          <tr>
            <th>Production step</th>
            <th>Ingredient group</th>
            <th>Replacement</th>
          </tr>
          </thead>
        </q-table>
      </div>
    </template>
  </c-q-headlined-card>
</template>

<script>
import CQHeadlinedCard from 'components/CQHeadlinedCard'
import { mdiAlertOutline, mdiCheck } from '@quasar/extras/mdi-v5'
export default {
  name: 'CMakeCocktailDialogIngredientGroupReplacements',
  components: { CQHeadlinedCard },
  props: {
    missingIngredientGroupReplacements: Array
  },
  data () {
    return {
      columns: [
        { name: 'productionStep', style: 'width: 30px', headerStyle: 'width: 30px', label: 'Production step', field: 'productionStep', align: 'center' },
        { name: 'ingredientGroup', label: 'Ingredient group', field: 'ingredientGroup', align: 'center' },
        { name: 'replacement', label: 'Replacement', field: 'replacement', align: 'center' }
      ],
      tableRows: [
        {
          productionStep: 1,
          ingredientGroup: 1,
          replacement: 1
        }, {
          productionStep: 1,
          ingredientGroup: 1,
          replacement: 1
        }
      ]
    }
  },
  computed: {
    isFulfilled () {
      return this.missingIngredientGroupReplacements.length === 0
    },
    cardClass () {
      return {
        'bg-warning': !this.isFulfilled,
        'bg-info': this.isFulfilled
      }
    },
    headline () {
      if (this.isFulfilled) {
        return 'All ingredient-groups have been replaced with concrete ingredients!'
      } else {
        return 'The following ingredient-groups have to get real existing ingredients assigned:'
      }
    },
    iconClass () {
      return this.isFulfilled ? 'text-white' : 'text-orange-14'
    },
    iconBackgroundClass () {
      return this.isFulfilled ? 'bg-light-green-14' : 'bg-warning'
    },
    icon () {
      return this.isFulfilled ? mdiCheck : mdiAlertOutline
    }
  }
}
</script>

<style scoped>
</style>
