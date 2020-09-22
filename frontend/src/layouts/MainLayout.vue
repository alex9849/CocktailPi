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
        content-class="bg-sidebar shadow-5"
      >
        <q-list>
          <q-expansion-item
            v-for="(section, index) in sidebarItems"
            v-if="!section.onlyAdmins || isAdmin"
            :label="section.label"
            :icon="section.icon"
            :key="index"
            expand-separator
            default-opened
          >
            <q-item
              v-for="(subsecion, subindex) in section.subSections"
              v-if="!subsecion.onlyAdmins || isAdmin"
              style="padding-top: 5px; padding-bottom: 5px; min-height: 30px;"
              active-class="bg-orange-2 text-dark"
              :inset-level="0.4"
              :key="subindex"
              clickable
              @click="() => {
                if(!desktopMode) {
                  leftDrawerOpen = false;
                }
              }"
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
  import {mapGetters} from 'vuex'

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
            onlyAdmins: false,
            subSections: [
              {
                onlyAdmins: false,
                label: 'Dashboard',
                to: {name: 'dashboard'}
              }, {
                onlyAdmins: false,
                label: 'My recipes',
                to: {name: 'myrecipes'}
              }
            ]
          }, {
            label: 'PUBLIC COCKTAILS',
            icon: mdiEarth,
            onlyAdmins: false,
            subSections: [
              {
                label: 'Public recipes',
                to: {name: 'publicrecipes'}
              }
            ]
          }, {
            label: 'ADMINISTRATION',
            icon: mdiCogs,
            onlyAdmins: true,
            subSections: [
              {
                label: 'Users',
                onlyAdmins: true,
                to: {name: 'usermanagement'}
              }, {
                label: 'Ingredients',
                onlyAdmins: true,
                to: {name: 'ingredientmanagement'}
              }, {
                label: 'Pumps',
                onlyAdmins: true,
                to: {name: 'pumpmanagement'}
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
      ...mapGetters({
        isAdmin: 'auth/isAdmin'
      }),
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
