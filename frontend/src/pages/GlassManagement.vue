<template>
  <q-page
    padding
    class="page-content"
  >
    <h5>Glass Management</h5>
    <TopButtonArranger>
      <q-btn
        color="positive"
        label="Create category"
        :disable="loading"
        @click="showEditDialog(null)"
        no-caps
      />
      <q-btn
        color="info"
        label="Refresh"
        :disable="loading"
        :loading="loading"
        @click="onRefresh"
        no-caps
      />
    </TopButtonArranger>
  </q-page>
</template>

<script>

import TopButtonArranger from 'components/TopButtonArranger.vue'

export default {
  name: 'GlassManagement',
  components: { TopButtonArranger }
}
</script>

<style scoped>

</style>
