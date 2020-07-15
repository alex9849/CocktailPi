<template>
  <q-layout view="lHh Lpr lFf">
    <AppHeader>
      <template slot="left">
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="leftDrawerOpen = !leftDrawerOpen"
        />
      </template>
    </AppHeader>
    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered
      content-class="bg-grey-1"
    >
      <q-list>
        <q-item-label
          header
          class="text-grey-8"
        >
          Essential Links
        </q-item-label>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
  import AppHeader from "../components/AppHeader";

  export default {
  name: 'MainLayout',

  components: {AppHeader},

  data () {
    return {
      leftDrawerOpen: false
    }
  }
}
</script>
