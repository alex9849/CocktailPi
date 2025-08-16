<template>
  <q-page class="page-content" padding>
    <h5 class="q-mb-md">{{ $t('page.transfer.headline') }}</h5>
    <q-card flat bordered class="bg-card-body text-card-body q-pa-md q-mb-md">
      <div class="row q-col-gutter-md">
        <div
          v-for="(button, index) in data"
          :key="index"
          class="col-12 col-md-6"
        >
          <router-link
            :to="{name: button.routeName}"
            class="no-link-format"
          >
            <q-card
              flat
              bordered
              class="q-pa-md"
              :class="{ 'bg-primary': $route.name === button.routeName, 'text-primary': $route.name === button.routeName }"
            >
              <div class="row items-center">
                <q-icon
                  :name="button.icon"
                  size="xl"
                  class="q-mr-md"
                />
                <div class="text-h6">{{ button.name }}</div>
              </div>
            </q-card>
          </router-link>
        </div>
      </div>
    </q-card>
    <router-view />
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
