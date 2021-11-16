<template>
  <div style="display: inline">
    <q-circular-progress
      show-value
      style="margin: 5px; position: marker"
      class="text-white cursor-pointer actionable"
      :value="hasCocktailProgress? cocktailProgress.progress: 0"
      :thickness="0.13"
      color="positive"
      :center-color="loadingBarColor"
      track-color="transparent"
      :instant-feedback="!hasCocktailProgress"
      size="lg"
      @click="showDialog = !showDialog"
    >
      <q-icon :name="circularProgressIcon" size="20px"/>
    </q-circular-progress>
    <q-dialog
      v-model:model-value="showDialog"
    >
      <q-card q-card-section class="text-center" style="width: 100%">
        <q-card-section
          style="padding-bottom: 0"
        >
          <h5 style="margin: 0 0 10px 0">Currently fabricated cocktail</h5>
          <q-splitter
            horizontal
            :model-value="10"
            style="padding-bottom: 10px"
          />
          <div v-if="hasCocktailProgress">
            <c-recipe-card
              :recipe="cocktailProgress.recipe"
              class="text-left"
              background-color="#EEEEEE"
            >
              <template v-slot:bottom>
                <q-card v-if="cocktailProgress.state === 'MANUAL_INGREDIENT_ADD'"
                        class="bg-warning" style="margin: 10px 0"
                >
                  <q-card-section>
                    <div class="">Please manually add the following ingredients and click "continue":</div>
                    <div class="row">
                      <div class="col-12 col-sm">
                        <ul>
                          <li v-for="recipeIngredient in cocktailProgress.currentIngredientsToAddManually">
                            {{ recipeIngredient.amount + ' ' + recipeIngredient.ingredient.unit + ' ' + recipeIngredient.ingredient.name }}
                          </li>
                        </ul>
                      </div>
                      <div class="flex col-12 col-sm-auto justify-center" style="align-self: end;" >
                        <q-btn color="green"
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
                <q-linear-progress
                  :value="cocktailProgress.progress / 100"
                  stripe
                  rounded
                  :color="loadingBarColor"
                  size="20px"
                  style="margin: 10px 0"
                >
                  <div class="absolute-full flex flex-center">
                    <q-badge
                      color="red-5"
                      :label="cocktailProgressBarLabel"
                    />
                  </div>
                </q-linear-progress>
              </template>
              <template v-slot:topRight>
                <div>
                  <q-btn
                    dense
                    round
                    color="info"
                    class="q-ml-sm"
                    text-color="white"
                    :icon="mdiMagnify"
                    @click="showDialog = false"
                    :to="{name: 'recipedetails', params: {id: cocktailProgress.recipe.id}}"
                  />
                  <q-btn
                    dense
                    round
                    color="red"
                    class="q-ml-sm"
                    text-color="white"
                    :icon="mdiStop"
                    :loading="canceling"
                    :disable="hasCocktailProgress && (cocktailProgress.state === 'CANCELLED' || cocktailProgress.state === 'FINISHED')"
                    @click="onCancelCocktail()"
                    v-if="isAdmin || currentUser.id === cocktailProgress.user.id"
                  />
                </div>
              </template>
            </c-recipe-card>
          </div>
          <div v-else>
            <p>
              Currently no cocktail gets fabricated!
            </p>
            <p style="margin-bottom: 0">
              Go to "My recipes" or "Public recipes" to put one in order.
            </p>
          </div>
        </q-card-section>
        <q-card-actions
          style="padding-top: 32px; padding-bottom: 16px"
          align="around"
        >
          <q-btn
            color="grey"
            @click="showDialog = false"
            style="width: 100px"
          >
            Close
          </q-btn>
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { mdiAlertOutline, mdiCheckBold, mdiMagnify, mdiStop, mdiTimerSandEmpty } from '@quasar/extras/mdi-v5'
import CocktailService from '../services/cocktail.service'
import CRecipeCard from './CRecipeCard'

export default {
  name: 'Circular-Cocktail-Progress',
  components: { CRecipeCard },
  data () {
    return {
      canceling: false,
      continueProductionClicked: false,
      noCacheString: new Date().getTime()
    }
  },
  created () {
    this.mdiTimerSandEmpty = mdiTimerSandEmpty
    this.mdiMagnify = mdiMagnify
    this.mdiStop = mdiStop
    this.mdiCheckBold = mdiCheckBold
    this.mdiAlertOutline = mdiAlertOutline
  },
  watch: {
    'cocktailProgress.recipe.id': function () {
      this.noCacheString = new Date().getTime()
    }
  },
  computed: {
    ...mapGetters({
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      cocktailProgress: 'cocktailProgress/getCocktailProgress',
      currentUser: 'auth/getUser',
      isAdmin: 'auth/isAdmin'
    }),
    showDialog: {
      get () {
        return this.$store.getters['cocktailProgress/isShowProgressDialog']
      },
      set (val) {
        return this.$store.commit('cocktailProgress/setShowProgressDialog', val)
      }
    },
    isCocktailCancelled () {
      if (!this.hasCocktailProgress) {
        return false
      }
      return this.cocktailProgress.state === 'CANCELLED'
    },
    circularProgressIcon () {
      if (!this.hasCocktailProgress) {
        return mdiCheckBold
      }
      if (this.cocktailProgress.state === 'FINISHED') {
        return mdiCheckBold
      }
      if (this.cocktailProgress.state === 'CANCELLED') {
        return mdiStop
      }
      if (this.cocktailProgress.state === 'MANUAL_ACTION_REQUIRED' || this.cocktailProgress.state === 'MANUAL_INGREDIENT_ADD') {
        return mdiAlertOutline
      }
      return mdiTimerSandEmpty
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
  },
  methods: {
    onCancelCocktail () {
      this.canceling = true
      CocktailService.cancelCocktail()
        .finally(() => {
          this.canceling = false
        })
    },
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
  .actionable {
    border-radius: 4px;
  }

  .actionable:hover {
    box-shadow: inset 0 0 100px 100px rgba(255, 255, 255, 0.1);
  }
</style>
