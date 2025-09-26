<template>
  <q-card flat bordered class="bg-card-body text-card-body q-pa-md">
    <q-form class="q-col-gutter-md">
      <div class="row q-mb-md">
        <q-card
          flat
          :dark="colors.cardItemGroupDark"
          bordered
          class="bg-card-item-group text-card-item-group col-12"
        >
          <q-radio
            :disable="loading"
            :dark="colors.cardItemGroupDark"
            v-model="exportMode"
            val="all"
            :label="$t('page.transfer.export.recipes.export_all')"
            class="q-mr-md"
          />
          <q-radio
            :disable="loading"
            :dark="colors.cardItemGroupDark"
            v-model="exportMode"
            val="selection"
            :label="$t('page.transfer.export.recipes.export_selection')"
          />
          <q-radio
            :disable="loading"
            :dark="colors.cardItemGroupDark"
            v-model="exportMode"
            val="none"
            :label="$t('page.transfer.export.recipes.export_none')"
            class="q-mr-md"
          />
        </q-card>
      </div>
      <transition
        enter-active-class="animated fadeIn"
        leave-active-class="animated fadeOut"
      >
        <div v-if="exportMode === 'selection'" class="q-mb-md">
          <RecipeSelectionTable
            v-if="exportMode === 'selection'"
            :recipes="recipes"
            :selected="selected"
            :disable="loading"
            :recipeLoading="recipeLoading"
            @update:selected="val => selected = val"
          />
        </div>
      </transition>
      <div class="row q-mb-md">
        <q-card
          flat
          bordered
          class="bg-card-item-group text-card-item-group col-12"
        >
          <q-radio
            :disable="loading"
            v-model="exportCollectionsMode"
            :dark="colors.cardItemGroupDark"
            val="all"
            :label="$t('page.transfer.export.collections.export_all')"
            class="q-mr-md"
          />
          <q-radio
            :disable="loading"
            v-model="exportCollectionsMode"
            :dark="colors.cardItemGroupDark"
            val="selection"
            :label="$t('page.transfer.export.collections.export_selection')"
          />
          <q-radio
            :disable="loading"
            v-model="exportCollectionsMode"
            :dark="colors.cardItemGroupDark"
            val="none"
            :label="$t('page.transfer.export.collections.export_none')"
            class="q-mr-md"
          />
        </q-card>
      </div>
      <transition
        enter-active-class="animated fadeIn"
        leave-active-class="animated fadeOut"
      >
        <div v-if="exportCollectionsMode === 'selection'" class="q-mb-md">
          <CollectionSelectionTable
            v-if="exportCollectionsMode === 'selection'"
            :collections="collections"
            :selected="selectedCollections"
            :disable="loading"
            :collectionsLoading="collectionsLoading"
            @update:selected="val => selectedCollections = val"
          />
        </div>
      </transition>
      <div class="row justify-end">
        <q-btn
          color="primary"
          :label="$t('page.transfer.export.download_btn_label')"
          @click="exportRecipes"
          :disable="!enableExportBtn"
          :loading="loading"
        />
      </div>
    </q-form>
  </q-card>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import TransferService from 'src/services/transfer.service'
import CollectionService from 'src/services/collection.service'
import RecipeSelectionTable from 'components/transfer/RecipeSelectionTable.vue'
import CollectionSelectionTable from 'components/transfer/CollectionSelectionTable.vue'
import { useStore } from 'vuex'
const store = useStore()

const colors = computed(() => store.getters['appearance/getNormalColors'])
const exportMode = ref('none')
const selected = ref([])
const loading = ref(false)
const recipeLoading = ref(false)
const recipes = ref([])
const recipesLoaded = ref(false)

const exportCollectionsMode = ref('none')
const selectedCollections = ref([])
const collections = ref([])
const collectionsLoading = ref(false)
const collectionsLoaded = ref(false)

watch(exportMode, async (val) => {
  if (val === 'selection' && !recipesLoaded.value) {
    recipeLoading.value = true
    try {
      recipes.value = await TransferService.getRecipes()
      recipesLoaded.value = true
    } finally {
      recipeLoading.value = false
    }
  }
})

watch(exportCollectionsMode, async (val) => {
  if (val === 'selection' && !collectionsLoaded.value) {
    collectionsLoading.value = true
    try {
      const data = await CollectionService.getCollections()
      collections.value = data
      collectionsLoaded.value = true
    } finally {
      collectionsLoading.value = false
    }
  }
})

const enableExportBtn = computed(() => {
  const recipesOk = exportMode.value !== 'none' && (exportMode.value === 'all' || selected.value.length > 0)
  const collectionsOk = exportCollectionsMode.value !== 'none' && (exportCollectionsMode.value === 'all' || selectedCollections.value.length > 0)
  return recipesOk || collectionsOk
})

async function exportRecipes () {
  loading.value = true
  try {
    await TransferService.exportRecipes({
      exportAllRecipes: exportMode.value === 'all',
      exportRecipeIds: exportMode.value === 'selection' ? selected.value.map(r => r.id) : [],
      exportAllCollections: exportCollectionsMode.value === 'all',
      exportCollectionIds: exportCollectionsMode.value === 'selection' ? selectedCollections.value.map(c => c.id) : []
    })
  } finally {
    loading.value = false
  }
}

</script>
