<template>
  <q-layout view="lHh Lpr lFf">
    <AppHeader>
      <template slot="left">
        <q-btn
          v-if="!desktopMode"
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
      :behavior="desktopMode? 'desktop':'mobile'"
      persistent
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
      <router-view/>
    </q-page-container>
  </q-layout>
</template>

<script>
  import AppHeader from "../components/AppHeader";

  export default {
    name: 'MainLayout',

    components: {AppHeader},

    data() {
      return {
        desktopModeBreakPoint: 1023,
        leftDrawerOpen: false,
        windowWidth: 0
      }
    },
    created() {
      window.addEventListener('resize', this.handleResize);
      this.handleResize();
    },
    destroyed() {
      window.removeEventListener('resize', this.handleResize);
    },
    computed: {
      desktopMode() {
        return this.windowWidth > this.desktopModeBreakPoint;
      }
    },
    methods: {
      handleResize() {
        this.windowWidth = window.innerWidth;
        if(this.windowWidth > this.desktopModeBreakPoint) {
          this.leftDrawerOpen = true;
        }
      }
    }
  }
</script>
