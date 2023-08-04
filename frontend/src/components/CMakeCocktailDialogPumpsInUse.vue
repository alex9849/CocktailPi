<template>
  <c-q-headlined-card
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
      mdiClose: mdiClose,
      mdiCheck: mdiCheck
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
        return 'Machine occupied! A cocktail ist getting prepared currently!'
      } else if (this.pumpsOccupied) {
        return 'Machine occupied! One or more pumps are getting cleaned/pumping up currently!'
      } else {
        return 'Machine is not occupied!'
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
