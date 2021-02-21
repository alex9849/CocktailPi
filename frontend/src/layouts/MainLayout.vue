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
            v-if="section.reqLevel <= getUser.adminLevel"
            :label="section.label"
            :icon="section.icon"
            :key="index"
            expand-separator
            default-opened
          >
            <q-item
              v-for="(subsecion, subindex) in section.subSections"
              v-if="subsecion.reqLevel <= getUser.adminLevel"
              style="padding-top: 5px; padding-bottom: 5px; min-height: 30px;"
              active-class="bg-orange-2 text-dark"
              :inset-level="0.4"
              :key="subindex"
              clickable
              :exact="subsecion.exact"
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
        <q-card
          class="bg-sidebar"
          flat
          square
        >
          <q-card-section>
            <div class="row justify-center text-h6">CocktailMaker</div>
            <div class="row justify-center text-subtitle2">©2021 Alexander Liggesmeyer</div>
            <div class="row justify-center">
              <a
                v-for="link in projectLinks"
                :href="link.link"
                target="_blank"
              >
                <q-btn
                  round
                  dense
                  flat
                  :icon="link.icon"
                />
              </a>
            </div>
            <form class="row justify-center" action="https://www.paypal.com/donate" method="post" target="_top">
              <input type="hidden" name="hosted_button_id" value="B5YNFG7WH4D3S" />
              <input type="image" src="https://www.paypalobjects.com/en_US/DK/i/btn/btn_donateCC_LG.gif" border="0" name="submit" title="PayPal - The safer, easier way to pay online!" alt="Donate with PayPal button" />
              <img alt="" border="0" src="https://www.paypal.com/en_DE/i/scr/pixel.gif" width="1" height="1" />
            </form>
          </q-card-section>
        </q-card>

      </q-drawer>

    <q-page-container>
      <router-view/>
    </q-page-container>
  </q-layout>
</template>

<style scoped>
a {
  text-decoration: none;
  color: inherit;
}
</style>

<script>
import AppHeader from "../components/AppHeader";
import {mdiAccount, mdiChevronRight, mdiCogs, mdiEarth, mdiGithub, mdiDocker, mdiLinkedin, mdiWeb } from "@quasar/extras/mdi-v5";
import {mapGetters} from 'vuex'
import CategoryService from "../services/category.service";

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
            label: 'ME',
            icon: mdiAccount,
            reqLevel: 0,
            subSections: [
              {
                reqLevel: 0,
                label: 'Dashboard',
                to: {name: 'dashboard'},
                exact: false
              }, {
                reqLevel: 0,
                label: 'My bar',
                to: {name: 'mybar'},
                exact: false
              }, {
                label: 'My recipes',
                reqLevel: 1,
                to: {name: 'myrecipes'},
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
                to: {name: 'publicrecipes'},
                exact: true,
                reqLevel: 0,
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
                to: {name: 'usermanagement'},
                exact: false
              }, {
                label: 'Ingredients',
                reqLevel: 3,
                to: {name: 'ingredientmanagement'},
                exact: false
              }, {
                label: 'Categories',
                reqLevel: 3,
                to: {name: 'categorymanagement'},
                exact: false
              }, {
                label: 'Pumps',
                reqLevel: 3,
                to: {name: 'pumpmanagement'},
                exact: false
              }
            ]
          }
        ],
        projectLinks: [{
          icon: mdiGithub,
          link: 'https://github.com/alex9849/pi-cocktail-maker/'
        }, {
          icon: mdiDocker,
          link: 'https://hub.docker.com/repository/docker/alex9849/pi-cocktailmaker/'
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
    created() {
      window.addEventListener('resize', this.handleResize);
      this.handleResize();
      this.setCategories();
      this.mdiChevronRight = mdiChevronRight;
    },
    destroyed() {
      window.removeEventListener('resize', this.handleResize);
    },
    computed: {
      ...mapGetters({
        getUser: 'auth/getUser'
      }),
      desktopMode() {
        return this.windowWidth > this.desktopModeBreakPoint;
      }
    },
    methods: {
      setCategories() {
        CategoryService.getAllCategories()
          .then(data => {
            for(let category of data) {
              this.sidebarItems[1].subSections.push({
                label: category.name,
                reqLevel: 0,
                to: {name: 'publiccategoryrecipes', params: {cid: category.id}},
                exact: true
              })
            }
          });
      },
      handleResize() {
        this.windowWidth = window.innerWidth;
        if(this.windowWidth > this.desktopModeBreakPoint) {
         this.leftDrawerOpen = true;
        }
      }
    }
  }
</script>
