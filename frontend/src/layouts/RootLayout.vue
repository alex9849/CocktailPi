<template>
  <q-layout>
    <q-page-container>
        <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { mapMutations } from 'vuex'

export default {
  name: 'RootLayout',
  created () {
    const isKiosk = Object.hasOwn(this.$route.query, 'isKiosk')
    this.setShowExternalLinksAsQrCode((isKiosk))
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
