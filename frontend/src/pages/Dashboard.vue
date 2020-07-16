<template>
  <q-page>
    <p>Dashboard</p>
    <q-btn @click="test" label="test"/>
    {{ getInc }}
  </q-page>
</template>

<script>
  import store from '../store/index'
  import {mapGetters, mapMutations} from "vuex";

  export default {
    name: "Dashboard",
    methods: {
      ...mapMutations({
        test: 'auth/inc'
      }),
      doIncrement() {
        store().commit('auth/inc')
      }
    },
    computed: {
      ...mapGetters({
        getInc: 'auth/getInc'
      })
    }
  }
</script>

<style scoped>

</style>
