<template>
  <q-page class="page-content" padding>
    <h5>Rezepte exportieren</h5>
    <q-card flat bordered class="bg-card-body text-card-body q-pa-md">
      <q-form class="q-col-gutter-md">
        <div class="row q-mb-md">
          <q-card
            flat
            bordered
            class="bg-card-item-group text-card-item-group col-12"
          >
            <q-radio
              v-model="exportMode"
              val="all"
              label="Alle Rezepte exportieren"
              class="q-mr-md"
            />
            <q-radio
              v-model="exportMode"
              val="selection"
              label="Nur ausgewählte Rezepte exportieren"
            />
          </q-card>
        </div>
        <div v-if="exportMode === 'selection'" class="q-mb-md">
          <q-input
            filled
            dense
            debounce="300"
            v-model="filter"
            placeholder="Rezepte suchen..."
            class="q-mb-sm"
            clearable
          />
          <q-table
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
            <template v-slot:top-right>
              <q-btn
                flat
                dense
                icon="select_all"
                label="Alle auf Seite auswählen"
                @click="selectAllVisible"
                v-if="filteredRows.length > 0"
              />
            </template>
          </q-table>
        </div>
        <div class="row justify-end">
          <q-btn
            color="primary"
            label="Export starten"
            @click="exportRecipes"
            :disable="exportMode === 'selection' && selected.length === 0"
          />
        </div>
      </q-form>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref, computed } from 'vue'

const exportMode = ref('all')
const selected = ref([])
const filter = ref('')
const pagination = ref({ page: 1, rowsPerPage: 25 })

const recipes = ref([
  { id: 1, name: 'Pizza Margherita', category: 'Italienisch' },
  { id: 2, name: 'Sushi', category: 'Japanisch' },
  { id: 3, name: 'Tacos', category: 'Mexikanisch' }
])

const columns = [
  { name: 'name', label: 'Name', field: 'name', align: 'left' },
  { name: 'category', label: 'Kategorie', field: 'category', align: 'left' }
]

const filteredRows = computed(() => {
  let rows = recipes.value
  if (filter.value) {
    const f = filter.value.toLowerCase()
    rows = rows.filter(r =>
      r.name.toLowerCase().includes(f) ||
      r.category.toLowerCase().includes(f)
    )
  }
  const start = (pagination.value.page - 1) * pagination.value.rowsPerPage
  const end = start + pagination.value.rowsPerPage
  return rows.slice(start, end)
})

function selectAllVisible () {
  const ids = filteredRows.value.map(r => r.id)
  selected.value = [
    ...selected.value,
    ...recipes.value.filter(r => ids.includes(r.id) && !selected.value.includes(r))
  ]
}

function exportRecipes () {
  if (exportMode.value === 'all') {
    // Alle Rezepte exportieren
  } else {
    // Nur ausgewählte Rezepte exportieren
  }
}
</script>

<style scoped>
</style>
