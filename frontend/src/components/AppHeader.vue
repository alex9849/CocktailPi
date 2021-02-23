<template>
  <q-header reveal bordered>
    <q-toolbar>
      <slot name="left" />

      <q-toolbar-title>
        CocktailMaker
      </q-toolbar-title>

      <div>
        <circular-cocktail-progress />
        <q-btn-dropdown
          size="md"
          flat
          :label="username"
          :icon="mdiAccountBox"
        >
          <q-list separator bordered style="border-radius: 0px">
            <q-item clickable :to="{name: 'myprofile'}">
              <q-item-section avatar>
                <q-icon :name="mdiAccountBox"/>
              </q-item-section>
              <q-item-section>
                Profile
              </q-item-section>
            </q-item>
            <q-item clickable @click="logout()">
              <q-item-section avatar>
                <q-icon :name="mdiPower"/>
              </q-item-section>
              <q-item-section>
                Logout
              </q-item-section>
            </q-item>
          </q-list>

        </q-btn-dropdown>
      </div>
    </q-toolbar>
  </q-header>
</template>

<script>
import {mapActions, mapGetters, mapMutations} from "vuex";
import {mdiAccountBox, mdiPower} from "@quasar/extras/mdi-v5";
import CircularCocktailProgress from "./Circular-Cocktail-Progress";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import authHeader from "../services/auth-header";

export default {
    name: "AppHeader",
    components: {CircularCocktailProgress},
    data() {
      return {
        websocketAutoreconnect: true,
        stompClient: null
      }
    },
    methods: {
      ...mapMutations({
        setCocktailProgress: 'cocktailProgress/setCocktailProgress',
        setPumpLayout: 'pumpLayout/setLayout'
      }),
      ...mapActions({
        storeLogout: 'auth/logout',
      }),
      logout() {
        this.storeLogout();
        this.$router.push({name: 'login'});
      },
      connectWebsocket() {
        let socket = new SockJS(this.$store.getters['auth/getFormattedServerAddress'] + "/ws/cocktailprogress");
        this.stompClient = Stomp.over(socket);
        if(!process.env.DEV) {
          this.stompClient.debug = null;
        }
        this.websocketAutoreconnect = true;
        let vm = this;
        let connectCallback = function () {
          vm.stompClient.subscribe('/topic/cocktailprogress', function (cocktailProgressMessage) {
            if(cocktailProgressMessage.body === "DELETE") {
              vm.setCocktailProgress(null);
            } else {
              let progress = JSON.parse(cocktailProgressMessage.body);
              progress.recipe.lastUpdate = new Date(progress.recipe.lastUpdate)
              vm.setCocktailProgress(progress);
            }
          });
          vm.stompClient.subscribe('/topic/pumplayout', function (layoutMessage) {
            vm.setPumpLayout(JSON.parse(layoutMessage.body));
          });
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
    },
    computed: {
      ...mapGetters({
        user: 'auth/getUser',
        isLoggedIn: 'auth/isLoggedIn'
      }),
      username() {
        if(this.isLoggedIn) {
          return this.user.username;
        }
        return '';
      }
    },
    watch: {
      isLoggedIn(val) {
        if(val) {
          this.connectWebsocket();
        } else {
          this.destroyWebsocket();
        }
      }
    },
    created() {
      this.mdiAccountBox = mdiAccountBox;
      this.mdiPower = mdiPower;
      if(this.isLoggedIn) {
        this.connectWebsocket();
      }
    },
    destroyed() {
      this.destroyWebsocket();
    }
  }
</script>

<style scoped>

</style>
