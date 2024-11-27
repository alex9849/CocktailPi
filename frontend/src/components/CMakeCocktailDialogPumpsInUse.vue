<template>
  <c-q-headlined-card
    v-if="!hide"
    :card-class="cardClass"
    :headline="headline"
    :icon="icon"
    :icon-background-class="iconBackgroundClass"
    :icon-class="iconClass"
    :hide-content-slot="isFulfilled"
  >
  </c-q-headlined-card>
</template>

<script>
import CQHeadlinedCard from 'components/CQHeadlinedCard'
import { mdiCheck, mdiClose } from '@quasar/extras/mdi-v5'
import { mapGetters } from 'vuex'

export default {
  name: 'CMakeCocktailDialogPumpsInUse',
  components: { CQHeadlinedCard },
  props: {
    pumpsOccupied: {
      type: Boolean,
      default: false
    }
  },
  setup () {
    return {
      mdiClose,
      mdiCheck
    }
  },
  computed: {
    ...mapGetters({
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress'
    }),
    isFulfilled () {
      return !this.hasCocktailProgress && !this.pumpsOccupied
    },
    cardClass () {
      return {
        'bg-warning': !this.isFulfilled,
        'bg-light-blue-3': this.isFulfilled
      }
    },
    headline () {
      if (this.hasCocktailProgress) {
        return this.$t('component.make_cocktail_occupied.occupied_cocktail_msg')
      } else if (this.pumpsOccupied) {
        return this.$t('component.make_cocktail_occupied.occupied_pumps_msg')
      } else {
        return this.$t('component.make_cocktail_occupied.fulfilled_msg')
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
    },
    hide () {
      return this.isFulfilled
    }
  }
}
</script>

<style scoped>

</style>
