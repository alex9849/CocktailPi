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
      :filter="filter"
      hide-no-data
      selection="multiple"
    >
      <template v-slot:top-right>
        <q-input outlined dense debounce="300" v-model="filter" placeholder="Search">
          <template v-slot:append>
            <q-icon name="search" />
          </template>
        </q-input>
      </template>
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
    <c-delete-warning
      ref="deleteDialog"
      :delete-method="deleteGroup"
      :list-point-method="x => x.name"
      banner-warning="This also removes the selected groups from all associated recipes!"
      item-name-plural="groups"
      item-name-singular="group"
      @deleteFailure="fetchAll"
      @deleteSuccess="onDeleteSuccess"
    />
    <c-edit-dialog
      v-model:show="editOptions.show"
      :error-message="editOptions.editErrorMessage"
      :saving="editOptions.saving"
      :title="editDialogHeadline"
      :valid="editOptions.valid"
      @clickAbort="closeEditDialog"
      @clickSave="onClickSaveGroup"
    >
      <c-ingredient-group-form
        v-model:model-value="editOptions.editGroup"
        :disable="editOptions.saving"
        @invalid="editOptions.valid = false"
        @valid="editOptions.valid = true"
      />
    </c-edit-dialog>
  </div>
</template>

<script>
import { mdiDelete, mdiPencilOutline } from '@quasar/extras/mdi-v5'
import TopButtonArranger from 'components/TopButtonArranger'
import IngredientService, { ingredientDtoMapper } from 'src/services/ingredient.service'
import CDeleteWarning from 'components/CDeleteWarning'
import CEditDialog from 'components/CEditDialog'
import CIngredientGroupForm from 'components/CIngredientGroupForm'

export default {
  name: 'CIngredientGroupManagement',
  components: { CIngredientGroupForm, TopButtonArranger, CDeleteWarning, CEditDialog },
  data () {
    return {
      filter: '',
      selected: [],
      groups: [],
      columns: [
        { name: 'name', label: 'Group', field: 'name', align: 'center' },
        { name: 'alcoholContent', label: 'Alcohol content', field: 'alcoholContent', align: 'center' },
        { name: 'parentGroup', label: 'Parent group', field: 'parentGroupName', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ],
      loading: false,
      editOptions: {
        show: false,
        editErrorMessage: '',
        valid: false,
        saving: false,
        editGroup: {
          id: -1,
          name: '',
          type: 'group',
          parentGroupId: null
        },
        newGroup: {
          id: -1,
          name: '',
          type: 'group',
          parentGroupId: null
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
    fetchAll () {
      this.loading = true
      IngredientService.getIngredientsFilter(null, true,
        true, null, null, false)
        .then(groups => {
          this.loading = false
          this.groups = groups
        })
    },
    onClickSaveGroup () {
      this.editOptions.saving = true
      const vm = this
      const onSuccess = function () {
        vm.editOptions.saving = false
        vm.editOptions.editErrorMessage = ''
        vm.$q.notify({
          type: 'positive',
          message: 'Ingredient ' + (vm.isEditGroupNew ? 'created' : 'updated') + ' successfully'
        })
        vm.closeEditDialog()
        vm.fetchAll()
      }

      const onError = function (error) {
        vm.editOptions.saving = false
        vm.editOptions.editErrorMessage = error.response.data.message
      }

      const dto = ingredientDtoMapper.toIngredientCreateDto(this.editOptions.editGroup)
      if (this.isEditGroupNew) {
        IngredientService.createIngredient(dto)
          .then(onSuccess, error => onError(error))
      } else {
        IngredientService.updateIngredient(this.editOptions.editGroup.id, dto)
          .then(onSuccess, error => onError(error))
      }
    },
    getAlcoholContentString (min, max) {
      if (min == null) {
        return '-'
      }
      if (min === max) {
        return min + '%'
      }
      return min + ' - ' + max + '%'
    },
    onDeleteSuccess () {
      this.selected.splice(0, this.selected.length)
      this.fetchAll()
    },
    deleteGroup (id) {
      IngredientService.deleteIngredient(id)
    },
    showEditDialog (group) {
      if (group) {
        this.editOptions.editGroup = Object.assign({}, this.editOptions.newGroup)
        this.editOptions.editGroup = Object.assign(this.editOptions.editGroup, group)
      }
      this.editOptions.show = true
    },
    closeEditDialog () {
      this.editOptions.editGroup = Object.assign({}, this.editOptions.newGroup)
      this.editOptions.show = false
      this.editOptions.editErrorMessage = ''
    }
  },
  computed: {
    isEditGroupNew () {
      return this.editOptions.editGroup.id === -1
    },
    editDialogHeadline () {
      if (this.isEditGroupNew) {
        return 'Create group'
      }
      return 'Edit group'
    }
  },
  setup () {
    return {
      mdiDelete: mdiDelete,
      mdiPencilOutline: mdiPencilOutline
    }
  },
  created () {
    this.fetchAll()
  }
}
</script>

<style scoped>

</style>
