<template>
  <c-q-headlined-card
    :card-class="cardClass"
    :headline="headline"
    :icon="icon"
    :icon-background-class="iconBackgroundClass"
    :icon-class="iconClass"
  >
    <template v-slot:content v-if="!isFulfilled">
      <ul style="text-align: start">
        <li v-for="ingredient in unassignedIngredients" :key="ingredient.id">
          {{ ingredient.name }}
          <q-chip :color="ingredient.inBar? 'green-4' : 'red-4'"
                  dense
                  square
                  :ripple="false"
          >
            <div v-if="ingredient.inBar">in bar</div>
            <div v-else>not in bar</div>
          </q-chip>
        </li>
      </ul>
    </template>
  </c-q-headlined-card>
</template>

<script>
import { mdiAlertOutline, mdiCheck } from '@quasar/extras/mdi-v5'
import CQHeadlinedCard from 'components/CQHeadlinedCard'

export default {
  name: 'CMakeCocktailDialogIngredientsToAddManually',
  components: { CQHeadlinedCard },
  props: {
    unassignedIngredients: {
      type: Array,
      reqiured: true
    }
  },
  setup () {
    return {
      mdiAlertOutline: mdiAlertOutline,
      mdiCheck: mdiCheck
    }
  },
  computed: {
    isFulfilled () {
      return this.unassignedIngredients.length === 0
    },
    cardClass () {
      return {
        'bg-warning': !this.isFulfilled,
        'bg-info': this.isFulfilled
      }
    },
    headline () {
      if (this.isFulfilled) {
        return 'All ingredients assigned to pumps! Cocktail can be produced fully automatic!'
      } else {
        return 'The following ingredients have to get added manually or are not assigned to pumps. ' +
          'You will be asked to add them during the production progress:'
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
