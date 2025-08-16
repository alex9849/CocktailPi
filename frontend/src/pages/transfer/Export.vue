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
        </q-card>
      </div>
      <div v-if="exportMode === 'selection'" class="q-mb-md">
        <q-input
          :disable="loading"
          filled
          dense
          debounce="300"
          v-model="filter"
          placeholder="Rezepte suchen..."
          class="q-mb-sm"
          clearable
        />
        <q-table
          :loading="recipeLoading"
          loading-label="Lade Rezepte..."
          :rows="recipes"
          :columns="columns"
          row-key="id"
          dense
          selection="multiple"
          v-model:selected="selected"
          :filter="filter"
          :pagination="pagination"
          @update:pagination="val => pagination = val"
          title="Verfügbare Rezepte"
          :rows-per-page-options="[10, 25, 50, 100]"
        >
          <template v-slot:body-selection="props">
            <q-checkbox
              v-model="props.selected"
              dense
              :disable="loading"
            />
          </template>
          <template v-slot:body-cell-categories="props">
            <q-td>
              {{ props.row.categories.map(c => c.name).join(', ') }}
            </q-td>
          </template>
          <template v-slot:body-cell-ingredientCount="props">
            <q-td class="text-center">
              {{ props.row.ingredientCount }}
            </q-td>
          </template>
          <template v-slot:body-cell-alcoholFree="props">
            <q-td class="text-center">
              <q-badge :color="props.row.alcoholFree ? 'green' : 'grey'" align="middle">
                {{ props.row.alcoholFree ? 'Ja' : 'Nein' }}
              </q-badge>
            </q-td>
          </template>
          <template v-slot:top-right>
            <q-btn
              flat
              dense
              :icon="allVisibleSelected ? 'deselect' : 'select_all'"
              :label="allVisibleSelected ? 'Alle auf Seite abwählen' : 'Alle auf Seite auswählen'"
              @click="toggleSelectAllVisible"
              v-if="filteredRows.length > 0"
            />
          </template>
        </q-table>
      </div>

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
        </q-card>
      </div>
      <div v-if="exportCollectionsMode === 'selection'" class="q-mb-md">
        <q-input
          :disable="loading"
          filled
          dense
          debounce="300"
          v-model="collectionFilter"
          placeholder="Collections suchen..."
          class="q-mb-sm"
          clearable
        />
        <q-table
          :rows="collections"
          :columns="collectionColumns"
          row-key="id"
          dense
          selection="multiple"
          v-model:selected="selectedCollections"
          :filter="collectionFilter"
          :pagination="collectionPagination"
          @update:pagination="val => collectionPagination = val"
          title="Verfügbare Collections"
          :rows-per-page-options="[10, 25, 50, 100]"
        >
          <template v-slot:body-selection="props">
            <q-checkbox
              v-model="props.selected"
              dense
              :disable="loading"
            />
          </template>
          <template v-slot:top-right>
            <q-btn
              flat
              dense
              :icon="allVisibleCollectionsSelected ? 'deselect' : 'select_all'"
              :label="allVisibleCollectionsSelected ? 'Alle auf Seite abwählen' : 'Alle auf Seite auswählen'"
              @click="toggleSelectAllVisibleCollections"
              v-if="filteredCollections.length > 0"
            />
          </template>
        </q-table>
      </div>

      <div class="row justify-end">
        <q-btn
          color="primary"
          label="Export starten"
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

const exportMode = ref('all')
const selected = ref([])
const filter = ref('')
const pagination = ref({ page: 1, rowsPerPage: 25 })
const loading = ref(false)
const recipeLoading = ref(false)
const recipes = ref([])
const recipesLoaded = ref(false)

const exportCollectionsMode = ref('all')
const selectedCollections = ref([])
const collections = ref([])
const collectionsLoaded = ref(false)
const collectionFilter = ref('')
const collectionPagination = ref({ page: 1, rowsPerPage: 25 })

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
    const data = await CollectionService.getCollections()
    collections.value = data
    collectionsLoaded.value = true
  }
})

const columns = [
  { name: 'name', label: 'Name', field: 'name', align: 'left' },
  { name: 'categories', label: 'Kategorien', field: 'categories', align: 'left' },
  { name: 'ingredientCount', label: 'Zutaten', field: 'ingredientCount', align: 'center' },
  { name: 'alcoholFree', label: 'Alkoholfrei', field: 'alcoholFree', align: 'center' }
]

const collectionColumns = [
  { name: 'name', label: 'Name', field: 'name', align: 'left' },
  { name: 'description', label: 'Beschreibung', field: 'description', align: 'left' },
  { name: 'size', label: 'Rezepte', field: 'size', align: 'center' }
]

const filteredRows = computed(() => {
  let rows = recipes.value
  if (filter.value) {
    const f = filter.value.toLowerCase()
    rows = rows.filter(r =>
      r.name.toLowerCase().includes(f) ||
      r.categories.some(c => c.name.toLowerCase().includes(f))
    )
  }
  const start = (pagination.value.page - 1) * pagination.value.rowsPerPage
  const end = start + pagination.value.rowsPerPage
  return rows.slice(start, end)
})

const filteredCollections = computed(() => {
  let rows = collections.value
  if (collectionFilter.value) {
    const f = collectionFilter.value.toLowerCase()
    rows = rows.filter(c =>
      c.name.toLowerCase().includes(f) ||
      (c.description && c.description.toLowerCase().includes(f))
    )
  }
  const start = (collectionPagination.value.page - 1) * collectionPagination.value.rowsPerPage
  const end = start + collectionPagination.value.rowsPerPage
  return rows.slice(start, end)
})

const enableExportBtn = computed(() => {
  return exportMode.value === 'all' || selected.value.length > 0 ||
         exportCollectionsMode.value === 'all' || selectedCollections.value.length > 0
})

const allVisibleSelected = computed(() => {
  const visibleIds = filteredRows.value.map(r => r.id)
  return visibleIds.every(id => selected.value.some(s => s.id === id)) && visibleIds.length > 0
})

const allVisibleCollectionsSelected = computed(() => {
  const visibleIds = filteredCollections.value.map(c => c.id)
  return visibleIds.every(id => selectedCollections.value.some(s => s.id === id)) && visibleIds.length > 0
})

function toggleSelectAllVisible () {
  const visibleIds = filteredRows.value.map(r => r.id)
  if (allVisibleSelected.value) {
    // Abwählen
    selected.value = selected.value.filter(r => !visibleIds.includes(r.id))
  } else {
    // Auswählen
    selected.value = [
      ...selected.value,
      ...recipes.value.filter(r => visibleIds.includes(r.id) && !selected.value.includes(r))
    ]
  }
}

function toggleSelectAllVisibleCollections () {
  const visibleIds = filteredCollections.value.map(c => c.id)
  if (allVisibleCollectionsSelected.value) {
    selectedCollections.value = selectedCollections.value.filter(c => !visibleIds.includes(c.id))
  } else {
    selectedCollections.value = [
      ...selectedCollections.value,
      ...collections.value.filter(c => visibleIds.includes(c.id) && !selectedCollections.value.includes(c))
    ]
  }
}

async function exportRecipes () {
  loading.value = true
  try {
    await TransferService.exportRecipes({
      exportAllRecipes: exportMode.value === 'all',
      exportRecipeIds: selected.value.map(r => r.id),
      exportAllCollections: exportCollectionsMode.value === 'all',
      exportCollectionIds: selectedCollections.value.map(c => c.id)
    })
  } finally {
    loading.value = false
  }
}

</script>
