<template>
  <q-card
    flat
    bordered
    class="bg-card-body text-card-body q-pa-md"
  >
    <q-stepper
      v-model="step"
      flat
      animated
      vertical
    >
      <q-step :name="1" title="Datei auswählen" icon="upload" :done="step > 1">
        <q-form @submit.prevent="uploadFile">
          <q-file
            v-model="file"
            label="Import-Datei auswählen (.zip)"
            accept=".zip"
            :disable="loading"
            filled
            class="q-mb-md"
          />
          <q-btn
            color="primary"
            label="Hochladen"
            type="submit"
            :loading="loading"
            :disable="!file"
          />
        </q-form>
      </q-step>
      <q-step :name="2" title="Import-Auswahl" icon="list_alt" :done="false">
        <div v-if="importData">
          <div v-if="importData.recipes && importData.recipes.length">
            <div class="row q-mb-md">
              <div class="col-6">
                <div class="q-gutter-y-sm">
                  <q-radio
                    v-model="importRecipesMode"
                    val="all"
                    label="Alle Rezepte importieren"
                  />
                  <q-radio
                    v-model="importRecipesMode"
                    val="selection"
                    label="Nur ausgewählte Rezepte importieren"
                  />
                  <q-radio
                    v-model="importRecipesMode"
                    val="none"
                    label="Keine Rezepte importieren"
                  />
                </div>
              </div>
            </div>
            <div v-if="importRecipesMode === 'selection'" class="q-mb-md">
              <q-table
                :rows="importData.recipes"
                :columns="recipeColumns"
                row-key="id"
                dense
                selection="multiple"
                v-model:selected="selectedRecipes"
                title="Gefundene Rezepte"
                :rows-per-page-options="[10, 25, 50, 100]"
              />
            </div>
          </div>
          <div v-if="importData.collections && importData.collections.length">
            <div class="row q-mb-md">
              <div class="col-6">
                <div class="q-gutter-y-sm">
                  <q-radio
                    v-model="importCollectionsMode"
                    val="all"
                    label="Alle Collections importieren"
                  />
                  <q-radio
                    v-model="importCollectionsMode"
                    val="selection"
                    label="Nur ausgewählte Collections importieren"
                  />
                  <q-radio
                    v-model="importCollectionsMode"
                    val="none"
                    label="Keine Collections importieren"
                  />
                </div>
              </div>
            </div>
            <div v-if="importCollectionsMode === 'selection'" class="q-mb-md">
              <q-table
                :rows="importData.collections"
                :columns="collectionColumns"
                row-key="id"
                dense
                selection="multiple"
                v-model:selected="selectedCollections"
                title="Gefundene Collections"
                :rows-per-page-options="[10, 25, 50, 100]"
              />
            </div>
          </div>

          <div class="row justify-end">
            <q-btn
              color="primary"
              label="Import starten"
              @click="startImport"
              :loading="loading"
              :disable="!enableImportBtn"
            />
          </div>
        </div>
        <div v-else>
          <q-banner dense class="bg-grey-2 text-grey-8">
            Bitte zuerst eine Import-Datei hochladen.
          </q-banner>
        </div>
      </q-step>
    </q-stepper>
  </q-card>
</template>

<script setup>
import { ref, computed } from 'vue'
import TransferService from 'src/services/transfer.service'

const step = ref(1)
const file = ref(null)
const loading = ref(false)
const importData = ref(null)

const importRecipesMode = ref('all')
const importCollectionsMode = ref('all')
const selectedRecipes = ref([])
const selectedCollections = ref([])

const recipeColumns = [
  { name: 'name', label: 'Name', field: 'name', align: 'left' },
  { name: 'categories', label: 'Kategorien', field: row => row.categories?.join(', '), align: 'left' }
]
const collectionColumns = [
  { name: 'name', label: 'Name', field: 'name', align: 'left' },
  { name: 'description', label: 'Beschreibung', field: 'description', align: 'left' }
]

const enableImportBtn = computed(() => {
  const recipesOk = importRecipesMode.value !== 'none' && (importRecipesMode.value === 'all' || selectedRecipes.value.length > 0)
  const collectionsOk = importCollectionsMode.value !== 'none' && (importCollectionsMode.value === 'all' || selectedCollections.value.length > 0)
  // Mindestens eins muss importiert werden
  return recipesOk || collectionsOk
})

async function uploadFile () {
  loading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file.value)
    importData.value = await TransferService.uploadImportFile(formData)
    // Server gibt z.B. { recipes: [...], collections: [...] } zurück
    step.value = 2
    importRecipesMode.value = importData.value.recipes?.length ? 'all' : 'none'
    importCollectionsMode.value = importData.value.collections?.length ? 'all' : 'none'
    selectedRecipes.value = []
    selectedCollections.value = []
  } finally {
    loading.value = false
  }
}

async function startImport () {
  loading.value = true
  try {
    /* await ImportService.startImport({
      importAllRecipes: importRecipesMode.value === 'all',
      importRecipeIds: importRecipesMode.value === 'selection' ? selectedRecipes.value.map(r => r.id) : [],
      importAllCollections: importCollectionsMode.value === 'all',
      importCollectionIds: importCollectionsMode.value === 'selection' ? selectedCollections.value.map(c => c.id) : []
    }) */
    // Nach dem Import ggf. Feedback/Navigation
  } finally {
    loading.value = false
  }
}
</script>
