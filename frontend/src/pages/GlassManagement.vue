<template>
  <q-page
    padding
    class="page-content"
  >
    <h5>Glass Management</h5>
    <TopButtonArranger>
      <q-btn
        color="positive"
        label="Create glass"
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
        <template
          v-slot:loading
        >
          <q-inner-loading
            showing
            color="info"
          />
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
        <template v-slot:body-cell-size="props">
          <q-td :props="props"
                key="nonLocked"
          >
            {{ props.row.size }} ml
          </q-td>
        </template>
        <template v-slot:body-cell-default="props">
          <q-td :props="props"
                key="nonLocked"
          >
            <q-icon
              v-if="props.row.default"
              size="sm"
              :name="mdiCheckCircle"
            />
            <q-icon
              v-else
              size="sm"
              :name="mdiCheckboxBlankCircleOutline"
            />
          </q-td>
        </template>
        <template v-slot:body-cell-useForSingleIngredients="props">
          <q-td :props="props"
                key="nonLocked"
          >
            <q-icon
              v-if="props.row.useForSingleIngredients"
              size="sm"
              :name="mdiCheckCircle"
            />
            <q-icon
              v-else
              size="sm"
              :name="mdiCheckboxBlankCircleOutline"
            />
          </q-td>
        </template>
      </q-table>
      <c-delete-warning
        ref="deleteDialog"
        :delete-method="deleteGlass"
        :list-point-method="x => x.name"
        item-name-plural="glasses"
        item-name-singular="glass"
        @deleteFailure="onRefresh"
        @deleteSuccess="onDeleteSuccess"
      />
    </div>
    <c-edit-dialog
      v-model:show="editOptions.editDialog"
      :error-message="editOptions.editErrorMessage"
      :saving="editOptions.saving"
      :title="editOptions.editDialogHeadline"
      :valid="editOptions.valid"
      @clickAbort="closeEditDialog"
      @clickSave="onClickEditDialogSave"
    >
      <c-glass-form
        v-model:model-value="editOptions.editGlass"
        @invalid="editOptions.valid = !$event"
      />
    </c-edit-dialog>
  </q-page>
</template>

<script>

import TopButtonArranger from 'components/TopButtonArranger.vue'
import { mdiCheckboxBlankCircleOutline, mdiCheckCircle, mdiDelete, mdiPencilOutline } from '@quasar/extras/mdi-v5'
import CDeleteWarning from 'components/CDeleteWarning.vue'
import GlassService from 'src/services/glass.service'
import CEditDialog from 'components/CEditDialog.vue'
import CGlassForm from 'components/CGlassForm.vue'

export default {
  name: 'GlassManagement',
  components: { CGlassForm, CEditDialog, CDeleteWarning, TopButtonArranger },
  async beforeRouteEnter (to, from, next) {
    const glasses = await GlassService.getAllGlasses()
    next(vm => {
      vm.glasses = glasses
    })
  },
  data: () => {
    return {
      glasses: [],
      columns: [
        { name: 'name', label: 'Name', field: 'name', align: 'center' },
        { name: 'size', label: 'Size', field: 'size', align: 'center' },
        { name: 'default', label: 'Default', field: 'default', align: 'center' },
        {
          name: 'useForSingleIngredients',
          label: 'For single ingredients',
          field: 'useForSingleIngredients',
          align: 'center'
        },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ],
      loading: false,
      editOptions: {
        valid: false,
        editDialogHeadline: '',
        editErrorMessage: '',
        saving: false,
        editDialog: false,
        editGlass: {},
        newGlass: {
          id: -1,
          name: '',
          size: '',
          useForSingleIngredients: false,
          default: false
        }
      }
    }
  },
  created () {
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiCheckboxBlankCircleOutline = mdiCheckboxBlankCircleOutline
    this.mdiCheckCircle = mdiCheckCircle
  },
  methods: {
    showEditDialog (glass) {
      if (glass) {
        this.editOptions.editGlass = Object.assign({}, glass)
        this.editOptions.editDialogHeadline = 'Edit glass'
      } else {
        this.editOptions.editGlass = Object.assign({}, this.editOptions.newGlass)
        this.editOptions.editDialogHeadline = 'Create glass'
      }
      this.editOptions.editDialog = true
    },
    closeEditDialog () {
      this.editOptions.editIngredient = Object.assign({}, this.editOptions.newGlass)
      this.editOptions.editDialog = false
      this.editOptions.editErrorMessage = ''
    },
    onClickEditDialogSave () {
      const isNew = this.editOptions.editGlass.id === -1
      this.editOptions.saving = true
      let promise
      if (isNew) {
        promise = GlassService.createGlass(this.editOptions.editGlass)
      } else {
        promise = GlassService.updateGlass(this.editOptions.editGlass)
      }
      promise = promise.then((recipe) => {
        const msg = 'Glass ' + (this.isNew ? 'created' : 'updated') + ' successfully'
        this.$q.notify({
          type: 'positive',
          message: msg
        })
        this.closeEditDialog()
        this.onRefresh()
      })
      promise.finally(() => {
        this.editOptions.saving = false
      })
    },
    onRefresh () {
      this.loading = true
      setTimeout(() => {
        GlassService.getAllGlasses()
          .then(x => {
            this.glasses = x
          })
          .finally(() => {
            this.loading = false
          })
      }, 500)
    },
    onDeleteSuccess () {
      this.onRefresh()
    },
    deleteGlass (id) {
      return GlassService.deleteGlass(id)
    }
  }
}
</script>

<style scoped>

</style>
