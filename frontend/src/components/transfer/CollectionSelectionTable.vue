<template>
  <div>
    <q-input
      :dark="colors.cardItemGroupDark"
      :disable="disable"
      filled
      dense
      debounce="300"
      v-model="filter"
      :placeholder="$t('component.collection_selection_table.search_label')"
      class="q-mb-sm"
      clearable
    />
    <q-table
      :dark="colors.cardItemGroupDark"
      :rows="collections"
      :columns="collectionColumns"
      row-key="id"
      dense
      selection="multiple"
      :selected="selected"
      @update:selected="$emit('update:selected', $event)"
      :filter="filter"
      :pagination="pagination"
      @update:pagination="val => pagination = val"
      :title="$t('component.collection_selection_table.available_collections')"
      :no-data-label="$t('component.collection_selection_table.no_collections_found')"
      :no-results-label="$t('component.collection_selection_table.no_collections_found')"
      :rows-per-page-options="[10, 25, 50, 100]"
      :loading="collectionsLoading"
    >
      <template v-slot:header-selection>
        <q-checkbox
          :dark="colors.cardItemGroupDark"
          :model-value="allCollectionsSelected"
          @update:model-value="toggleSelectAllCollections"
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
          :dark="colors.cardItemGroupDark"
          :model-value="props.selected"
          @update:modelValue="onSelect(props.row, $event)"
          dense
          :disable="disable"
        />
      </template>
      <template v-slot:top-right>
        <q-btn
          flat
          dense
          :icon="allVisibleCollectionsSelected ? 'deselect' : 'select_all'"
          :label="allVisibleCollectionsSelected ? $t('component.collection_selection_table.page_deselect_all') : $t('component.collection_selection_table.page_select_all')"
          @click="toggleSelectAllVisibleCollections"
          v-if="filteredCollections.length > 0"
        />
      </template>
    </q-table>
  </div>
</template>

<script setup>
import { ref, computed, toRefs, watch } from 'vue'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { useStore } from 'vuex'
const store = useStore()

const colors = computed(() => store.getters['appearance/getNormalColors'])

const props = defineProps({
  collections: Array,
  selected: Array,
  disable: Boolean,
  collectionsLoading: Boolean
})

const emit = defineEmits([
  'update:selected'
])

const { collections, selected, disable, collectionsLoading } = toRefs(props)

const filter = ref('')
const pagination = ref({ page: 1, rowsPerPage: 25 })

const collectionColumns = [
  { name: 'name', label: t('component.collection_selection_table.col.name'), field: 'name', align: 'left' },
  { name: 'normalName', label: 'normalName', field: 'normalName', align: 'left' },
  { name: 'description', label: t('component.collection_selection_table.col.description'), field: 'description', align: 'left' },
  { name: 'size', label: t('component.collection_selection_table.col.size'), field: 'size', align: 'center' }
]

const filteredCollections = computed(() => {
  let rows = collections.value
  if (filter.value) {
    const f = filter.value.toLowerCase()
    rows = rows.filter(c =>
      c.name.toLowerCase().includes(f) ||
      c.normalName.toLowerCase().includes(f) ||
      (c.description && c.description.toLowerCase().includes(f))
    )
  }
  const start = (pagination.value.page - 1) * pagination.value.rowsPerPage
  const end = start + pagination.value.rowsPerPage
  return rows.slice(start, end)
})

const allVisibleCollectionsSelected = computed(() => {
  const visibleIds = filteredCollections.value.map(c => c.id)
  return visibleIds.every(id => selected.value.some(s => s.id === id)) && visibleIds.length > 0
})

function toggleSelectAllVisibleCollections () {
  const visibleIds = filteredCollections.value.map(c => c.id)
  if (allVisibleCollectionsSelected.value) {
    emit('update:selected', selected.value.filter(c => !visibleIds.includes(c.id)))
  } else {
    emit('update:selected', [
      ...selected.value,
      ...collections.value.filter(c => visibleIds.includes(c.id) && !selected.value.includes(c))
    ])
  }
}

function onSelect (collection, newValue) {
  if (newValue) {
    emit('update:selected', [...selected.value, collection])
  } else {
    emit('update:selected', selected.value.filter(c => c.id !== collection.id))
  }
}

const allCollectionsSelected = computed(() =>
  collections.value.length > 0 &&
  collections.value.every(c => selected.value.some(s => s.id === c.id))
)

function toggleSelectAllCollections () {
  if (allCollectionsSelected.value) {
    emit('update:selected', [])
  } else {
    emit('update:selected', [...collections.value])
  }
}

watch(selected, (val) => {
  emit('update:selected', val)
})
</script>
