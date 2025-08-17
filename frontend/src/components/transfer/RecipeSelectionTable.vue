<template>
  <div>
    <q-input
      :disable="disable"
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
      :columns="recipeColumns"
      row-key="id"
      dense
      selection="multiple"
      :selected="selected"
      @update:selected="$emit('update:selected', $event)"
      :filter="filter"
      :pagination="pagination"
      @update:pagination="val => pagination = val"
      title="Verfügbare Rezepte"
      no-data-label="No recipes found"
      no-results-label="No recipes found"
      :rows-per-page-options="[10, 25, 50, 100]"
    >
      <template v-slot:header-selection>
        <q-checkbox
          :model-value="allRecipesSelected"
          @update:model-value="toggleSelectAllRecipes"
          :disable="disable"
          dense
        />
      </template>
      <template v-slot:header-cell-normalName>
      </template>
      <template v-slot:body-cell-normalName>
      </template>
      <template v-slot:body-selection="props">
        <q-checkbox
          :model-value="props.selected"
          @update:modelValue="onSelect(props.row, $event)"
          dense
          :disable="disable"
        />
      </template>
      <template v-slot:body-cell-categories="props">
        <q-td>
          {{ props.row.categories.map(c => c.name).join(', ') }}
        </q-td>
      </template>
      <template v-slot:body-cell-ingredientCount="props">
        <q-td class="text-center">
          {{ numberIngredients(props.row) }}
        </q-td>
      </template>
      <template v-slot:body-cell-alcoholFree="props">
        <q-td class="text-center">
          <q-badge :color="isAlcoholFree(props.row) ? 'green' : 'grey'" align="middle">
            {{ isAlcoholFree(props.row) ? 'Ja' : 'Nein' }}
          </q-badge>
        </q-td>
      </template>
      <template v-slot:top-right>
        <q-btn
          flat
          dense
          :icon="allVisibleRecipesSelected ? 'deselect' : 'select_all'"
          :label="allVisibleRecipesSelected ? 'Alle auf Seite abwählen' : 'Alle auf Seite auswählen'"
          @click="toggleSelectAllVisibleRecipes"
          v-if="filteredRecipes.length > 0"
        />
      </template>
    </q-table>
  </div>
</template>

<script setup>
import { ref, computed, toRefs, watch } from 'vue'

const props = defineProps({
  recipes: Array,
  selected: Array,
  disable: Boolean,
  recipeLoading: Boolean
})

const emit = defineEmits([
  'update:selected'
])

const recipeColumns = [
  { name: 'name', label: 'Name', field: 'name', align: 'left' },
  { name: 'normalName', label: 'normalName', field: 'normalName', align: 'left' },
  { name: 'categories', label: 'Kategorien', field: 'categories', align: 'left' },
  { name: 'ingredientCount', label: 'Zutaten', field: 'ingredientCount', align: 'center' },
  { name: 'alcoholFree', label: 'Alkoholfrei', field: 'alcoholFree', align: 'center' }
]

const { recipes, selected, disable, recipeLoading } = toRefs(props)

const filter = ref('')
const pagination = ref({ page: 1, rowsPerPage: 25 })

const filteredRecipes = computed(() => {
  let rows = recipes.value
  if (filter.value) {
    const f = filter.value.toLowerCase()
    rows = rows.filter(r =>
      r.name.toLowerCase().includes(f) ||
      r.normalName.toLowerCase().includes(f) ||
      r.categories.some(c => c.name.toLowerCase().includes(f))
    )
  }
  const start = (pagination.value.page - 1) * pagination.value.rowsPerPage
  const end = start + pagination.value.rowsPerPage
  return rows.slice(start, end)
})

const allVisibleRecipesSelected = computed(() => {
  const visibleIds = filteredRecipes.value.map(r => r.id)
  return visibleIds.every(id => selected.value.some(s => s.id === id)) && visibleIds.length > 0
})

function toggleSelectAllVisibleRecipes () {
  const visibleIds = filteredRecipes.value.map(r => r.id)
  if (allVisibleRecipesSelected.value) {
    emit('update:selected', selected.value.filter(r => !visibleIds.includes(r.id)))
  } else {
    emit('update:selected', [
      ...selected.value,
      ...recipes.value.filter(r => visibleIds.includes(r.id) && !selected.value.includes(r))
    ])
  }
}

function isAlcoholFree (recipe) {
  return recipe.minAlcoholContent === 0 && recipe.maxAlcoholContent === 0
}

function numberIngredients (recipe) {
  return recipe.productionSteps
    .filter(s => Array.isArray(s.stepIngredients))
    .reduce((sum, s) => sum + s.stepIngredients.length, 0)
}

function onSelect (recipe, newValue) {
  if (newValue) {
    emit('update:selected', [...selected.value, recipe])
  } else {
    emit('update:selected', selected.value.filter(c => c.id !== recipe.id))
  }
}

const allRecipesSelected = computed(() =>
  recipes.value.length > 0 &&
  recipes.value.every(r => selected.value.some(s => s.id === r.id))
)

function toggleSelectAllRecipes () {
  if (allRecipesSelected.value) {
    emit('update:selected', [])
  } else {
    emit('update:selected', [...recipes.value])
  }
}

// Sync Table selection with parent
watch(selected, (val) => {
  emit('update:selected', val)
})
</script>
