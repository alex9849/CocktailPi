<template>
  <c-q-headlined-card
    :headline="headline"
    :card-class="cardClass"
    :icon="icon"
    :icon-background-class="iconBackgroundClass"
    :icon-class="iconClass"
    :hide-content-slot="isFulfilled"
  >
    <template v-slot:content v-if="!isFulfilled">
      <ul style="text-align: start">
        <li v-for="insufficientIngredient in insufficientIngredients" :key="insufficientIngredient.ingredient.id">
          {{ insufficientIngredient.ingredient.name }}:
          <strong>{{ insufficientIngredient.amountNeeded }} ml</strong> required
        </li>
      </ul>
    </template>
  </c-q-headlined-card>
</template>

<script>
import { mdiClose, mdiCheck } from '@quasar/extras/mdi-v5'
import CQHeadlinedCard from 'components/CQHeadlinedCard'

export default {
  name: 'CMakeCocktailDialogInsufficientIngredients',
  components: { CQHeadlinedCard },
  props: {
    insufficientIngredients: {
      type: Object,
      required: true
    }
  },
  computed: {
    isFulfilled () {
      return this.insufficientIngredients.length === 0
    },
    cardClass () {
      return {
        'bg-warning': !this.isFulfilled,
        'bg-light-blue-3': this.isFulfilled
      }
    },
    headline () {
      if (this.isFulfilled) {
        return 'Enough liquid left to make cocktail!'
      } else {
        return 'Can\'t make cocktail! Some pumps don\'t have enough liquid left:'
      }
    },
    iconClass () {
      return this.isFulfilled ? 'text-white' : 'text-negative'
    },
    iconBackgroundClass () {
      return this.isFulfilled ? 'bg-light-green-14' : 'bg-warning'
    },
    icon () {
      return this.isFulfilled ? mdiCheck : mdiClose
    }
  }
}
</script>

<style scoped>

</style>
