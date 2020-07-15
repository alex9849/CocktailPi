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

          :label="user.username"
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
  import {mapActions, mapGetters} from "vuex";
  import {mdiAccountBox, mdiPower} from "@quasar/extras/mdi-v5";
  import CircularCocktailProgress from "./Circular-Cocktail-Progress";

  export default {
    name: "AppHeader",
    components: {CircularCocktailProgress},
    methods: {
      ...mapActions({
        storeLogout: 'auth/logout'
      }),
      logout() {
        this.storeLogout();
        this.$router.push({name: 'login'});
      }
    },
    computed: {
      ...mapGetters({
        user: 'auth/getUser'
      })
    },
    created() {
      this.mdiAccountBox = mdiAccountBox;
      this.mdiPower = mdiPower;
    }
  }
</script>

<style scoped>

</style>
