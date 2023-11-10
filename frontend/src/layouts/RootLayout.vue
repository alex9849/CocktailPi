<template>
  <q-layout>
    <q-page-container>
        <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { mapMutations } from 'vuex'
import store from '../store'

export default {
  name: 'RootLayout',
  beforeRouteEnter (to, from, next) {
    store().dispatch('common/fetchAppearanceSettings')
      .then(x => next())
  },
  created () {
    let isKiosk = Object.hasOwn(this.$route.query, 'isKiosk')
    let isMobile = Object.hasOwn(this.$route.query, 'isMobile')
    if (this.$route.redirectedFrom) {
      isKiosk |= Object.hasOwn(this.$route.redirectedFrom.query, 'isKiosk')
      isMobile |= Object.hasOwn(this.$route.redirectedFrom.query, 'isMobile')
    }
    this.setShowExternalLinksAsQrCode(!!isKiosk)
    if (isMobile) {
      this.$q.platform.is.mobile = true
    }
  },
  methods: {
    ...mapMutations({
      setShowExternalLinksAsQrCode: 'common/setShowExternalLinksAsQrCode'
    })
  }
}
</script>

<style scoped>

</style>
