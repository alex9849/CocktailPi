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
              :dark="colors.itemGroupDark"
              flat
              bordered
              class="q-pa-md"
              :class="{
                'bg-primary': $route.name === button.routeName,
                'text-primary': $route.name === button.routeName,
                'bg-card-item-group': $route.name !== button.routeName,
                'text-card-item-group': $route.name !== button.routeName
              }"
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
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useStore } from 'vuex'
const { t } = useI18n()
const store = useStore()

const colors = computed(() => store.getters['appearance/getNormalColors'])

const data = ref([{
  icon: 'download',
  name: t('page.transfer.export_btn_label'),
  routeName: 'export'
}, {
  icon: 'upload',
  name: t('page.transfer.import_btn_label'),
  routeName: 'import'
}])
</script>
<style scoped>

</style>
