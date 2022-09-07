<template>
  <q-page class="page-content" padding>
    <c-pump-management/>
    <c-reverse-pumping-settings/>
  </q-page>
</template>

<script>

import CPumpManagement from 'components/CPumpManagement'
import CReversePumpingSettings from 'components/CReversePumpingSettings'

export default {
  name: 'PumpManagement',
  components: { CReversePumpingSettings, CPumpManagement }
}
</script>

<style scoped>
</style>
