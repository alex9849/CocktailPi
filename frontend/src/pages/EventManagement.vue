<template>
  <q-page class="page-content" padding>
    <h5>Events</h5>
    <TopButtonArranger>
      <q-btn
        color="negative"
        label="Delete selected actions"
        no-caps
        @click="openDeleteDialog(true)"
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
              @click="() => {deleteOptions.eventActions.push(props.row); openDeleteDialog(false);}"
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
    <c-question
      v-model:show="deleteOptions.dialog"
      :loading="deleteOptions.loading"
      :question="deleteQuestionMessage"
      ok-button-text="Delete"
      ok-color="red"
      @clickAbort="closeDeleteDialog"
      @clickOk="deleteSelected"
    >
      <template v-slot:buttons>
        <q-btn
          v-if="deleteOptions.eventActions.length === 0"
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
            v-for="(eventAction, index) in deleteOptions.eventActions"
            :key="index"
          >
            {{ eventAction.description }}
          </li>
        </ul>
      </template>
    </c-question>
  </q-page>
</template>

<script>

import { mdiDelete, mdiPencilOutline } from '@quasar/extras/mdi-v5'
import TopButtonArranger from 'components/TopButtonArranger'
import CQuestion from 'components/CQuestion'
import CEditDialog from 'components/CEditDialog'
import EventAction from '../models/EventAction'
import EventActionService from '../services/eventaction.service'
import CEventActionEditorForm from 'components/CEventActionEditorForm'
import { eventActionTriggerDisplayNames } from '../mixins/constants'

export default {
  name: 'EventManagement',
  mixins: [eventActionTriggerDisplayNames],
  components: { CEventActionEditorForm, TopButtonArranger, CEditDialog, CQuestion },
  data () {
    return {
      selected: [],
      eventActions: [],
      isLoading: false,
      deleteOptions: {
        dialog: false,
        eventActions: [],
        loading: false
      },
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
    openDeleteDialog (forSelectedEventActions) {
      if (forSelectedEventActions) {
        this.deleteOptions.eventActions.push(...this.selected)
      }
      this.deleteOptions.dialog = true
    },
    closeDeleteDialog () {
      this.deleteOptions.eventActions.splice(0, this.deleteOptions.eventActions.length)
      this.deleteOptions.dialog = false
    },
    deleteSelected () {
      this.deleteOptions.loading = true
      const vm = this
      const promises = []
      this.deleteOptions.eventActions.forEach(eventAction => {
        promises.push(EventActionService.deleteEvent(eventAction))
      })
      Promise.all(promises)
        .then(() => {
          vm.$q.notify({
            type: 'positive',
            message: ((vm.deleteOptions.eventActions.length > 1) ? 'Actions' : 'Action') + ' deleted successfully'
          })
          vm.closeDeleteDialog()
          vm.selected.splice(0, vm.selected.length)
          vm.deleteOptions.loading = false
          vm.initialize()
        }, () => {
          vm.deleteOptions.dialog = false
          vm.selected.splice(0, vm.selected.length)
          vm.initialize()
        })
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
    deleteQuestionMessage () {
      if (this.deleteOptions.eventActions.length === 0) {
        return 'No actions selected!'
      }
      if (this.deleteOptions.eventActions.length === 1) {
        return 'The following actions will be deleted:'
      }
      return 'The following action will be deleted:'
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
