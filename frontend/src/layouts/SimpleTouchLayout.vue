<template>
  <q-layout view="hHh LpR fFf" class="bg-black">
    <q-header class="bg-indigo-10">
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
      <router-view />
    </q-page-container>
    <simple-footer />
  </q-layout>
</template>

<style scoped>
</style>

<script>

import SimpleFooter from 'pages/SimpleFooter'
export default {
  name: 'SimpleTouchLayout',
  components: { SimpleFooter }
}
</script>
