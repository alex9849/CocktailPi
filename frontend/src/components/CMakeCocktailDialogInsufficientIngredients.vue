<template>
  <c-q-headlined-card
    :headline="headline"
    :card-class="cardClass"
    :icon="icon"
    :icon-background-class="iconBackgroundClass"
    :icon-class="iconClass"
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
import { mdiAlertOutline, mdiCheck } from '@quasar/extras/mdi-v5'
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
  setup () {
    return {
      mdiAlertOutline: mdiAlertOutline
    }
  },
  computed: {
    isFulfilled () {
      return this.insufficientIngredients.length === 0
    },
    cardClass () {
      return {
        'bg-red-5': !this.isFulfilled,
        'bg-info': this.isFulfilled
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
      return this.isFulfilled ? 'text-white' : 'text-orange-14'
    },
    iconBackgroundClass () {
      return this.isFulfilled ? 'bg-light-green-14' : 'bg-red-5'
    },
    icon () {
      return this.isFulfilled ? mdiCheck : mdiAlertOutline
    }
  }
}
</script>

<style scoped>

</style>
