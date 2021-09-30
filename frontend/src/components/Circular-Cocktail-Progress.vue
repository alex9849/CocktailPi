<template>
  <div style="display: inline">
    <q-circular-progress
      show-value
      style="margin: 5px; position: marker"
      class="text-white cursor-pointer actionable"
      :value="hasCocktailProgress? cocktailProgress.progress: 0"
      :thickness="0.13"
      color="positive"
      :center-color="hasCocktailProgress? (cocktailProgress.canceled? 'red' : 'green') : 'info'"
      track-color="transparent"
      :instant-feedback="!hasCocktailProgress"
      size="lg"
      @click="showDialog = !showDialog"
    >
      <q-icon :name="hasCocktailProgress? (cocktailProgress.canceled? mdiStop : mdiTimerSandEmpty) : mdiCheckBold" size="20px"/>
    </q-circular-progress>
    <q-dialog
      v-model="showDialog"
    >
      <q-card q-card-section class="text-center" style="width: 100%">
        <q-card-section
          style="padding-bottom: 0"
        >
          <h5 style="margin: 0 0 10px 0">Currently fabricated cocktail</h5>
          <q-splitter
            horizontal
            :value="10"
            style="padding-bottom: 10px"
          />
          <div v-if="hasCocktailProgress">
            <c-recipe-card
              :recipe="cocktailProgress.recipe"
              class="text-left"
              background-color="#EEEEEE"
            >
              <template v-slot:bottom>
                <q-linear-progress
                  :value="cocktailProgress.progress / 100"
                  stripe
                  rounded
                  :color="isCocktailCancelled ? 'red-4' : 'green-4'"
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
              <template v-slot:headlineRight>
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
import {mapGetters} from "vuex";
import {mdiCheckBold, mdiMagnify, mdiStop, mdiTimerSandEmpty} from "@quasar/extras/mdi-v5";
import CocktailService from "../services/cocktail.service";
import CRecipeCard from "./CRecipeCard";

export default {
    name: "Circular-Cocktail-Progress",
    components: {CRecipeCard},
    data() {
      return {
        canceling: false,
        noCacheString: new Date().getTime()
      }
    },
    created() {
      this.mdiTimerSandEmpty = mdiTimerSandEmpty;
      this.mdiMagnify = mdiMagnify;
      this.mdiStop = mdiStop;
      this.mdiCheckBold = mdiCheckBold;
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
        get() {
          return this.$store.getters['cocktailProgress/isShowProgressDialog']
        },
        set(val) {
          return this.$store.commit('cocktailProgress/setShowProgressDialog', val)
        }
      },
      isCocktailCancelled() {
        if(!this.hasCocktailProgress) {
          return false;
        }
        return this.cocktailProgress.state === 'CANCELLED';
      },
      cocktailProgressBarLabel() {
        if(!this.hasCocktailProgress) {
          return '';
        }
        if(this.cocktailProgress.state === 'FINISHED') {
          return 'Done!';
        }
        if(this.cocktailProgress.state === 'CANCELLED') {
          return 'Cancelled!';
        }
        return this.cocktailProgress.progress + '%'
      }
    },
    methods: {
      onCancelCocktail() {
        this.canceling = true;
        CocktailService.cancelCocktail()
          .then(() => {
            this.canceling = false;
          }, err => {
            this.canceling = false;
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
