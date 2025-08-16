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

const recipesRaw = [
  {
    id: 122,
    name: '20th Century',
    categories: [{ id: 14, name: 'Classics' }, { id: 5, name: 'Gin' }],
    minAlcoholContent: 28,
    maxAlcoholContent: 28,
    productionSteps: [
      {
        type: 'addIngredients',
        stepIngredients: [
          { ingredient: { name: 'Gin' }, amount: 45 },
          { ingredient: { name: 'Lemon Juice' }, amount: 20 },
          { ingredient: { name: 'Lillet Blanc' }, amount: 20 },
          { ingredient: { name: 'Coffee Liqueur' }, amount: 15 }
        ]
      }
    ]
  },
  {
    id: 123,
    name: 'Gin Tonic',
    categories: [{ id: 5, name: 'Gin' }],
    minAlcoholContent: 15,
    maxAlcoholContent: 15,
    productionSteps: [
      {
        type: 'addIngredients',
        stepIngredients: [
          { ingredient: { name: 'Gin' }, amount: 50 },
          { ingredient: { name: 'Tonic Water' }, amount: 150 }
        ]
      }
    ]
  },
  {
    id: 124,
    name: 'Virgin Colada',
    categories: [{ id: 7, name: 'Alkoholfrei' }],
    minAlcoholContent: 0,
    maxAlcoholContent: 0,
    productionSteps: [
      {
        type: 'addIngredients',
        stepIngredients: [
          { ingredient: { name: 'Ananassaft' }, amount: 100 },
          { ingredient: { name: 'Kokosmilch' }, amount: 50 },
          { ingredient: { name: 'Sahne' }, amount: 20 }
        ]
      }
    ]
  }
]

const recipes = ref(
  recipesRaw.map(r => ({
    ...r,
    ingredientCount: r.productionSteps
      .filter(s => Array.isArray(s.stepIngredients))
      .reduce((sum, s) => sum + s.stepIngredients.length, 0),
    alcoholFree: r.minAlcoholContent === 0 && r.maxAlcoholContent === 0
  }))
)

const columns = [
  { name: 'name', label: 'Name', field: 'name', align: 'left' },
  { name: 'categories', label: 'Kategorien', field: 'categories', align: 'left' },
  { name: 'ingredientCount', label: 'Zutaten', field: 'ingredientCount', align: 'center' },
  { name: 'alcoholFree', label: 'Alkoholfrei', field: 'alcoholFree', align: 'center' }
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

const allVisibleSelected = computed(() => {
  const visibleIds = filteredRows.value.map(r => r.id)
  return visibleIds.every(id => selected.value.some(s => s.id === id)) && visibleIds.length > 0
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

function exportRecipes () {
  if (exportMode.value === 'all') {
    // Alle Rezepte exportieren
  } else {
    // Nur ausgewählte Rezepte exportieren
  }
}
</script>
