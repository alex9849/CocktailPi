<template>
  <q-layout view="hHh LpR fFf" class="bg-black">
    <q-header class="bg-indigo-10">
      <div class="row justify-between">
        <div class="col-shrink bg-indigo-10 flex items-center">
          <q-toolbar-title class="q-ma-sm">CocktailMaker</q-toolbar-title>
        </div>
        <transition
          appear
          enter-active-class="animated slideInDown"
          leave-active-class="animated slideOutUp"
        >
          <div v-if="hasCocktailProgress" class="q-px-xl col-grow trapezoid-middle bg-green-10 row q-col-gutter-sm items-center justify-center">
            <div
              style="height: 100%"
              class="col-grow q-pl-none row items-center justify-center"
            >
              <c-cocktail-progress-bar
                rounded
                stripe
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
                  class="bg-green-8"
                  no-caps flat
                  dense color="white"
                  label="Go to details >>"
                  :to="{name: 'simpleorderprogress'}"
                />
              </div>
            </transition>
          </div>
        </transition>
        <div class="col-shrink bg-indigo-10 flex items-center">
          <q-btn no-caps dense class="bg-red q-ma-sm" @click="showLeaveDialog = true">
            Leave simple-view
          </q-btn>
        </div>
      </div>
    </q-header>
    <c-question
      ok-button-text="Yes"
      ok-color="green"
      abort-button-text="No"
      @clickOk="$router.push({name: 'dashboard'})"
      @clickAbort="showLeaveDialog = false"
      v-model:show="showLeaveDialog"
      question="Leave simple view?"
    />
    <q-page-container>
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
import CSimpleHeaderProgressStatus from 'components/CSimpleHeaderProgressStatus'
import { mapGetters } from 'vuex'
import CCocktailProgressBar from 'components/CCocktailProgressBar'
import CQuestion from 'components/CQuestion'
export default {
  name: 'SimpleTouchLayout',
  components: { CQuestion, CCocktailProgressBar, CSimpleHeaderProgressStatus, SimpleFooter },
  data: () => {
    return {
      showLeaveDialog: false
    }
  },
  computed: {
    ...mapGetters({
      hasCocktailProgress: 'cocktailProgress/hasCocktailProgress'
    })
  }
}
</script>
