<template>
  <q-page class="page-content" padding>
    <h5>Pumps</h5>
    <div class="q-gutter-sm">
      <TopButtonArranger>
        <q-btn
          color="negative"
          label="Delete selected pumps"
          no-caps
          :disable="isLoading"
          @click="$refs.deleteDialog.openForItems(selected)"
        />
        <q-btn
          color="positive"
          label="Add pump"
          @click="showEditDialog()"
          no-caps
          :disable="isLoading"
        />
      </TopButtonArranger>
      <TopButtonArranger>
        <q-btn
          color="positive"
          label="Start all pumps"
          @click="onClickTurnOnAllPumps()"
          :icon="mdiPlay"
          no-caps
          :disable="isLoading"
        />
        <q-btn
          color="negative"
          label="Stop all pumps"
          @click="onClickTurnOffAllPumps()"
          :icon="mdiStop"
          no-caps
          :disable="isLoading"
        />
      </TopButtonArranger>
    </div>
    <div class="q-py-md">
      <q-table
        :columns="columns"
        :rows="pumps"
        :loading="isLoading"
        v-model:selected="selected"
        selection="multiple"
        hide-bottom
        :pagination="{rowsPerPage: 0, sortBy: 'id'}"
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
              key="bcmPin"
              :props="props"
            >
              {{ props.row.bcmPin }}
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
              key="powerStateHigh"
              :props="props"
            >
              {{ props.row.powerStateHigh ? 'High':'Low' }}
            </q-td>
            <q-td
              key="pumpedUp"
              :props="props"
            >
              <c-pumped-up-icon-button
                :pump-id="props.row.id"
                :read-only="false"
              />
            </q-td>
            <q-td
              key="actions"
              class="q-pa-md q-gutter-x-sm"
              :props="props"
            >
              <c-pump-up-button
                v-if="false"
                :pump-id="props.row.id"
                :current-pump-direction-reversed="props.row.reversed"
                :pump-up-direction-reversed="true"
              />
              <c-pump-up-button
                :pump-id="props.row.id"
                :current-pump-direction-reversed="props.row.reversed"
                :pump-up-direction-reversed="false"
              />
              <c-pump-turn-on-off-button
                :pump-id="props.row.id"
              />
            </q-td>
            <q-td
              key="options"
              class="q-pa-md q-gutter-x-sm"
              :props="props"
            >
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
                @click="$refs.deleteDialog.openForItems([props.row])"
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
      v-model:show="editOptions.editDialog"
      :error-message="editOptions.editErrorMessage"
      :title="editDialogHeadline"
      :saving="editOptions.editPumpSaving"
      :valid="editOptions.valid"
      @clickAbort="closeEditDialog"
      @clickSave="onClickSavePump"
    >
      <pump-editor-form
        v-model:model-value="editOptions.editPump"
        :persistent="editOptions.editPumpSaving"
        @hide="closeEditDialog"
        @valid="editOptions.valid = true"
        @invalid="editOptions.valid = false"
      />
    </c-edit-dialog>
    <c-delete-warning
      ref="deleteDialog"
      :delete-method="deletePump"
      :list-point-method="x => 'Pump #' + x.id"
      item-name-plural="pumps"
      item-name-singular="pump"
      @deleteSuccess="onDeleteSuccess"
    />
  </q-page>
</template>

<script>

import { mdiDelete, mdiPencilOutline, mdiPlay, mdiStop } from '@quasar/extras/mdi-v5'
import { mapGetters } from 'vuex'
import PumpEditorForm from '../components/PumpEditorForm'
import PumpService, { pumpDtoMapper } from '../services/pump.service'
import CDeleteWarning from 'components/CDeleteWarning'
import CEditDialog from 'components/CEditDialog'
import TopButtonArranger from 'components/TopButtonArranger'
import CPumpedUpIconButton from 'components/CPumpedUpIconButton'
import CPumpUpButton from 'components/CPumpUpButton'
import CPumpTurnOnOffButton from 'components/CPumpTurnOnOffButton'

export default {
  name: 'PumpManagement',
  components: { CPumpTurnOnOffButton, CPumpUpButton, CPumpedUpIconButton, TopButtonArranger, CEditDialog, PumpEditorForm, CDeleteWarning },
  data () {
    return {
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
          bcmPin: '',
          fillingLevelInMl: 0,
          powerStateHigh: false,
          pumpedUp: false,
          currentIngredient: null
        },
        newPump: {
          id: -1,
          timePerClInMs: '',
          tubeCapacityInMl: '',
          bcmPin: '',
          fillingLevelInMl: 0,
          powerStateHigh: false,
          pumpedUp: false,
          currentIngredient: null
        }
      },
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
        { name: 'bcmPin', label: 'BCM-Pin', field: 'bcmPin', align: 'center' },
        { name: 'currentIngredient', label: 'Current Ingredient', field: 'currentIngredient', align: 'center' },
        { name: 'fillingLevelInMl', label: 'Filling Level', field: 'fillingLevelInMl', align: 'center' },
        { name: 'powerStateHigh', label: 'Power State', field: 'powerStateHigh', align: 'center' },
        { name: 'pumpedUp', label: 'Pumped Up', field: 'pumpedUp', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' },
        { name: 'options', label: 'Options', field: '', align: 'center' }
      ]
    }
  },
  created () {
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiPlay = mdiPlay
    this.mdiStop = mdiStop
  },
  methods: {
    onClickTurnOnAllPumps () {
      PumpService.startPump(null)
    },
    onClickTurnOffAllPumps () {
      PumpService.stopPump(null)
    },
    deletePump (id) {
      return PumpService.deletePump(id)
    },
    onDeleteSuccess () {
      this.selected.splice(0, this.selected.length)
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
      }

      const onError = function (error) {
        vm.editOptions.editPumpSaving = false
        vm.editOptions.editErrorMessage = error.response.data.message
        vm.$q.notify({
          type: 'negative',
          message: error.response.data.message
        })
      }
      const dto = pumpDtoMapper.toPumpCreateDto(this.editOptions.editPump)
      if (this.isEditPumpNew) {
        PumpService.createPump(dto)
          .then(onSuccess, error => onError(error))
      } else {
        PumpService.updatePump(this.editOptions.editPump.id, dto)
          .then(onSuccess, error => onError(error))
      }
    }
  },
  computed: {
    ...mapGetters({
      getPumpOccupation: 'pumpLayout/getPumpOccupation',
      pumps: 'pumpLayout/getLayout'
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
