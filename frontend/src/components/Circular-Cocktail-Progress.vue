<template>
  <div style="display: inline">
    <q-circular-progress
      show-value
      style="margin: 5px; position: marker"
      class="text-white cursor-pointer actionable"
      :value="60"
      :thickness="0.13"
      color="positive"
      center-color="info"
      track-color="transparent"
      size="lg"
      @click="showDialog = !showDialog"
    >
      <q-icon :name="mdiTimerSandEmpty" size="20px"/>
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
                            :to="{name: 'recipedetails', params: {id: cocktailProgress.recipe.id}}"
                          />
                          <q-btn
                            dense
                            round
                            color="red"
                            class="q-ml-sm"
                            text-color="white"
                            :icon="mdiStop"
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
                    color="green-5"
                    size="20px"
                  >
                    <div class="absolute-full flex flex-center">
                      <q-badge
                        color="red-5"
                        :label="cocktailProgress.progress + '%'"
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
  import {mapGetters, mapMutations} from "vuex";
  import {mdiMagnify, mdiStop, mdiTimerSandEmpty} from "@quasar/extras/mdi-v5";
  import SockJS from "sockjs-client";
  import Stomp from "stompjs";
  import authHeader from "../services/auth-header";

  export default {
    name: "Circular-Cocktail-Progress",
    data() {
      return {
        showDialog: false,
        stompClient: null,
        websocketAutoreconnect: true,
        noCacheString: new Date().getTime()
      }
    },
    created() {
      this.mdiTimerSandEmpty = mdiTimerSandEmpty;
      this.mdiMagnify = mdiMagnify;
      this.mdiStop = mdiStop;
      this.connectWebsocket();
    },
    destroyed() {
      this.destroyWebsocket();
    },
    watch: {
      'cocktailProgress.recipe.id': function () {
        this.noCacheString = new Date().getTime()
      }
    },
    computed: {
      ...mapGetters({
        hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
        cocktailProgress: 'cocktailProgress/getCocktailProgress'
      })
    },
    methods: {
      ...mapMutations({
        setCocktailProgress: 'cocktailProgress/setCocktailProgress'
      }),
      connectWebsocket() {
        let socket = new SockJS("/ws/cocktailprogress");
        this.stompClient = Stomp.over(socket);
        let vm = this;
        let connectCallback = function () {
          vm.stompClient.subscribe('/topic/cocktailprogress', function (cocktailProgressMessage) {
            if(cocktailProgressMessage.body === "DELETE") {
              vm.setCocktailProgress(null);
            } else {
              vm.setCocktailProgress(JSON.parse(cocktailProgressMessage.body));
            }
          });
          vm.stompClient.send("/topic/cocktailprogress", {}, {name: 'Welcome'})
        };
        let disconnectCallback = function () {
          if (vm.websocketAutoreconnect) {
            vm.connectWebsocket();
          }
        };

        let headers = {
          'Authorization': authHeader()
        };
        this.stompClient.connect(headers, connectCallback, disconnectCallback);
      },
      disconnectWebsocket() {
        if (this.stompClient != null) {
          this.stompClient.disconnect();
        }
      },
      destroyWebsocket() {
        this.websocketAutoreconnect = false;
        this.disconnectWebsocket();
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
