<template>
  <q-page class="page-content" padding>
    <h5>Events</h5>
    <TopButtonArranger>
      <q-btn
        color="negative"
        label="Delete selected actions"
        no-caps
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
        @invalid="editOptions.valid = false"
        @valid="editOptions.valid = true"
      />
    </c-edit-dialog>
  </q-page>
</template>

<script>

import TopButtonArranger from 'components/TopButtonArranger'
import CEditDialog from 'components/CEditDialog'
import EventAction from '../models/EventAction'
import EventService from '../services/event.service'
import CEventActionEditorForm from 'components/CEventActionEditorForm'

export default {
  name: 'EventManagement',
  components: { CEventActionEditorForm, TopButtonArranger, CEditDialog },
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
        editEventAction: new EventAction(-1, null)
      },
      columns: [
        { name: 'trigger', label: 'Trigger', field: 'trigger', align: 'center' },
        { name: 'action', label: 'Action', field: 'eventAction', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ]
    }
  },
  created () {
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
      EventService.getAllEvents()
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
      this.editOptions.editEventAction = new EventAction(-1, null)
    },
    onClickSaveEventAction () {
      this.closeEditDialog()
      this.onRefreshButton()
    }
  },
  computed: {
    editDialogHeadline () {
      if (this.editOptions.editEventAction.id === -1) {
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
