<template>
  <transition-group
    enter-active-class="animated fadeInUp"
    leave-active-class="animated fadeOutDown"
  >
    <q-footer v-if="showFooter" class="bg-indigo-10" bordered>
      <div class="row justify-evenly items-center q-ma-xs">
        <p
          class="col-1 text-center text-weight-thin"
          style="font-size: 10px"
        >
          ©2022 Alexander Liggesmeyer
        </p>
        <div class="col-10">
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
        <div class="col-1 row justify-end">
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
      }, {
        label: 'Bar',
        to: {
          name: 'bar'
        }
      }]
    }
  }
}
</script>

<style scoped>

</style>
