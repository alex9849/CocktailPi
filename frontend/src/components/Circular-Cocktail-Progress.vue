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
              :show-ingredients="false"
            >
              <template v-slot:bottom>
                <div class="q-my-sm">
                  <c-cocktail-production-manual-step-card />
                </div>
                <c-cocktail-progress-bar
                  stripe
                  rounded
                  size="20px"
                />
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
                    v-if="isAdmin || currentUser.id === cocktailProgress.userId"
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
import CCocktailProgressBar from 'components/CCocktailProgressBar'
import CCocktailProductionManualStepCard from 'components/CCocktailProductionManualStepCard'

export default {
  name: 'Circular-Cocktail-Progress',
  components: { CCocktailProductionManualStepCard, CCocktailProgressBar, CRecipeCard },
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
  methods: {
    onCancelCocktail () {
      this.canceling = true
      CocktailService.cancelCocktail()
        .finally(() => {
          this.canceling = false
        })
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
