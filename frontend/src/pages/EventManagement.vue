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
            <q-chip v-for="(item, index) in props.value"
                    :key="index"
                    :label="item"
                    :ripple="false"
                    dense
                    square
            />
          </q-td>
        </template>
        <template v-slot:body-cell-status="props">
          <q-td
            :props="props"
          >
            <div v-if="getEventActionStatus(props.row.id).status === 'RUNNING'">
              <q-chip :ripple="false"
                      color="green"
                      dense
                      label="Running"
                      square
                      text-color="white"
              />
              <q-btn :icon="mdiSkullCrossbones"
                     dense
                     label="Kill process"
                     no-caps
                     size="sm"
                     text-color="red"
                     unelevated
                     @click="killEventActionProcess(getEventActionStatus(props.row.id).runId)"
              />
            </div>
            <div v-else-if="getEventActionStatus(props.row.id).status === 'STOPPED'">
              <q-chip :ripple="false"
                      color="red"
                      dense
                      label="Stopped"
                      square
                      text-color="white"
              />
            </div>
            <div v-else>
              <q-chip :ripple="false"
                      color="red"
                      dense
                      label="Error"
                      square
                      text-color="white"
              />
            </div>
          </q-td>
        </template>
        <template v-slot:body-cell-actions="props">
          <q-td
            key="actions"
            :props="props"
            class="q-pa-md q-gutter-x-sm"
          >
            <q-btn
              v-if="getEventActionStatus(props.row.id).hasLog"
              :icon="mdiFormatListText"
              :style="{backgroundColor: '#31ccec'}"
              @click="showEventActionLog(props.row.id)"
              dense
              rounded
              text-color="white"
            >
              <q-tooltip>
                View logs
              </q-tooltip>
            </q-btn>
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
        :previousEventActionType="editOptions.previousEventActionType"
        :previous-file-name="editOptions.previousFileName"
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
    <q-dialog
      v-model:model-value="actionLog.show"
      @hide="hideEventActionLog"
    >
      <q-card
        class="bg-black text-white"
        flat
        style="resize: both; overflow-wrap: break-word; max-width: none"
      >
        <q-card-section style="height: 300px;">
          <p
            v-for="entry of actionLog.log"
            :key="entry.date"
          >
            ({{ entry.timeStamp.toLocaleTimeString() }}) [{{ entry.type }}]: {{ entry.message}}
          </p>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script>

import { mdiDelete, mdiPencilOutline, mdiSkullCrossbones, mdiFormatListText } from '@quasar/extras/mdi-v5'
import WebSocketService from '../services/websocket.service'
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
        editEventAction: new EventAction(-1, null, []),
        previousEventActionType: null,
        previousFileName: undefined
      },
      columns: [
        { name: 'trigger', label: 'Trigger', field: 'trigger', align: 'center' },
        { name: 'description', label: 'Description', field: 'description', align: 'center' },
        { name: 'comment', label: 'Comment', field: 'comment', align: 'center' },
        { name: 'executionGroups', label: 'Execution-groups', field: 'executionGroups', align: 'center' },
        { name: 'status', label: 'Status', field: 'status', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
      ],
      actionStatus: {
        actionStatus: new Map(),
        log: []
      },
      actionLog: {
        topic: null,
        show: false,
        log: []
      }
    }
  },
  created () {
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiSkullCrossbones = mdiSkullCrossbones
    this.mdiFormatListText = mdiFormatListText
    this.initialize()
  },
  mounted () {
    const vm = this
    WebSocketService.subscribe('/user/topic/eventactionstatus', (data) => {
      vm.actionStatus.actionStatus.clear()
      data = JSON.parse(data.body)
      for (const actionStatus of data) {
        vm.actionStatus.actionStatus.set(actionStatus.eventActionId, actionStatus)
      }
    })
  },
  unmounted () {
    WebSocketService.unsubscribe('/user/topic/eventactionstatus')
  },
  methods: {
    showEventActionLog (actionId) {
      this.actionLog.show = true
      this.actionLog.topic = '/user/topic/eventactionlog/' + actionId
      const vm = this
      WebSocketService.subscribe(this.actionLog.topic, (data) => {
        if (data.body === 'DELETE') {
          vm.actionLog.log.splice(0, vm.actionLog.log.length)
        } else {
          data = JSON.parse(data.body)
          for (const entry of data) {
            entry.timeStamp = new Date(entry.timeStamp)
          }
          vm.actionLog.log.push(...data)
        }
      })
    },
    hideEventActionLog () {
      this.actionLog.show = false
      WebSocketService.unsubscribe(this.actionLog.topic)
      this.actionLog.topic = null
      this.actionLog.log.splice(0, this.actionLog.log.length)
    },
    killEventActionProcess (processId) {
      EventActionService.killEventAction(processId)
    },
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
        const eventActionCopy = Object.assign({}, eventAction)
        this.editOptions.editEventAction = eventActionCopy
        this.editOptions.previousEventActionType = eventAction.type
        this.editOptions.previousFileName = eventAction.fileName
      }
      this.editOptions.editDialog = true
    },
    closeEditDialog () {
      this.editOptions.editDialog = false
      this.editOptions.editEventAction = new EventAction(-1, null, [])
      this.editOptions.selectedFile = null
      this.editOptions.previousFileName = undefined
      this.editOptions.previousEventActionType = null
      this.editOptions.editErrorMessage = ''
    },
    onClickSaveEventAction () {
      this.editOptions.saving = true
      let promise
      let successMessage
      if (this.isNewEditEventAction) {
        promise = EventActionService.createEvent(this.editOptions.editEventAction,
          this.editOptions.selectedFile)
        successMessage = 'Action created successfully!'
      } else {
        promise = EventActionService.updateEvent(this.editOptions.editEventAction,
          this.editOptions.selectedFile)
        successMessage = 'Action updated successfully!'
      }
      promise.then(() => {
        this.$q.notify({
          type: 'positive',
          message: successMessage
        })
        this.closeEditDialog()
        this.onRefreshButton()
      }, err => {
        this.editOptions.editErrorMessage = err.message
      }).finally(() => {
        this.editOptions.saving = false
      })
    },
    getEventActionStatus (eventActionId) {
      const status = this.actionStatus.actionStatus.get(eventActionId)
      if (status) {
        return status
      }
      return {
        eventActionId: -1,
        runId: -1,
        hasLog: false,
        status: 'STOPPED'
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
