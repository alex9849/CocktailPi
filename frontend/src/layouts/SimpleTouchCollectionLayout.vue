<template>
  <q-layout view="hHh LpR fFf" class="bg-black">
    <q-header class="bg-indigo-10" bordered>
      <q-toolbar>
        <q-toolbar-title class="q-ma-xs">
          CocktailMaker
        </q-toolbar-title>
        <div>
          <q-btn no-caps dense class="bg-red" :to="{name: 'dashboard'}">
            Leave local-view
          </q-btn>
        </div>
      </q-toolbar>
    </q-header>
    <q-page-container>
      <q-page padding>
        <router-view />
        <div class="row justify-evenly">
          <div class="col-3 q-ma-lg q-pa-md rounded-borders text-center"
               v-for="i in 20"
               :class="[recipeState()]"
               style="cursor: grabbing"
          >
            Recipe {{ i }}
          </div>
        </div>
      </q-page>
    </q-page-container>
    <transition
      enter-active-class="animated fadeInUp"
      leave-active-class="animated fadeOutDown"
    >
      <q-footer v-if="showFooter" class="bg-indigo-10" bordered>
        <div class="row justify-evenly items-center q-ma-xs">
          <p
            class="col-1 text-center text-weight-thin"
            style="font-size: 10px"
          >
            ©2022 Alexander Liggesmeyer
          </p>
          <div class="col-10">
            <div class="row justify-evenly">
              <q-btn no-caps dense flat unelevated class="bg-purple-4 col-2">Collections</q-btn>
              <q-btn no-caps dense class="bg-grey-8 col-2">Recipes</q-btn>
              <q-btn no-caps dense class="bg-grey-8 col-2">Bar</q-btn>
            </div>
          </div>
          <div class="col-1 row justify-end">
            <q-btn round flat class="bg-indigo-5"
                   dense icon="keyboard_arrow_down"
                   @click="showFooter = false"
            />
          </div>
        </div>
      </q-footer>
    </transition>
    <transition
      enter-active-class="animated fadeInUp"
      leave-active-class="animated fadeOutDown"
    >
      <q-page-sticky v-if="!showFooter" position="bottom-right" :offset="[3, 3]">
        <q-btn round flat class="bg-indigo-5 text-white"
               dense icon="keyboard_arrow_up"
               @click="showFooter = true"
        />
      </q-page-sticky>
    </transition>
  </q-layout>
</template>

<style scoped>
</style>

<script>

export default {
  name: 'SimpleTouchCollectionLayout',
  data: () => {
    return {
      showFooter: true
    }
  },
  methods: {
    recipeState () {
      const random = Math.random()
      if (random < 0.3) {
        return 'bg-green'
      } else if (random < 0.6) {
        return 'bg-warning'
      } else {
        return 'bg-grey'
      }
    }
  }
}
</script>
