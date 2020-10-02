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
          <h5 style="margin: 0 0 10px 0">Current prepared cocktail</h5>
          <q-splitter
            horizontal
            :value="10"
            style="padding-bottom: 10px"
          />
          <div v-if="hasCocktailProgress">
            <q-card
              flat
              style="background-color: #f3f3fa"
              class="text-left"
            >
              <q-card-section>
                <div
                  class="row"
                >
                  <q-img
                    :src="'/api/recipe/' + cocktailProgress.recipe.id + '/image?nocache=' + noCacheString"
                    placeholder-src="../assets/cocktail-solid.png"
                    :ratio="16/9"
                    class="col rounded-borders"
                    style="max-width: 225px; max-height: 180px"
                  />
                  <div class="col" style="padding-left: 10px; position: relative">
                    <div class="row">
                      <div class="col">
                        <h5
                          style="margin: 0; padding-bottom: 10px;"
                        >
                          <b>{{ cocktailProgress.recipe.name}}</b>
                        </h5>
                      </div>
                      <div class="col">
                        <div class="float-right">
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
                            :disable="hasCocktailProgress && (cocktailProgress.canceled || cocktailProgress.done)"
                            @click="onCancelCocktail()"
                            v-if="isAdmin || currentUser.id === cocktailProgress.user.id"
                          />
                        </div>
                      </div>
                    </div>
                    <div class="row">
                      {{ cocktailProgress.recipe.shortDescription }}
                    </div>

                    <div class="row" style="position: absolute; bottom: 0; left: 0; right: 0; padding-inline: 10px">
                      <div class="col"/>
                      <div class="col" style="text-align: right; max-width: max-content">
                        by {{ cocktailProgress.recipe.owner.username }}
                      </div>
                    </div>
                  </div>
                </div>
                <div
                  style="padding-top: 10px"
                  class="row"
                >
                  <q-linear-progress
                    :value="cocktailProgress.progress / 100"
                    stripe
                    rounded
                    :color="cocktailProgress.canceled? 'red-4' : 'green-4'"
                    size="20px"
                  >
                    <div class="absolute-full flex flex-center">
                      <q-badge
                        color="red-5"
                        :label="cocktailProgressBarLabel"
                      />
                    </div>
                  </q-linear-progress>
                </div>
              </q-card-section>
            </q-card>
          </div>
          <div v-else>
            <p>
              Currently no cocktail gets prepared!
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
        showDialog: false,
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
      cocktailProgressBarLabel() {
        if(!this.hasCocktailProgress) {
          return '';
        }
        if(this.cocktailProgress.done) {
          return 'Done!';
        }
        if(this.cocktailProgress.canceled) {
          return 'Canceled!';
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
