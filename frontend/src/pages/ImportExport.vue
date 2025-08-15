<template>
  <q-page class="page-content" padding>
    <h5>{{ $t('page.transfer.headline') }}</h5>
    <div class="row items-center justify-around q-col-gutter-md"
    >
      <div
        v-for="(button, index) in data"
        :key="index"
        class="col-12 col-md-5 col-lg-4"
      >
        <router-link
          :to="{name: button.routeName}"
          class="no-link-format"
        >
          <q-card class="q-pa-sm bg-card-body text-card-body">
            <q-card-section
              horizontal
              class="items-center"
            >
              <q-icon
                :name="button.icon"
                size="xl"
                class="q-pl-md"
              />
              <q-card-section class="col-grow">
                <p class="text-h4 text-center">{{ button.name }}</p>
              </q-card-section>
            </q-card-section>
          </q-card>
        </router-link>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'

const data = ref([{
  icon: 'download',
  name: 'Export',
  routeName: 'export'
}, {
  icon: 'upload',
  name: 'Import',
  routeName: 'import'
}])
</script>
<style scoped>

</style>
