<template>
  <div class="row items-center justify-center">
    <q-linear-progress
      v-if="hasCocktailProgress"
      :value="cocktailProgress.progress / 100"
      stripe
      rounded
      :animation-speed="500"
      :color="loadingBarColor"
      size="31px"
    >
      <div class="absolute-full flex flex-center">
        <q-badge
          color="red-5"
          :label="cocktailProgressBarLabel"
        />
      </div>
    </q-linear-progress>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { mdiGestureTap } from '@quasar/extras/mdi-v5'
export default {
  name: 'CSimpleHeaderProgressStatus',
  created () {
    this.mdiGestureTap = mdiGestureTap
  },
  computed: {
    ...mapGetters({
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      cocktailProgress: 'cocktailProgress/getCocktailProgress'
    }),
    loadingBarColor () {
      if (!this.hasCocktailProgress) {
        return 'info'
      }
      if (this.cocktailProgress.state === 'FINISHED') {
        return 'green'
      }
      if (this.cocktailProgress.state === 'CANCELLED') {
        return 'red'
      }
      if (this.cocktailProgress.state === 'MANUAL_ACTION_REQUIRED' || this.cocktailProgress.state === 'MANUAL_INGREDIENT_ADD') {
        return 'warning'
      }
      return 'green'
    },
    cocktailProgressBarLabel () {
      if (!this.hasCocktailProgress) {
        return ''
      }
      if (this.cocktailProgress.state === 'FINISHED') {
        return 'Done!'
      }
      if (this.cocktailProgress.state === 'CANCELLED') {
        return 'Cancelled!'
      }
      if (this.cocktailProgress.state === 'MANUAL_ACTION_REQUIRED' || this.cocktailProgress.state === 'MANUAL_INGREDIENT_ADD') {
        return 'Manual action required! (' + this.cocktailProgress.progress + '%)'
      }
      return this.cocktailProgress.progress + '%'
    }
  }
}
</script>

<style scoped>

</style>
