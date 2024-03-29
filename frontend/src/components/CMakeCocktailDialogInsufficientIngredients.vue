<template>
  <c-q-headlined-card
    :headline="headline"
    :card-class="cardClass"
    :icon="icon"
    :icon-background-class="iconBackgroundClass"
    :icon-class="iconClass"
  >
    <template v-slot:content>
      <ul
        style="text-align: start"
        v-if="isFulfilled"
      >
        <li v-for="requiredIngredient in requiredIngredients" :key="requiredIngredient.ingredient.id">
          {{ requiredIngredient.ingredient.name }}:
          <strong>{{ requiredIngredient.amountRequired }} {{ requiredIngredient.ingredient.unit }}</strong>
        </li>
      </ul>
      <ul
        style="text-align: start"
        v-else
      >
        <li v-for="insufficientIngredient in insufficientIngredients" :key="insufficientIngredient.ingredient.id">
          {{ insufficientIngredient.ingredient.name }}:
          <strong>{{ insufficientIngredient.amountRequired }} {{ insufficientIngredient.ingredient.unit }}</strong> required
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
    requiredIngredients: {
      type: Array,
      required: true
    }
  },
  computed: {
    isFulfilled () {
      return this.insufficientIngredients.length === 0
    },
    insufficientIngredients () {
      return this.requiredIngredients
        .filter(x => x.amountMissing > 0)
    },
    cardClass () {
      return {
        'bg-warning': !this.isFulfilled,
        'bg-light-blue-3': this.isFulfilled
      }
    },
    headline () {
      if (this.isFulfilled) {
        return this.$t('component.make_cocktail_insufficient_ingredients.fulfilled_msg')
      } else {
        return this.$t('component.make_cocktail_insufficient_ingredients.not_fulfilled_msg')
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
