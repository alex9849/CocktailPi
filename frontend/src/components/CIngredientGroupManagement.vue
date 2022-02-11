<template>
  <TopButtonArranger>
    <q-btn
      :disable="loading"
      color="negative"
      label="Delete selected groups"
      no-caps
      @click="$refs.deleteDialog.openForItems(selected)"
    />
    <q-btn
      :disable="loading"
      color="positive"
      label="Create group"
      no-caps
      @click="showEditDialog(null)"
    />
    <q-btn
      :disable="loading"
      :loading="loading"
      color="info"
      label="Refresh"
      no-caps
      @click="onRefresh"
    />
  </TopButtonArranger>
  <div class="q-py-md">
    <q-table
      v-model:selected="selected"
      :columns="columns"
      :loading="loading"
      :pagination="{rowsPerPage: 10, sortBy: 'name'}"
      :rows="groups"
      :table-style="{margin: '15px'}"
      hide-no-data
      selection="multiple"
      style="background-color: #f3f3fa"
    >
      <template v-slot:body-selection="props">
        <div class="text-center">
          <q-checkbox
            v-model:model-value="props.selected"
          />
        </div>
      </template>
      <template v-slot:body-cell-alcoholContent="props">
        <q-td
          key="alcoholContent"
          :props="props"
        >
          {{ getAlcoholContentString(props.row.minAlcoholContent, props.row.maxAlcoholContent) }}
        </q-td>
      </template>
      <template v-slot:body-cell-actions="props">
        <q-td
          key="actions"
          :props="props"
          class="q-pa-md q-gutter-x-sm"
        >
          <q-btn
            :icon="mdiPencilOutline"
            :style="{backgroundColor: '#31ccec'}"
            dense
            rounded
            text-color="white"
            @click="showEditDialog(props.row)"
          >
            <q-tooltip>
              Edit
            </q-tooltip>
          </q-btn>
          <q-btn
            :icon="mdiDelete"
            color="red"
            dense
            rounded
            @click="$refs.deleteDialog.openForItems([props.row])"
          >
            <q-tooltip>
              Delete
            </q-tooltip>
          </q-btn>
        </q-td>
      </template>
      <template
        v-slot:loading
      >
        <q-inner-loading
          color="info"
          showing
        />
      </template>
    </q-table>
  </div>
</template>

<script>
import TopButtonArranger from 'components/TopButtonArranger'

export default {
  name: 'CIngredientGroupManagement',
  components: { TopButtonArranger },
  data () {
    return {
      selected: [],
      groups: [],
      columns: [
        { name: 'name', label: 'Group', field: 'name', align: 'center' },
        { name: 'alcoholContent', label: 'Alcohol content', field: 'alcoholContent', align: 'center' },
        { name: 'parentGroup', label: 'Parent group', field: 'parentGroup', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ],
      loading: false
    }
  },
  methods: {
    getAlcoholContentString (min, max) {
      if (min === max) {
        return min + '%'
      }
      return min + '-' + max + '%'
    }
  }
}
</script>

<style scoped>

</style>
