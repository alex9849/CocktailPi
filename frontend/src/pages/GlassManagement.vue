<template>
  <q-page
    padding
    class="page-content"
  >
    <h5>Glass Management</h5>
    <TopButtonArranger>
      <q-btn
        color="positive"
        label="Create category"
        :disable="loading"
        @click="showEditDialog(null)"
        no-caps
      />
      <q-btn
        color="info"
        label="Refresh"
        :disable="loading"
        :loading="loading"
        @click="onRefresh"
        no-caps
      />
    </TopButtonArranger>
    <div class="q-py-md">
      <q-table
        :columns="columns"
        :loading="loading"
        :pagination="{rowsPerPage: 10, sortBy: 'name'}"
        :rows="glasses"
        hide-no-data
      >
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
      </q-table>
      <c-delete-warning
        ref="deleteDialog"
        :delete-method="deleteGlass"
        :list-point-method="x => x.name"
        item-name-plural="glasses"
        item-name-singular="glass"
        @deleteFailure="fetchAll"
        @deleteSuccess="onDeleteSuccess"
      />
    </div>
  </q-page>
</template>

<script>

import TopButtonArranger from 'components/TopButtonArranger.vue'
import { mdiDelete, mdiPencilOutline } from '@quasar/extras/mdi-v5'
import CDeleteWarning from 'components/CDeleteWarning.vue'

export default {
  name: 'GlassManagement',
  components: { CDeleteWarning, TopButtonArranger },
  beforeRouteEnter (to, from, next) {
    next()
  },
  data: () => {
    return {
      glasses: [{
        name: 'Jumbo',
        size: 250
      }],
      columns: [
        { name: 'name', label: 'Name', field: 'name', align: 'center' },
        { name: 'size', label: 'Size', field: 'size', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ],
      loading: false
    }
  },
  created () {
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
  },
  methods: {
    showEditDialog (glass) {

    },
    onRefresh () {

    },
    fetchAll () {

    },
    onDeleteSuccess () {

    },
    deleteGlass (id) {

    }
  }
}
</script>

<style scoped>

</style>
