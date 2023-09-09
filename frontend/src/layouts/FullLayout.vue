<template>
  <q-layout view="hHh LpR fFf">
    <AppHeader>
      <template v-slot:left>
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
      class="bg-sidebar shadow-5"
    >
      <q-list>
        <q-expansion-item
          v-for="(section, index) in permittedSidebarItems"
          :label="section.label"
          :icon="section.icon"
          :key="index"
          expand-separator
          default-opened
        >
          <q-item
            v-for="(subsection, subindex) in section.subSections"
            style="padding-top: 5px; padding-bottom: 5px; min-height: 30px;"
            active-class="bg-orange-2 text-dark"
            :inset-level="0.4"
            :key="subindex"
            clickable
            :exact="subsection.exact"
            @click="() => {
                if(!desktopMode) {
                  leftDrawerOpen = false;
                }
              }"
            :to="subsection.to"
          >
            <q-item-section avatar style="min-width: 0">
              <q-icon :name="mdiChevronRight"/>
            </q-item-section>
            <q-item-section>
              {{ subsection.label }}
            </q-item-section>
          </q-item>
        </q-expansion-item>
      </q-list>
      <q-card
        class="bg-sidebar"
        flat
        square
      >
        <q-card-section>
          <div class="row justify-center text-h6">CocktailMaker</div>
          <div class="row justify-center text-subtitle1">
            v%MAVEN_PROJECT_VERSION%
          </div>
          <div class="row justify-center text-subtitle2">©%CURRENT_YEAR% Alexander Liggesmeyer</div>
          <div class="row justify-center">
            <q-btn
              v-for="link in projectLinks"
              :key="link.link"
              round
              dense
              flat
              @click="openExternalLink(link.link)"
              :icon="link.icon"
            />
          </div>
          <div class="row justify-center">
            <q-btn
              label="Donate"
              :icon="mdiPiggyBank"
              color="orange-5"
              @click="clickDonate"
            />
          </div>
        </q-card-section>
      </q-card>

    </q-drawer>

    <q-page-container class="justify-center">
      <router-view/>
    </q-page-container>
  </q-layout>
</template>

<script>
import AppHeader from '../components/AppHeader'
import {
  mdiAccount,
  mdiChevronRight,
  mdiCogs,
  mdiEarth,
  mdiGithub,
  mdiLinkedin,
  mdiPiggyBank,
  mdiWeb
} from '@quasar/extras/mdi-v5'
import { mapGetters, mapMutations } from 'vuex'

export default {
  name: 'FullLayout',

  components: { AppHeader },

  data () {
    return {
      desktopModeBreakPoint: 1024,
      leftDrawerOpen: false,
      windowWidth: 0,
      sidebarItems: [
        {
          label: 'ME',
          icon: mdiAccount,
          reqLevel: 0,
          subSections: [
            {
              reqLevel: 0,
              label: 'Dashboard',
              to: { name: 'dashboard' },
              exact: false
            }, {
              reqLevel: 0,
              label: 'Simple view',
              to: { name: 'simplecollections' },
              exact: false
            }, {
              reqLevel: 0,
              label: 'Bar',
              to: { name: 'bar' },
              exact: false
            }, {
              reqLevel: 0,
              label: 'Collections',
              to: { name: 'collections' },
              exact: false
            }, {
              label: 'My recipes',
              reqLevel: 1,
              to: { name: 'myrecipes' },
              exact: false
            }
          ]
        }, {
          label: 'PUBLIC COCKTAILS',
          icon: mdiEarth,
          reqLevel: 0,
          subSections: [
            {
              label: 'All',
              to: { name: 'publicrecipes' },
              exact: true,
              reqLevel: 0
            }, {
              label: 'Ingredients',
              to: { name: 'ingredientrecipes' },
              exact: true,
              reqLevel: 0
            }
          ]
        }, {
          label: 'ADMINISTRATION',
          icon: mdiCogs,
          reqLevel: 3,
          subSections: [
            {
              label: 'Users',
              reqLevel: 3,
              to: { name: 'usermanagement' },
              exact: false
            }, {
              label: 'Ingredients',
              reqLevel: 3,
              to: { name: 'ingredientmanagement' },
              exact: false
            }, {
              label: 'Categories',
              reqLevel: 3,
              to: { name: 'categorymanagement' },
              exact: false
            }, {
              label: 'GPIO',
              reqLevel: 3,
              to: { name: 'gpiomanagement' },
              exact: false
            }, {
              label: 'Pumps',
              reqLevel: 3,
              to: { name: 'pumpmanagement' },
              exact: false
            }, {
              label: 'Glasses',
              reqLevel: 3,
              to: { name: 'glassmanagement' },
              exact: false
            }, {
              label: 'Events',
              reqLevel: 3,
              to: { name: 'eventmanagement' },
              exact: false
            }, {
              label: 'System',
              reqLevel: 3,
              to: { name: 'systemmanagement' },
              exact: false
            }
          ]
        }
      ],
      projectLinks: [{
        icon: mdiGithub,
        link: 'https://github.com/alex9849/pi-cocktail-maker/'
      }, {
        icon: mdiLinkedin,
        link: 'https://www.linkedin.com/in/alexander-liggesmeyer/'
      }, {
        icon: mdiWeb,
        link: 'https://alexander.liggesmeyer.net/'
      }
      ]
    }
  },
  created () {
    window.addEventListener('resize', this.handleResize)
    this.handleResize()
    this.mdiChevronRight = mdiChevronRight
    this.mdiPiggyBank = mdiPiggyBank
  },
  unmounted () {
    window.removeEventListener('resize', this.handleResize)
  },
  computed: {
    ...mapGetters({
      getUser: 'auth/getUser',
      getAdminLevel: 'auth/getAdminLevel',
      recipeCategories: 'category/getCategories'
    }),
    desktopMode () {
      return this.windowWidth > this.desktopModeBreakPoint
    },
    permittedSidebarItems () {
      const sidebarItems = []
      for (const item of this.sidebarItems) {
        if (item.reqLevel <= this.getAdminLevel) {
          sidebarItems.push(item)
        }
        item.subSections = item.subSections
          .filter(x => x.reqLevel <= this.getAdminLevel)
      }
      return sidebarItems
    }
  },
  watch: {
    recipeCategories: {
      immediate: true,
      handler (newValue) {
        this.setCategories(newValue)
      }
    }
  },
  methods: {
    ...mapMutations({
      setShowDonateDialog: 'common/setShowDonateDialog',
      openExternalLink: 'common/openExternalLink'
    }),
    clickDonate () {
      this.setShowDonateDialog(true)
    },
    handleResize () {
      this.windowWidth = window.innerWidth
      if (this.windowWidth > this.desktopModeBreakPoint) {
        this.leftDrawerOpen = true
      }
    },
    setCategories (categories) {
      this.sidebarItems[1].subSections = [
        {
          label: 'All',
          to: { name: 'publicrecipes' },
          exact: true,
          reqLevel: 0
        }
      ]
      for (const category of categories) {
        this.sidebarItems[1].subSections.push({
          label: category.name,
          reqLevel: 0,
          to: { name: 'publiccategoryrecipes', params: { cid: category.id } },
          exact: true
        })
      }
      this.sidebarItems[1].subSections.push({
        label: 'Ingredients',
        to: { name: 'ingredientrecipes' },
        exact: true,
        reqLevel: 0
      })
    }
  }
}
</script>

<style scoped>
a {
  text-decoration: none;
  color: inherit;
}
</style>
