<template>
  <q-layout>
    <q-page-container>
        <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { mapActions, mapMutations } from 'vuex'

export default {
  name: 'RootLayout',
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
    this.fetchAppearance(this.$i18n)
  },
  methods: {
    ...mapMutations({
      setShowExternalLinksAsQrCode: 'common/setShowExternalLinksAsQrCode'
    }),
    ...mapActions({
      fetchAppearance: 'common/fetchAppearanceSettings'
    })
  }
}
</script>

<style scoped>

</style>
