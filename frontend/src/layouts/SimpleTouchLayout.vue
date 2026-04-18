<template>
  <q-layout view="hHh LpR fFf" class="bg-black">
    <q-header class="bg-sv-header text-sv-header shadow-3">
      <div class="row justify-between">
        <div class="col-shrink flex items-center">
          <q-toolbar-title class="q-ma-sm items-center flex">
            <q-icon
              size="md"
              :name="mdiGlassCocktail"
            />
            {{ getProjectName }}
          </q-toolbar-title>
        </div>
        <transition
          appear
          enter-active-class="animated slideInDown"
          leave-active-class="animated slideOutUp"
        >
          <div v-if="hasCocktailProgress" class="q-px-xl col-grow trapezoid-middle bg-sv-cocktailprogress row q-col-gutter-sm items-center justify-center">
            <div
              style="height: 100%"
              class="col-grow q-pl-none row items-center justify-center"
            >
              <c-cocktail-progress-bar
                rounded
                stripe
                :dark="color.cocktailProgressDark"
                class="col"
                size="31px"
              />
            </div>
            <transition
              enter-active-class="animated bounceIn"
              leave-active-class="animated bounceOut"
            >
              <div class="col-shrink"
                   v-if="$route.name !== 'simpleorderprogress'"
              >
                <q-btn
                  no-caps flat
                  dense
                  :style="{ 'backgroundColor': progressDetailsColor }"
                  :text-color="progressDetailsTextColor"
                  :label="$t('simple_header.go_to_cocktail_progress_btn_label')"
                  :to="{name: 'simpleorderprogress'}"
                />
              </div>
            </transition>
          </div>
        </transition>
        <div class="col-shrink flex items-center">
          <q-btn-dropdown
            noCaps
            label="Options"
            color="red"
          >
            <q-list
              separator
              bordered
              style="border-radius: 0px"
            >
              <q-item
                clickable
                @click="logback()"
                v-if="allowLogback"
              >
                <q-item-section
                  avatar
                >
                  <q-icon :name="mdiPower"/>
                </q-item-section>
                <q-item-section>
                  {{ $t('simple_header.logback_btn_label') }}
                </q-item-section>
              </q-item>
              <q-item
                v-else
                clickable
                @click="clickSwitchUser()"
              >
                <q-item-section avatar>
                  <q-icon :name="mdiAccountSwitchOutline"/>
                </q-item-section>
                <q-item-section>
                  {{ $t('simple_header.switch_user_btn_label') }}
                </q-item-section>
              </q-item>
              <q-item
                clickable
                @click="() => showLeaveDialog = true"
              >
                <q-item-section avatar>
                  <q-icon :name="mdiExitToApp"/>
                </q-item-section>
                <q-item-section>
                  {{ $t('simple_header.leave_sv_btn_label') }}
                </q-item-section>
              </q-item>
            </q-list>
          </q-btn-dropdown>
        </div>
      </div>
    </q-header>
    <c-question
      :ok-button-text="$t('simple_header.leave_sv_dialog.yes_btn_label')"
      ok-color="green"
      :abort-button-text="$t('simple_header.leave_sv_dialog.no_btn_label')"
      @clickOk="$router.push({name: 'dashboard'})"
      @clickAbort="showLeaveDialog = false"
      v-model:show="showLeaveDialog"
      :question="$t('simple_header.leave_sv_dialog.headline')"
    />
    <q-page-container class="bg-sv-background text-sv-background">
      <router-view />
    </q-page-container>
    <q-dialog
      v-model:model-value="showSwitchUserDialog"
    >
      <CLoginCard
        @login-success="handleLoginSuccess"
      />
    </q-dialog>
    <simple-footer />
  </q-layout>
</template>

<style scoped>
.trapezoid-middle {
  clip-path: polygon(0% 0%, 100% 0%, calc(100% - 40px) 100%, 40px 100%);
}
</style>

<script>

import SimpleFooter from 'pages/SimpleFooter'
import { mapGetters } from 'vuex'
import CCocktailProgressBar from 'components/CCocktailProgressBar'
import CQuestion from 'components/CQuestion'
import { colors } from 'quasar'
import { mdiGlassCocktail, mdiAccountSwitchOutline, mdiPower, mdiExitToApp } from '@quasar/extras/mdi-v5'
import CLoginCard from 'components/CLoginCard.vue'
import { logback } from 'src/mixins/logback'

export default {
  name: 'SimpleTouchLayout',
  components: { CLoginCard, CQuestion, CCocktailProgressBar, SimpleFooter },
  mixins: [logback],
  data: () => {
    return {
      showLeaveDialog: false,
      showSwitchUserDialog: false
    }
  },
  created () {
    this.mdiGlassCocktail = mdiGlassCocktail
    this.mdiAccountSwitchOutline = mdiAccountSwitchOutline
    this.mdiPower = mdiPower
    this.mdiExitToApp = mdiExitToApp
  },
  methods: {
    clickSwitchUser () {
      this.showSwitchUserDialog = true
    },
    handleLoginSuccess () {
      this.showSwitchUserDialog = false
    }
  },
  computed: {
    ...mapGetters({
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      color: 'appearance/getSvColors',
      getProjectName: 'common/getProjectName'
    }),
    progressDetailsColor () {
      if (this.color.cocktailProgressDark) {
        return colors.lighten(this.color.cocktailProgress, 20)
      } else {
        return colors.lighten(this.color.cocktailProgress, -10)
      }
    },
    progressDetailsTextColor () {
      if (this.color.cocktailProgressDark) {
        return 'white'
      } else {
        return 'black'
      }
    }
  }
}
</script>
