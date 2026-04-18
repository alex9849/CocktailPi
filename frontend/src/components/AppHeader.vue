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
        {{ getProjectName }}
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
            <q-item clickable @click="reload()">
              <q-item-section avatar>
                <q-icon :name="mdiReload"/>
              </q-item-section>
              <q-item-section>
                {{ $t('header.profile.reload_btn_label') }}
              </q-item-section>
            </q-item>
            <q-item
              v-if="getUserCount === 1"
              clickable
              @click="clickSwitchUser()"
            >
              <q-item-section avatar>
                <q-icon :name="mdiAccountSwitchOutline"/>
              </q-item-section>
              <q-item-section>
                Switch User
              </q-item-section>
            </q-item>
            <q-item clickable @click="logout()">
              <q-item-section avatar>
                <q-icon :name="mdiPower"/>
              </q-item-section>
              <q-item-section v-if="getUserCount === 1">
                {{ $t('header.profile.logout_btn_label') }}
              </q-item-section>
              <q-item-section v-else>
                Logback
              </q-item-section>
            </q-item>
          </q-list>

        </q-btn-dropdown>
      </div>
    </q-toolbar>
    <q-dialog
      v-model:model-value="showSwitchUserDialog"
    >
      <CLoginCard
        @login-success="handleLoginSuccess"
      />
    </q-dialog>
  </q-header>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import { mdiAccountBox, mdiReload, mdiAlert, mdiPower, mdiGlassCocktail, mdiAccountSwitchOutline } from '@quasar/extras/mdi-v5'
import CircularCocktailProgress from './Circular-Cocktail-Progress'
import CLoginCard from 'components/CLoginCard.vue'

export default {
  name: 'AppHeader',
  components: { CLoginCard, CircularCocktailProgress },
  data: () => {
    return {
      showSwitchUserDialog: false
    }
  },
  methods: {
    ...mapActions({
      storeLogout: 'auth/logout'
    }),
    logout () {
      this.$router.push({ name: 'login' })
      this.$nextTick(() => {
        this.storeLogout()
      })
    },
    clickSwitchUser () {
      this.showSwitchUserDialog = true
    },
    handleLoginSuccess () {
      this.showSwitchUserDialog = false
    },
    reload () {
      let search = location.search
      const addParams = new Set()
      if (this.$q.platform.is.mobile) {
        addParams.add('isMobile')
      }
      if (this.$store.getters['common/showExternalLinksAsQrCode']) {
        addParams.add('isKiosk')
      }
      if (addParams.size > 0) {
        if (!search) {
          search += '?'
        } else {
          search += '&'
        }
        let first = true
        for (const val of addParams) {
          if (!first) {
            search += '&'
          }
          search += val
          first = false
        }
      }
      window.open(location.origin + location.pathname + search, '_self')
    }
  },
  watch: {
    getAdminLevel: {
      handler (newVal, oldVal) {
        if (oldVal > newVal && this.$route.name !== 'dashboard') {
          this.$router.push({ name: 'dashboard' })
        }
      }
    }
  },
  computed: {
    ...mapGetters({
      getAdminLevel: 'auth/getAdminLevel',
      user: 'auth/getUser',
      isLoggedIn: 'auth/isLoggedIn',
      colors: 'appearance/getNormalColors',
      getProjectName: 'common/getProjectName',
      getUserCount: 'auth/getUserCount'
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
      mdiAccountBox,
      mdiPower,
      mdiAlert,
      mdiGlassCocktail,
      mdiReload,
      mdiAccountSwitchOutline
    }
  }
}
</script>

<style scoped>

</style>
