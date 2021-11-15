<template>
  <q-page class="page-content" padding>
    <h5>Pumps</h5>
    <TopButtonArranger>
      <q-btn
        color="negative"
        label="Delete selected pumps"
        no-caps
        :disable="isLoading"
        @click="openDeleteDialog(true)"
      />
      <q-btn
        color="positive"
        label="Add pump"
        @click="showEditDialog()"
        no-caps
        :disable="isLoading"
      />
      <q-btn
        color="info"
        label="Refresh"
        :disable="isLoading"
        :loading="isLoading"
        @click="onRefreshButton"
        no-caps
      />
    </TopButtonArranger>
    <div class="q-py-md">
      <q-table
        :columns="columns"
        :data="pumps"
        :loading="isLoading"
        v-model:selected="selected"
        selection="multiple"
        hide-bottom
        :pagination="{rowsPerPage: 0, sortBy: 'id'}"
        :table-style="{margin: '15px'}"
        style="background-color: #f3f3fa"
      >
        <template v-slot:body="props">
          <q-tr
            :props="props"
          >
            <q-td
              auto-width
              style="text-align: center"
            >
              <q-checkbox
                v-model="props.selected"
              />
            </q-td>
            <q-td
              key="id"
              :props="props"
            >
              {{ props.row.id }}
            </q-td>
            <q-td
              key="timePerClInMs"
              :props="props"
            >
              {{ props.row.timePerClInMs }} ms
            </q-td>
            <q-td
              key="tubeCapacityInMl"
              :props="props"
            >
              {{ props.row.tubeCapacityInMl }} ml
            </q-td>
            <q-td
              key="gpioPin"
              :props="props"
            >
              {{ props.row.gpioPin }}
            </q-td>
            <q-td
              key="currentIngredient"
              :props="props"
            >
              {{ props.row.currentIngredient ? props.row.currentIngredient.name : "Empty" }}
            </q-td>
            <q-td
              key="fillingLevelInMl"
              :props="props"
            >
              {{ props.row.fillingLevelInMl }} ml
            </q-td>
            <q-td
              key="actions"
              class="q-pa-md q-gutter-x-sm"
              :props="props"
            >
              <q-btn
                :icon="mdiPlay"
                color="green"
                @click="onClickCleanPump(props.row)"
                dense
                rounded
                :loading="isCleaning(props.row.id)"
              >
                <q-tooltip>
                  Pump up
                </q-tooltip>
              </q-btn>
              <q-btn
                :icon="mdiPencilOutline"
                color="info"
                @click="showEditDialog(props.row)"
                dense
                rounded
              >
                <q-tooltip>
                  Edit
                </q-tooltip>
              </q-btn>
              <q-btn
                :icon="mdiDelete"
                color="red"
                @click="() => {deletePumps.push(props.row); openDeleteDialog(false);}"
                dense
                rounded
              >
                <q-tooltip>
                  Delete
                </q-tooltip>
              </q-btn>
            </q-td>
          </q-tr>
        </template>
        <template
          v-slot:bottom-row
        >
          <td
            style="color: #b5b5b5"
          >
            {{ pumps.length }} pump(s) in total
          </td>
          <td rowspan="5"/>
        </template>
        <template
          v-slot:loading
        >
          <q-inner-loading
            showing
            color="info"
          />
        </template>
      </q-table>
    </div>
    <c-edit-dialog
      v-model="editOptions.editDialog"
      :error-message="editOptions.editErrorMessage"
      :title="editDialogHeadline"
      :saving="editOptions.editPumpSaving"
      :valid="editOptions.valid"
      @clickAbort="closeEditDialog"
      @clickSave="onClickSavePump"
    >
      <pump-editor-form
        class="innerpadding"
        v-model="editOptions.editPump"
        :persistent="editOptions.editPumpSaving"
        @hide="closeEditDialog"
        @valid="editOptions.valid = true"
        @invalid="editOptions.valid = false"
      />
    </c-edit-dialog>
    <c-question
      :question="deleteQuestionMessage"
      ok-color="red"
      ok-button-text="Delete"
      :loading="deleteLoading"
      v-model="deleteDialog"
      @clickOk="deleteSelected"
      @clickAbort="closeDeleteDialog"
    >
      <template v-slot:buttons>
        <q-btn
          v-if="deletePumps.length === 0"
          color="grey"
          style="width: 150px"
          @click="closeDeleteDialog"
        >
          Ok
        </q-btn>
      </template>
      <template v-slot:default>
        <ul style="padding: 0; list-style: none">
          <li
            :key="index"
            v-for="(pump, index) in deletePumps"
          >
            Pump #{{ pump.id }}
          </li>
        </ul>
      </template>
    </c-question>
  </q-page>
</template>

<script>

import {mdiDelete, mdiPencilOutline, mdiPlay} from '@quasar/extras/mdi-v5'
import {mapGetters} from 'vuex'
import PumpEditorForm from '../components/PumpEditorForm'
import PumpService from '../services/pump.service'
import CQuestion from '../components/CQuestion'
import CEditDialog from 'components/CEditDialog'
import TopButtonArranger from 'components/TopButtonArranger'

export default {
  name: 'PumpManagement',
  components: { TopButtonArranger, CEditDialog, PumpEditorForm, CQuestion },
  data () {
    return {
      deleteDialog: false,
      deletePumps: [],
      deleteLoading: false,
      isLoading: false,
      editOptions: {
        editErrorMessage: '',
        editPumpSaving: false,
        editDialog: false,
        valid: false,
        editPump: {
          id: -1,
          timePerClInMs: '',
          tubeCapacityInMl: '',
          gpioPin: '',
          fillingLevelInMl: 0,
          currentIngredient: null
        },
        newPump: {
          id: -1,
          timePerClInMs: '',
          tubeCapacityInMl: '',
          gpioPin: '',
          fillingLevelInMl: 0,
          currentIngredient: null
        }
      },
      pumps: [],
      selected: [],
      columns: [
        { name: 'id', label: 'Nr', field: 'id', align: 'left' },
        { name: 'timePerClInMs', label: 'Time per Cl', field: 'timePerClInMs', align: 'center' },
        {
          name: 'tubeCapacityInMl',
          label: 'Tube capacity',
          field: 'tubeCapacityInMl',
          align: 'center'
        },
        { name: 'gpioPin', label: 'GPIO-Pin', field: 'gpioPin', align: 'center' },
        { name: 'currentIngredient', label: 'Current Ingredient', field: 'currentIngredient', align: 'center' },
        { name: 'fillingLevelInMl', label: 'Filling level', field: 'fillingLevelInMl', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ]
    }
  },
  created () {
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiPlay = mdiPlay
    this.initialize()
  },
  methods: {
    onRefreshButton () {
      this.isLoading = true
      const vm = this
      setTimeout(() => {
        vm.initialize()
      }, 500)
    },
    initialize () {
      this.isLoading = true
      PumpService.getAllPumps()
        .then(pumps => {
          this.pumps = pumps
          this.isLoading = false
        })
    },
    onClickCleanPump (pump) {
      PumpService.cleanPump(pump)
        .catch(error => {
          this.$q.notify({
            type: 'negative',
            message: error.response.data.message
          })
        })
    },
    openDeleteDialog (forSelectedPumps) {
      if (forSelectedPumps) {
        this.deletePumps.push(...this.selected)
      }
      this.deleteDialog = true
    },
    closeDeleteDialog () {
      this.deletePumps.splice(0, this.deletePumps.length)
      this.deleteDialog = false
    },
    deleteSelected () {
      this.deleteLoading = true
      const vm = this
      const promises = []
      this.deletePumps.forEach(pump => {
        promises.push(PumpService.deletePump(pump))
      })
      Promise.all(promises)
        .then(() => {
          vm.$q.notify({
            type: 'positive',
            message: ((vm.deletePumps.length > 1) ? 'Pumps' : 'Pump') + ' deleted successfully'
          })
          vm.closeDeleteDialog()
          vm.selected.splice(0, vm.selected.length)
          vm.deleteLoading = false
          vm.initialize()
        }, err => {
          vm.deleteLoading = false
          vm.selected.splice(0, vm.selected.length)
          vm.initialize()
        })
    },
    closeEditDialog () {
      this.editOptions.editPump = Object.assign({}, this.editOptions.newPump)
      this.editOptions.editDialog = false
      this.editOptions.editErrorMessage = ''
    },
    showEditDialog (pump) {
      if (pump) {
        this.editOptions.editPump = Object.assign({}, pump)
      }
      this.editOptions.editDialog = true
    },
    onClickSavePump () {
      this.editOptions.editPumpSaving = true
      const vm = this
      const onSuccess = function () {
        vm.editOptions.editPumpSaving = false
        vm.editOptions.editErrorMessage = ''
        vm.$q.notify({
          type: 'positive',
          message: 'Pump ' + (vm.isEditIngredientNew ? 'created' : 'updated') + ' successfully'
        })
        vm.closeEditDialog()
        vm.initialize()
      }

      const onError = function (error) {
        vm.editOptions.editPumpSaving = false
        vm.editOptions.editErrorMessage = error.response.data.message
        vm.$q.notify({
          type: 'negative',
          message: error.response.data.message
        })
      }

      if (this.isEditPumpNew) {
        PumpService.createPump(this.editOptions.editPump)
          .then(onSuccess, error => onError(error))
      } else {
        PumpService.updatePump(this.editOptions.editPump)
          .then(onSuccess, error => onError(error))
      }
    }
  },
  computed: {
    ...mapGetters({
      isCleaning: 'pumpLayout/isCleaning'
    }),
    isEditPumpNew () {
      return this.editOptions.editPump.id === -1
    },
    deleteQuestionMessage () {
      if (this.deletePumps.length === 0) {
        return 'No pumps selected!'
      }
      if (this.deletePumps.length === 1) {
        return 'The following pump will be deleted:'
      }
      return 'The following pumps will be deleted:'
    },
    editDialogHeadline () {
      if (this.isEditPumpNew) {
        return 'Create pump'
      }
      return 'Edit pump'
    }
  }
}
</script>

<style scoped>
</style>
