<template>
  <transition-group
    enter-active-class="animated fadeInUp"
    leave-active-class="animated fadeOutDown"
  >
    <q-footer v-if="showFooter" class="bg-indigo-10" bordered>
      <div class="row justify-around items-center q-ma-xs">
        <div
          class="col-shrink row justify-start items-center"
        >
          <p
            class="text-center"
            style="font-size: 10px; max-width: 100px"
          >
            ©2023 Alexander Liggesmeyer
          </p>
          <q-btn
            outline
            dense
            :icon="mdiPiggyBank"
            color="orange-5"
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
                       'bg-purple-4': $route.name === link.to.name,
                       'bg-grey-8': $route.name !== link.to.name
                     }"
            />
          </div>
        </div>
        <div class="col-shrink row justify-end">
          <q-btn round flat class="bg-indigo-5"
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
      <q-btn round flat class="bg-indigo-5 text-white"
             dense icon="keyboard_arrow_up"
             @click="showFooter = true"
      />
    </q-page-sticky>
  </transition>
</template>

<script>
import { mdiPiggyBank } from '@quasar/extras/mdi-v5'
import { mapMutations } from 'vuex'

export default {
  name: 'SimpleFooter',
  data: () => {
    return {
      showFooter: true,
      footerLinks: [{
        label: 'Collections',
        to: {
          name: 'simplecollections'
        }
      }, {
        label: 'Recipes',
        to: {
          name: 'simplerecipes'
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
  }
}
</script>

<style scoped>

</style>
