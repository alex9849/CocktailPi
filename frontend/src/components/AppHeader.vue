<template>
  <q-header
    reveal
    bordered
    class="bg-header text-header shadow-3"
  >
    <q-toolbar>
      <slot name="left" />
      <q-toolbar-title class="items-center flex">
        <q-icon
          size="md"
          :name="mdiGlassCocktail"
        />
        {{ $t('header.machine_name') }}
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
                {{ $t('header.profile.profile_btn_label') }}
              </q-item-section>
            </q-item>
            <q-item clickable @click="logout()">
              <q-item-section avatar>
                <q-icon :name="mdiPower"/>
              </q-item-section>
              <q-item-section>
                {{ $t('header.profile.logout_btn_label') }}
              </q-item-section>
            </q-item>
          </q-list>

        </q-btn-dropdown>
      </div>
    </q-toolbar>
  </q-header>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import { mdiAccountBox, mdiAlert, mdiPower, mdiGlassCocktail } from '@quasar/extras/mdi-v5'
import CircularCocktailProgress from './Circular-Cocktail-Progress'

export default {
  name: 'AppHeader',
  components: { CircularCocktailProgress },
  methods: {
    ...mapActions({
      storeLogout: 'auth/logout'
    }),
    logout () {
      this.$router.push({ name: 'login' })
      this.storeLogout()
    }
  },
  computed: {
    ...mapGetters({
      user: 'auth/getUser',
      isLoggedIn: 'auth/isLoggedIn',
      colors: 'appearance/getNormalColors'
    }),
    username () {
      if (this.isLoggedIn) {
        return this.user.username
      }
      return ''
    }
  },
  setup () {
    return {
      mdiAccountBox: mdiAccountBox,
      mdiPower: mdiPower,
      mdiAlert: mdiAlert,
      mdiGlassCocktail: mdiGlassCocktail
    }
  }
}
</script>

<style scoped>

</style>
