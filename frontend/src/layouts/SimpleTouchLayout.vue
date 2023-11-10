<template>
  <q-layout view="hHh LpR fFf" class="bg-black">
    <q-header class="bg-sv-header text-sv-header shadow-3">
      <div class="row justify-between">
        <div class="col-shrink flex items-center">
          <q-toolbar-title class="q-ma-sm">
            {{ $t('simple_header.machine_name') }}
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
          <q-btn no-caps dense color="red" class="q-ma-sm" @click="showLeaveDialog = true">
            {{ $t('simple_header.leave_sv_btn_label') }}
          </q-btn>
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

export default {
  name: 'SimpleTouchLayout',
  components: { CQuestion, CCocktailProgressBar, SimpleFooter },
  data: () => {
    return {
      showLeaveDialog: false
    }
  },
  computed: {
    ...mapGetters({
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress',
      color: 'appearance/getSvColors'
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
