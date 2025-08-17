<template>
  <q-card flat bordered class="bg-card-body text-card-body q-pa-md">
    <q-form class="q-col-gutter-md">
      <div class="row q-mb-md">
        <q-card
          flat
          bordered
          class="bg-card-item-group text-card-item-group col-12"
        >
          <q-radio
            :disable="loading"
            v-model="exportMode"
            val="all"
            label="Alle Rezepte exportieren"
            class="q-mr-md"
          />
          <q-radio
            :disable="loading"
            v-model="exportMode"
            val="selection"
            label="Nur ausgewählte Rezepte exportieren"
          />
          <q-radio
            :disable="loading"
            v-model="exportMode"
            val="none"
            label="Keine Rezepte exportieren"
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
            val="all"
            label="Alle Collections exportieren"
            class="q-mr-md"
          />
          <q-radio
            :disable="loading"
            v-model="exportCollectionsMode"
            val="selection"
            label="Nur ausgewählte Collections exportieren"
          />
          <q-radio
            :disable="loading"
            v-model="exportCollectionsMode"
            val="none"
            label="Keine Collections exportieren"
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
          label="Export herunterladen"
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

const exportMode = ref('all')
const selected = ref([])
const loading = ref(false)
const recipeLoading = ref(false)
const recipes = ref([])
const recipesLoaded = ref(false)

const exportCollectionsMode = ref('all')
const selectedCollections = ref([])
const collections = ref([])
const collectionsLoading = ref(false)
const collectionsLoaded = ref(false)

watch(exportMode, async (val) => {
  if (val === 'selection' && !recipesLoaded.value) {
    recipeLoading.value = true
    try {
      const data = await TransferService.getRecipes()
      recipes.value = data.map(r => ({
        ...r,
        ingredientCount: r.productionSteps
          .filter(s => Array.isArray(s.stepIngredients))
          .reduce((sum, s) => sum + s.stepIngredients.length, 0),
        alcoholFree: r.minAlcoholContent === 0 && r.maxAlcoholContent === 0
      }))
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
