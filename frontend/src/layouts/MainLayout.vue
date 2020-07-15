<template>
  <q-layout view="hHh LpR fFf">
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
        :width="250"
        v-model="leftDrawerOpen"
        :behavior="desktopMode? 'desktop':'mobile'"
        persistent
        bordered
        content-class="bg-grey-1"
      >
        <q-list>
          <q-expansion-item
            v-for="section in sidebarItems"
            :label="section.label"
            :icon="section.icon"
            expand-separator
            default-opened
          >
            <q-item
              v-for="subsecion in section.subSections"
              style="padding-top: 5px; padding-bottom: 5px; min-height: 30px;"
              active-class="bg-orange-2 text-dark"
              :inset-level="0.4"
              exact
              clickable
              :to="subsecion.to"
            >
              <q-item-section avatar style="min-width: 0">
                <q-icon :name="mdiChevronRight"/>
              </q-item-section>
              <q-item-section>
                {{ subsecion.label }}
              </q-item-section>
            </q-item>
          </q-expansion-item>
        </q-list>
      </q-drawer>

    <q-page-container>
      <router-view/>
    </q-page-container>
  </q-layout>
</template>

<style scoped>
</style>

<script>
  import AppHeader from "../components/AppHeader";
  import {mdiAccount, mdiChevronRight, mdiCogs, mdiEarth} from "@quasar/extras/mdi-v5";

  export default {
    name: 'MainLayout',

    components: {AppHeader},

    data() {
      return {
        desktopModeBreakPoint: 1023,
        leftDrawerOpen: false,
        windowWidth: 0,
        sidebarItems: [
          {
            label: 'GENERAL',
            icon: mdiAccount,
            subSections: [
              {
                label: 'Dashboard',
                to: {name: 'dashboard'}
              }, {
                label: 'My recipes',
                to: {name: 'myRecipes'}
              }, {
                label: 'Favorites',
                to: {name: 'myFavorites'}
              }
            ]
          }, {
            label: 'PUBLIC COCKTAILS',
            icon: mdiEarth,
            subSections: [
              {
                label: 'Search',
                to: {name: 'cocktailSearch'}
              }, {
                label: 'Public recipes',
                to: {name: 'publicRecipes'}
              }
            ]
          }, {
            label: 'ADMINISTRATION',
            icon: mdiCogs,
            subSections: [
              {
                label: 'Users',
                to: {name: 'adminUsers'}
              }, {
                label: 'Settings',
                to: {name: 'adminSettings'}
              }
            ]
          }
        ]
      }
    },
    created() {
      window.addEventListener('resize', this.handleResize);
      this.handleResize();
      this.mdiChevronRight = mdiChevronRight;
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
