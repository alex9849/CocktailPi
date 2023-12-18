<template>
  <c-q-headlined-card
    :card-class="cardClass"
    :headline="headline"
    :icon="icon"
    :icon-background-class="iconBackgroundClass"
    :icon-class="iconClass"
    :hide-content-slot="isFulfilled"
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
            <div v-if="ingredient.inBar">
              {{ $t('component.make_cocktail_add_manually.tags.in_bar') }}
            </div>
            <div v-else>
              {{ $t('component.make_cocktail_add_manually.tags.not_in_bar') }}
            </div>
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
      mdiAlertOutline,
      mdiCheck
    }
  },
  computed: {
    isFulfilled () {
      return this.unassignedIngredients.length === 0
    },
    cardClass () {
      return {
        'bg-warning': !this.isFulfilled,
        'bg-light-blue-3': this.isFulfilled
      }
    },
    headline () {
      if (this.isFulfilled) {
        return this.$t('component.make_cocktail_add_manually.fulfilled_msg')
      } else {
        return this.$t('component.make_cocktail_add_manually.not_fulfilled_msg')
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
