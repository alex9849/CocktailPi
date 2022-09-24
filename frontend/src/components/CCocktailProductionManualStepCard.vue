<template>
  <transition-group
    appear
    enter-active-class="animated fadeIn"
    leave-active-class="animated fadeOut"
  >
    <q-card
      v-if="cocktailProgress.state === 'MANUAL_INGREDIENT_ADD'"
      :class="colorClass"
    >
      <q-card-section>
        <div>Please manually add the following ingredients and click "continue":</div>
        <div class="row">
          <div class="col-12 col-sm">
            <ul>
              <li v-for="recipeIngredient in cocktailProgress.currentIngredientsToAddManually" :key="recipeIngredient.ingredient.id">
                {{ recipeIngredient.amount + ' ' + recipeIngredient.ingredient.unit + ' ' + recipeIngredient.ingredient.name }}
              </li>
            </ul>
          </div>
          <div class="flex col-12 col-sm-auto justify-center" style="align-self: end;" >
            <q-btn color="green"
                   :loading="continueProductionClicked"
                   @click="onClickContinueProduction"
            >Continue</q-btn>
          </div>
        </div>
      </q-card-section>
    </q-card>
    <q-card
      v-if="cocktailProgress.state === 'MANUAL_ACTION_REQUIRED'"
      :class="colorClass"
    >
      <q-card-section>
        <div>{{ cocktailProgress.writtenInstruction }}</div>
        <div class="row">
          <div class="col col-sm"></div>
          <div class="flex col-12 col-sm-auto justify-center" style="align-self: end;" >
            <q-btn color="green"
                   @click="onClickContinueProduction"
            >Continue</q-btn>
          </div>
        </div>
      </q-card-section>
    </q-card>
  </transition-group>
</template>

<script>
import { mapGetters } from 'vuex'
import CocktailService from 'src/services/cocktail.service'

export default {
  name: 'CCocktailProductionManualStepCard',
  data () {
    return {
      continueProductionClicked: false
    }
  },
  props: {
    color: {
      type: String,
      default: 'warning'
    }
  },
  computed: {
    ...mapGetters({
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      cocktailProgress: 'cocktailProgress/getCocktailProgress'
    }),
    colorClass () {
      return 'bg-' + this.color
    }
  },
  methods: {
    onClickContinueProduction () {
      this.continueProductionClicked = true
      CocktailService.continueProduction()
        .finally(() => {
          this.continueProductionClicked = false
        })
    }
  }
}
</script>

<style scoped>

</style>
