<template>
  <q-layout view="hHh LpR fFf">
    <q-header class="bg-indigo-10" reveal bordered>
      <q-toolbar>
        <q-toolbar-title class="q-ma-xs">
          CocktailMaker
        </q-toolbar-title>
        <div>
          <q-btn no-caps dense class="bg-red">
            Leave view
          </q-btn>
        </div>
      </q-toolbar>
    </q-header>
    <router-view />
  </q-layout>
</template>

<style>
body {
  background-color: black;
}
</style>

<script>

export default {
  name: 'SimpleTouchCollectionLayout'
}
</script>
