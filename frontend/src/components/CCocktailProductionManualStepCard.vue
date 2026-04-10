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
        <div>
          {{ $t('component.cocktail_production_manual_step_card.add_ingredient_headline') }}
        </div>
        <div class="row q-col-gutter-sm">
          <div class="col-12 col-sm-grow">
            <ul>
              <li v-for="recipeIngredient in cocktailProgress.currentIngredientsToAddManually" :key="recipeIngredient.ingredient.id">
                {{ recipeIngredient.amount + ' ' + recipeIngredient.ingredient.unit + ' ' + recipeIngredient.ingredient.name }}
              </li>
            </ul>
          </div>
          <div
            v-if="cocktailProgress.showLoadCellValue"
            class="col-12 col-sm-shrink flex items-end justify-center"
          >
            <div class="row justify-center items-center q-col-gutter-xs">
              <div class="col-sm-12 col">
                <div class="row justify-center">
                  <div class="text-h4">
                    {{ cocktailProgress.loadCellValue }}g
                  </div>
                </div>
              </div>
              <div class="col-sm-12 col-auto">
                <div class="row justify-center">
                  <q-btn
                    color="info"
                    @click="onClickTare"
                    :loading="tareClicked"
                  >
                    {{ $t('component.cocktail_production_manual_step_card.tare_btn_label') }}
                  </q-btn>
                </div>
              </div>
            </div>
          </div>
          <div class="col-12 col-sm-auto justify-center items-end flex">
            <q-btn color="green"
                   :loading="continueProductionClicked"
                   @click="onClickContinueProduction"
            >
              {{ $t('component.cocktail_production_manual_step_card.continue_btn_label') }}
            </q-btn>
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
            >
              {{ $t('component.cocktail_production_manual_step_card.continue_btn_label') }}
            </q-btn>
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
      continueProductionClicked: false,
      tareClicked: false
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
    },
    onClickTare () {
      this.tareClicked = true
      CocktailService.tareLoadCell()
        .finally(() => {
          this.tareClicked = false
        })
    }
  }
}
</script>

<style scoped>

</style>
