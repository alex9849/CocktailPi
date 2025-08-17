<template>
  <q-card flat bordered class="bg-card-body text-card-body q-pa-md">
    <q-stepper v-model="step" flat animated vertical>
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
      <q-step :name="2" title="Import-Auswahl" icon="list_alt" :done="step > 2">
        <div v-if="importData">
          <div class="q-gutter-md">
            <q-card flat bordered class="q-pa-md" v-if="importData.recipes && importData.recipes.length">
              <div class="text-h6 q-mb-sm">Rezepte</div>
              <div class="q-gutter-y-sm">
                <q-radio v-model="importRecipesMode" val="all" label="Alle Rezepte importieren" />
                <q-radio v-model="importRecipesMode" val="selection" label="Nur ausgewählte Rezepte importieren" />
                <q-radio v-model="importRecipesMode" val="none" label="Keine Rezepte importieren" />
              </div>
              <transition
                enter-active-class="animated fadeIn"
                leave-active-class="animated fadeOut"
              >
                <div v-if="importRecipesMode === 'selection'" class="q-mt-md">
                  <RecipeSelectionTable
                    :recipes="importData.recipes"
                    :selected="selectedRecipes"
                    :disable="loading"
                    :recipeLoading="false"
                    @update:selected="val => selectedRecipes = val"
                  />
                </div>
              </transition>
            </q-card>

            <q-card flat bordered class="q-pa-md" v-if="importData.collections && importData.collections.length">
              <div class="text-h6 q-mb-sm">Collections</div>
              <div class="q-gutter-y-sm">
                <q-radio v-model="importCollectionsMode" val="all" label="Alle Collections importieren" />
                <q-radio v-model="importCollectionsMode" val="selection" label="Nur ausgewählte Collections importieren" />
                <q-radio v-model="importCollectionsMode" val="none" label="Keine Collections importieren" />
              </div>
              <transition
                enter-active-class="animated fadeIn"
                leave-active-class="animated fadeOut"
              >
                <div v-if="importCollectionsMode === 'selection'" class="q-mt-md">
                  <CollectionSelectionTable
                    :collections="importData.collections"
                    :selected="selectedCollections"
                    :disable="loading"
                    :collectionsLoading="false"
                    @update:selected="val => selectedCollections = val"
                  />
                </div>
              </transition>
            </q-card>

            <q-card flat bordered class="q-pa-md" v-if="importData.glasses && importData.glasses.length">
              <div class="text-h6 q-mb-sm">Gläser</div>
              <div class="q-gutter-y-sm">
                <q-radio v-model="importGlassesMode" val="all" label="Gläser importieren" />
                <q-radio v-model="importGlassesMode" val="none" label="Keine Gläser importieren" />
              </div>
            </q-card>

            <q-card flat bordered class="q-pa-md" v-if="importData.categories && importData.categories.length">
              <div class="text-h6 q-mb-sm">Kategorien</div>
              <div class="q-gutter-y-sm">
                <q-radio v-model="importCategoriesMode" val="all" label="Kategorien importieren" />
                <q-radio v-model="importCategoriesMode" val="none" label="Keine Kategorien importieren" />
              </div>
            </q-card>

            <q-card flat bordered class="q-pa-md">
              <div class="text-h6 q-mb-sm">Duplikat-Strategie</div>
              <div class="q-gutter-y-sm">
                <q-radio v-model="duplicateMode" val="overwrite" label="Duplikate überschreiben" />
                <q-radio v-model="duplicateMode" val="skip" label="Duplikate überspringen" />
                <q-radio v-model="duplicateMode" val="keep_both" label="Beide behalten" />
              </div>
            </q-card>
          </div>
          <div class="row justify-end q-mt-lg">
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
      <q-step :name="3" title="Fertig" icon="check_circle" :done="false">
        <q-banner class="bg-green-2 text-green-10 q-mb-md" rounded>
          <div class="row items-center">
            <q-icon name="check_circle" size="xl" color="green" class="q-mr-sm" />
            <p class="text-h6">Der Import war erfolgreich!</p>
          </div>
        </q-banner>
      </q-step>
    </q-stepper>
  </q-card>
</template>

<script setup>
import { ref, computed } from 'vue'
import TransferService from 'src/services/transfer.service'
import RecipeSelectionTable from 'components/transfer/RecipeSelectionTable.vue'
import CollectionSelectionTable from 'components/transfer/CollectionSelectionTable.vue'

const step = ref(1)
const file = ref(null)
const loading = ref(false)
const duplicateMode = ref('overwrite')
const importData = ref(null)

const importRecipesMode = ref('all')
const importCollectionsMode = ref('all')
const selectedRecipes = ref([])
const selectedCollections = ref([])
const importGlassesMode = ref('all')
const importCategoriesMode = ref('all')

const enableImportBtn = computed(() => {
  const recipesOk = importRecipesMode.value !== 'none' && (importRecipesMode.value === 'all' || selectedRecipes.value.length > 0)
  const collectionsOk = importCollectionsMode.value !== 'none' && (importCollectionsMode.value === 'all' || selectedCollections.value.length > 0)
  const glassesOk = importGlassesMode.value !== 'none'
  const categoriesOk = importCategoriesMode.value !== 'none'
  return recipesOk || collectionsOk || glassesOk || categoriesOk
})

async function uploadFile () {
  loading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file.value)
    importData.value = await TransferService.uploadImportFile(formData)
    step.value = 2
    importRecipesMode.value = importData.value.recipes?.length ? 'all' : 'none'
    importCollectionsMode.value = importData.value.collections?.length ? 'all' : 'none'
    importGlassesMode.value = importData.value.glasses?.length ? 'all' : 'none'
    importCategoriesMode.value = importData.value.categories?.length ? 'all' : 'none'
    selectedRecipes.value = []
    selectedCollections.value = []
  } finally {
    loading.value = false
  }
}

async function startImport () {
  loading.value = true
  try {
    await TransferService.confirmImport(importData.value.importId, {
      importAllRecipes: importRecipesMode.value === 'all',
      importRecipeIds: importRecipesMode.value === 'selection' ? selectedRecipes.value.map(r => r.id) : [],
      importAllCollections: importCollectionsMode.value === 'all',
      importCollectionIds: importCollectionsMode.value === 'selection' ? selectedCollections.value.map(c => c.id) : [],
      importAllGlasses: importGlassesMode.value === 'all',
      importAllCategories: importCategoriesMode.value === 'all',
      duplicateMode: duplicateMode.value
    })
    step.value = 3
  } finally {
    loading.value = false
  }
}
</script>
