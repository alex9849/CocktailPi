<template>
  <transition-group
    enter-active-class="animated fadeInUp"
    leave-active-class="animated fadeOutDown"
  >
    <q-footer v-if="showFooter" class="bg-sv-header text-sv-header shadow-3" bordered>
      <div class="row justify-around items-center q-ma-xs">
        <div
          class="col-shrink row justify-start items-center"
        >
          <p
            class="text-center"
            style="font-size: 10px; max-width: 100px"
          >
            ©%CURRENT_YEAR% Alexander Liggesmeyer
          </p>
          <q-btn
            outline
            dense
            :icon="mdiPiggyBank"
            :style="{color: donateBtnTextColor}"
            @click="setShowDonateDialog(true)"
          >
            $
          </q-btn>
        </div>
        <div class="col-grow">
          <div class="row q-gutter-sm justify-evenly">
            <q-btn v-for="link in footerLinks"
                   :key="link.label"
                   :label="link.label"
                   :to="link.to"
                   no-caps dense
                   class="col-2"
                   :class="{
                       'bg-sv-navigation-active': $route.name === link.to.name,
                       'text-sv-navigation-active': $route.name === link.to.name,
                       'bg-sv-navigation': $route.name !== link.to.name,
                       'text-sv-navigation': $route.name !== link.to.name
                     }"
            />
          </div>
        </div>
        <div class="col-shrink row justify-end">
          <q-btn round flat class="bg-sv-navigation text-sv-navigation"
                 dense icon="keyboard_arrow_down"
                 @click="showFooter = false"
          />
        </div>
      </div>
    </q-footer>
  </transition-group>
  <transition
    enter-active-class="animated fadeInUp"
    leave-active-class="animated fadeOutDown"
  >
    <q-page-sticky v-if="!showFooter" position="bottom-right" :offset="[3, 3]">
      <q-btn round flat class="bg-sv-navigation text-sv-navigation"
             dense icon="keyboard_arrow_up"
             @click="showFooter = true"
      />
    </q-page-sticky>
  </transition>
</template>

<script>
import { mdiPiggyBank } from '@quasar/extras/mdi-v5'
import { mapGetters, mapMutations } from 'vuex'

export default {
  name: 'SimpleFooter',
  data () {
    return {
      showFooter: true,
      footerLinks: [{
        label: this.$t('simple_footer.collections_btn_label'),
        to: {
          name: 'simplecollections'
        }
      }, {
        label: this.$t('simple_footer.recipes_btn_label'),
        to: {
          name: 'simplerecipes'
        }
      }, {
        label: this.$t('simple_footer.ingredients_btn_label'),
        to: {
          name: 'simpleingredientrecipes'
        }
      }]
    }
  },
  created () {
    this.mdiPiggyBank = mdiPiggyBank
  },
  methods: {
    ...mapMutations({
      setShowDonateDialog: 'common/setShowDonateDialog'
    })
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getSvColors'
    }),
    donateBtnTextColor () {
      if (this.color.headerDark) {
        return '#fda626'
      } else {
        return '#d58100'
      }
    }
  }
}
</script>

<style scoped>

</style>
