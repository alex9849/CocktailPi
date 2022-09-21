<template>
  <q-card v-if="cocktailProgress.state === 'MANUAL_INGREDIENT_ADD'"
          class="bg-warning"
  >
    <q-card-section>
      <div class="">Please manually add the following ingredients and click "continue":</div>
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
  <q-card v-if="cocktailProgress.state === 'MANUAL_ACTION_REQUIRED'"
          class="bg-warning" style="margin: 10px 0"
  >
    <q-card-section>
      <div class="">{{ cocktailProgress.writtenInstruction }}</div>
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
  computed: {
    ...mapGetters({
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      cocktailProgress: 'cocktailProgress/getCocktailProgress'
    })
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
