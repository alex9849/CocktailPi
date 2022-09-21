<template>
  <q-linear-progress
    :value="progress"
    :stripe="stripe"
    :rounded="rounded"
    :animation-speed="500"
    :color="loadingBarColor"
    :size="size"
  >
    <div class="absolute-full flex flex-center">
      <q-badge
        color="red-5"
        :label="cocktailProgressBarLabel"
      />
    </div>
  </q-linear-progress>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'CCocktailProgressBar',
  props: {
    stripe: {
      type: Boolean,
      default: false
    },
    rounded: {
      type: Boolean,
      default: false
    },
    size: {
      type: String,
      default: () => '20px'
    }
  },
  computed: {
    ...mapGetters({
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      cocktailProgress: 'cocktailProgress/getCocktailProgress'
    }),
    progress () {
      if (!this.hasCocktailProgress) {
        return 0
      }
      return this.cocktailProgress.progress / 100
    },
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
