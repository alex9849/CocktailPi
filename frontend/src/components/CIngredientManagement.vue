<template>
  <TopButtonArranger>
    <q-btn
      :disable="loading"
      color="negative"
      label="Delete selected ingredients"
      no-caps
      @click="$refs.deleteDialog.openForItems(selected)"
    />
    <q-btn
      :disable="loading"
      color="positive"
      label="Create ingredient"
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
      :rows="ingredients"
      hide-no-data
      selection="multiple"
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
          {{ props.row.alcoholContent }}%
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
  <c-delete-warning
    ref="deleteDialog"
    :delete-method="deleteIngredient"
    :list-point-method="x => x.name"
    banner-warning="This also removes the selected ingredients from all associated recipes!"
    item-name-plural="ingredients"
    item-name-singular="ingredient"
    @deleteFailure="fetchAll"
    @deleteSuccess="onDeleteSuccess"
  />
  <c-edit-dialog
    v-model:show="editOptions.editDialog"
    :error-message="editOptions.editErrorMessage"
    :saving="editOptions.editIngredientSaving"
    :title="editDialogHeadline"
    :valid="editOptions.valid"
    @clickAbort="closeEditDialog"
    @clickSave="onClickSaveIngredient"
  >
    <ingredient-form
      v-model:model-value="editOptions.editIngredient"
      :disable="editOptions.editIngredientSaving"
      @invalid="editOptions.valid = false"
      @valid="editOptions.valid = true"
    />
  </c-edit-dialog>
</template>

<script>
import { mdiCheckboxBlankCircleOutline, mdiCheckCircle, mdiDelete, mdiPencilOutline } from '@quasar/extras/mdi-v5'
import IngredientService from '../services/ingredient.service'
import CEditDialog from 'components/CEditDialog'
import TopButtonArranger from 'components/TopButtonArranger'
import IngredientForm from 'components/IngredientForm'
import CDeleteWarning from 'components/CDeleteWarning'

export default {
  name: 'CIngredientManagement',
  components: { CDeleteWarning, IngredientForm, CEditDialog, TopButtonArranger },
  data () {
    return {
      columns: [
        { name: 'name', label: 'Ingredient', field: 'name', align: 'center' },
        { name: 'type', label: 'Type', field: 'type', align: 'center' },
        { name: 'alcoholContent', label: 'Alcohol content', field: 'alcoholContent', align: 'center' },
        { name: 'unit', label: 'Unit', field: 'unit', align: 'center' },
        { name: 'pumpTimeMultiplier', label: 'Pump time multiplier', field: 'pumpTimeMultiplier', align: 'center' },
        { name: 'parentGroup', label: 'Parent group', field: 'parentGroupName', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ],
      ingredients: [],
      selected: [],
      loading: false,
      editOptions: {
        editErrorMessage: '',
        editIngredientSaving: false,
        editDialog: false,
        valid: false,
        editIngredient: {
          id: -1,
          name: '',
          pumpTimeMultiplier: 1.0,
          alcoholContent: 0,
          type: 'automated',
          unit: null
        },
        newIngredient: {
          id: -1,
          name: '',
          pumpTimeMultiplier: 1.0,
          alcoholContent: 0,
          type: 'automated',
          unit: null
        }
      }
    }
  },
  methods: {
    onRefresh () {
      this.loading = true
      const vm = this
      setTimeout(vm.fetchAll, 500)
    },
    deleteIngredient (id) {
      IngredientService.deleteIngredient(id)
    },
    showEditDialog (ingredient) {
      if (ingredient) {
        this.editOptions.editIngredient = Object.assign({}, this.editOptions.newIngredient)
        this.editOptions.editIngredient = Object.assign(this.editOptions.editIngredient, ingredient)
      }
      this.editOptions.editDialog = true
    },
    closeEditDialog () {
      this.editOptions.editIngredient = Object.assign({}, this.editOptions.newIngredient)
      this.editOptions.editDialog = false
      this.editOptions.editErrorMessage = ''
    },
    onClickSaveIngredient () {
      this.editOptions.editIngredientSaving = true
      const vm = this
      const onSuccess = function () {
        vm.editOptions.editIngredientSaving = false
        vm.editOptions.editErrorMessage = ''
        vm.$q.notify({
          type: 'positive',
          message: 'Ingredient ' + (vm.isEditIngredientNew ? 'created' : 'updated') + ' successfully'
        })
        vm.closeEditDialog()
        vm.fetchAll()
      }

      const onError = function (error) {
        vm.editOptions.editIngredientSaving = false
        vm.editOptions.editErrorMessage = error.response.data.message
      }

      if (this.isEditIngredientNew) {
        IngredientService.createIngredient(this.editOptions.editIngredient)
          .then(onSuccess, error => onError(error))
      } else {
        IngredientService.updateIngredient(this.editOptions.editIngredient)
          .then(onSuccess, error => onError(error))
      }
    },
    onDeleteSuccess () {
      this.selected.splice(0, this.selected.length)
      this.fetchAll()
    },
    fetchAll () {
      this.loading = true
      IngredientService.getIngredientsFilter(null, false,
        false, true, false)
        .then(ingredients => {
          this.loading = false
          this.ingredients = ingredients
        })
    }
  },
  setup () {
    return {
      mdiDelete: mdiDelete,
      mdiPencilOutline: mdiPencilOutline,
      mdiCheckCircle: mdiCheckCircle,
      mdiCheckboxBlankCircleOutline: mdiCheckboxBlankCircleOutline
    }
  },
  created () {
    this.fetchAll()
  },
  computed: {
    isEditIngredientNew () {
      return this.editOptions.editIngredient.id === -1
    },
    editDialogHeadline () {
      if (this.isEditIngredientNew) {
        return 'Create ingredient'
      }
      return 'Edit ingredient'
    }
  }
}
</script>

<style scoped>
</style>
