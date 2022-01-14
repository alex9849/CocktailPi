<template>
  <q-page class="page-content" padding>
    <h5>Events</h5>
    <TopButtonArranger>
      <q-btn
        color="negative"
        label="Delete selected actions"
        no-caps
        @click="$refs.deleteDialog.openForItems(selected)"
      />
      <q-btn
        color="positive"
        label="Add action"
        no-caps
        @click="showEditDialog(null)"
      />
      <q-btn
        color="info"
        label="Refresh"
        no-caps
        :disable="isLoading"
        :loading="isLoading"
        @click="onRefreshButton"
      />
    </TopButtonArranger>
    <div class="q-py-md">
      <q-table
        v-model:selected="selected"
        :columns="columns"
        :loading="isLoading"
        :pagination="{rowsPerPage: 0, sortBy: 'trigger'}"
        :rows="eventActions"
        hide-bottom
        selection="multiple"
      >
        <template
          v-slot:loading
        >
          <q-inner-loading
            color="info"
            showing
          />
        </template>
        <template v-slot:body-cell-trigger="props">
          <q-td
            :props="props"
          >
            {{ eventTriggerToDisplayName(props.value) }}
          </q-td>
        </template>
        <template v-slot:body-cell-executionGroups="props">
          <q-td
            :props="props"
          >
            {{ props.value.join(', ') }}
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
              @click="showEditDialog(props.row)"
              text-color="white"
            >
              <q-tooltip>
                Edit
              </q-tooltip>
            </q-btn>
            <q-btn
              @click="$refs.deleteDialog.openForItems([props.row])"
              :icon="mdiDelete"
              color="red"
              dense
              rounded
            >
              <q-tooltip>
                Delete
              </q-tooltip>
            </q-btn>
          </q-td>
        </template>
      </q-table>
    </div>
    <c-edit-dialog
      v-model:show="editOptions.editDialog"
      :error-message="editOptions.editErrorMessage"
      :saving="editOptions.saving"
      :title="editDialogHeadline"
      :valid="editOptions.valid"
      @clickAbort="closeEditDialog"
      @clickSave="onClickSaveEventAction"
    >
      <c-event-action-editor-form
        v-model:modelValue="editOptions.editEventAction"
        v-model:selectedFile="editOptions.selectedFile"
        @invalid="editOptions.valid = false"
        @valid="editOptions.valid = true"
      />
    </c-edit-dialog>
    <c-delete-warning
      ref="deleteDialog"
      :delete-method="deleteEventAction"
      :list-point-method="x => x.description"
      item-name-plural="Actions"
      item-name-singular="Action"
      @deleteFailure="initialize"
      @deleteSuccess="onDeleteFailure"
    />
  </q-page>
</template>

<script>

import { mdiDelete, mdiPencilOutline } from '@quasar/extras/mdi-v5'
import TopButtonArranger from 'components/TopButtonArranger'
import CEditDialog from 'components/CEditDialog'
import EventAction from '../models/EventAction'
import EventActionService from '../services/eventaction.service'
import CEventActionEditorForm from 'components/CEventActionEditorForm'
import { eventActionTriggerDisplayNames } from '../mixins/constants'
import CDeleteWarning from 'components/CDeleteWarning'

export default {
  name: 'EventManagement',
  mixins: [eventActionTriggerDisplayNames],
  components: { CDeleteWarning, CEventActionEditorForm, TopButtonArranger, CEditDialog },
  data () {
    return {
      selected: [],
      eventActions: [],
      isLoading: false,
      editOptions: {
        editDialog: false,
        editErrorMessage: '',
        saving: false,
        valid: false,
        selectedFile: null,
        editEventAction: new EventAction(-1, null, [])
      },
      columns: [
        { name: 'trigger', label: 'Trigger', field: 'trigger', align: 'center' },
        { name: 'description', label: 'Description', field: 'description', align: 'center' },
        { name: 'comment', label: 'Comment', field: 'comment', align: 'center' },
        { name: 'executionGroups', label: 'Execution-groups', field: 'executionGroups', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ]
    }
  },
  created () {
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
    this.initialize()
  },
  methods: {
    onDeleteFailure () {
      this.selected.splice(0, this.selected.length)
      this.initialize()
    },
    deleteEventAction (eventActionId) {
      return EventActionService.deleteEvent(eventActionId)
    },
    onRefreshButton () {
      this.isLoading = true
      const vm = this
      setTimeout(() => {
        vm.initialize()
      }, 500)
    },
    initialize () {
      this.isLoading = true
      EventActionService.getAllEvents()
        .then(eventActions => {
          this.eventActions = eventActions
          this.isLoading = false
        })
    },
    showEditDialog (eventAction) {
      if (eventAction) {
        this.editOptions.editEventAction = eventAction
      }
      this.editOptions.editDialog = true
    },
    closeEditDialog () {
      this.editOptions.editDialog = false
      this.editOptions.editEventAction = new EventAction(-1, null, [])
    },
    onClickSaveEventAction () {
      this.editOptions.saving = true
      if (this.isNewEditEventAction) {
        EventActionService.createEvent(this.editOptions.editEventAction,
          this.editOptions.selectedFile)
          .then(() => {
            this.closeEditDialog()
            this.onRefreshButton()
          }, err => {
            this.editOptions.editErrorMessage = err.message
          }).finally(() => {
            this.editOptions.saving = false
          })
      }
    }
  },
  computed: {
    isNewEditEventAction () {
      return this.editOptions.editEventAction.id === -1
    },
    editDialogHeadline () {
      if (this.isNewEditEventAction) {
        return 'Create new action'
      } else {
        return 'Edit action'
      }
    }
  }
}
</script>

<style scoped>

</style>
