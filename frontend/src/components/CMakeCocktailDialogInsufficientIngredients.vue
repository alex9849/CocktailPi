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
      >
        <li
          v-for="requiredIngredient in displayIngredientList"
          :key="requiredIngredient.ingredient.id"
        >
          {{ requiredIngredient.ingredient.name }}:
          <strong>
            {{ requiredIngredient.amountRequired }} {{ requiredIngredient.ingredient.unit }}
          </strong>
          <span
            v-if="isError === 'INSUFFICIENT_INGREDIENTS'"
          > required</span>
          <q-chip
            :color="requiredIngredient.ingredient.inBar? 'green-4' : 'red-4'"
            v-if="!requiredIngredient.ingredient.onPump"
            dense
            square
            :ripple="false"
          >
            <div v-if="requiredIngredient.ingredient.inBar">
              {{ $t('component.make_cocktail_insufficient_ingredients.tags.in_bar') }}
            </div>
            <div v-else>
              {{ $t('component.make_cocktail_insufficient_ingredients.tags.not_in_bar') }}
            </div>
          </q-chip>
        </li>
      </ul>
    </template>
  </c-q-headlined-card>
</template>

<script>
import { mdiClose, mdiCheck, mdiAlertOutline } from '@quasar/extras/mdi-v5'
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
    isError () {
      if (this.insufficientIngredients.length !== 0) {
        return 'INSUFFICIENT_INGREDIENTS'
      }
      if (this.ingredientsToAddManually.length !== 0) {
        return 'UNASSIGNED_INGREDIENTS'
      }
      return null
    },
    insufficientIngredients () {
      return this.requiredIngredients
        .filter(x => x.amountMissing > 0)
    },
    ingredientsToAddManually () {
      return this.requiredIngredients
        .filter(x => !x.ingredient.onPump).map(x => x.ingredient)
    },
    displayIngredientList () {
      switch (this.isError) {
        case 'INSUFFICIENT_INGREDIENTS': return this.insufficientIngredients
        case 'UNASSIGNED_INGREDIENTS': return this.requiredIngredients
        default: return this.requiredIngredients
      }
    },
    cardClass () {
      return {
        'bg-warning': this.isError,
        'bg-light-blue-3': !this.isError
      }
    },
    headline () {
      switch (this.isError) {
        case 'INSUFFICIENT_INGREDIENTS': return this.$t('component.make_cocktail_insufficient_ingredients.insufficient_filling_level')
        case 'UNASSIGNED_INGREDIENTS': return this.$t('component.make_cocktail_insufficient_ingredients.unassigned_ingredients')
        default: return this.$t('component.make_cocktail_insufficient_ingredients.fulfilled_msg')
      }
    },
    iconClass () {
      return this.isError ? 'text-negative' : 'text-white'
    },
    iconBackgroundClass () {
      return this.isError ? 'bg-warning' : 'bg-light-green-14'
    },
    icon () {
      switch (this.isError) {
        case 'INSUFFICIENT_INGREDIENTS': return mdiClose
        case 'UNASSIGNED_INGREDIENTS': return mdiAlertOutline
        default: return mdiCheck
      }
    }
  }
}
</script>

<style scoped>

</style>
