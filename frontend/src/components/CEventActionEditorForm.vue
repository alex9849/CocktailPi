<template>
  <div
    class="q-gutter-y-sm"
  >
    <q-inner-loading
      :showing="groupsLoading"
    />
    <q-input
      :disable="disable || groupsLoading"
      :model-value="modelValue.comment"
      :rules="[
                val => !v.modelValue.comment.maxLength.$invalid || 'Max length: 40'
              ]"
      filled
      hide-bottom-space
      label="Comment"
      outlined
      @update:model-value="setValue('comment', $event)"
    />
    <q-dialog v-model:model-value="showHelpExecutionGroup">
      <q-card>
        <q-card-section>
          <h6>Execution-groups:</h6>
          Actions within the same execution-group can be executed concurrently.
          There are 2 cases:
          <ul>
            <li><b>An action has no execution group:</b> The action won't cancel any running actions.
              It also won't be canceled by other starting actions.
              The action will only get canceled if it gets executed a second time.
            </li>
            <li><b>The action shares an execution group with another action:</b>
              The action will cancel all other running actions (except the ones with no execution groups)
              that don't share any execution groups with it.
              The action will also get canceled if it gets executed a second time.
              Actions with common execution-groups can be executed concurrently.
            </li>
          </ul>
        </q-card-section>
      </q-card>
    </q-dialog>
    <q-select
      ref="executionGroupsSelector"
      :disable="disable || groupsLoading"
      :model-value="modelValue.executionGroups"
      :options="executionGroupOptions"
      filled
      hide-bottom-space
      input-debounce="0"
      label="Execution-groups"
      multiple
      new-value-mode="add-unique"
      use-chips
      use-input
      @new-value="onAddNewValueExecGroups"
      @filter="onFilterExecGroups"
      @update:model-value="setValue('executionGroups', $event)"
    >
      <template v-slot:append>
        <q-icon
          :name="mdiInformation"
          style="cursor: pointer"
          @click.stop.prevent="showHelpExecutionGroup = !showHelpExecutionGroup"
        />
      </template>
      <template v-slot:before-options>
        <div>
          <q-item
            v-if="currentExecutionGroupFilter && isExecutionGroupFilterUnique"
            clickable
            @click="addNewExecGroup(currentExecutionGroupFilter)"
          >
            <q-item-section>
              <div class="inline-block">
                <b>Create new:</b> {{ currentExecutionGroupFilter }}
              </div>
            </q-item-section>
          </q-item>
          <q-item v-else>
            <q-item-section>
              <div class="text-grey-7 text-italic text-center">
                Write to create new group...
              </div>
            </q-item-section>
          </q-item>
        </div>
      </template>
      <template v-slot:no-option>
        <div>
          <q-item
            v-if="currentExecutionGroupFilter && isExecutionGroupFilterUnique"
            clickable
            @click="addNewExecGroup(currentExecutionGroupFilter)"
          >
            <q-item-section>
              <div class="inline-block">
                <b>Create new:</b> {{ currentExecutionGroupFilter }}
              </div>
            </q-item-section>
          </q-item>
          <q-item v-else>
            <q-item-section>
              <div class="text-grey-7 text-italic text-center">
                Write to create new group...
              </div>
            </q-item-section>
          </q-item>
        </div>
      </template>
    </q-select>
    <q-select
      :disable="disable || groupsLoading"
      :model-value="modelValue.trigger"
      :options="eventActionTriggerDisplayNames"
      emit-value
      hide-bottom-space
      filled
      label="Trigger*"
      map-options
      @update:model-value="setValue('trigger', $event)"
      :rules="[
                val => !v.modelValue.trigger.required.$invalid || 'Required'
              ]"
    />
    <q-select
      :disable="disable || groupsLoading"
      :model-value="modelValue.type"
      :options="existingActions"
      emit-value
      hide-bottom-space
      filled
      label="Action*"
      map-options
      @update:model-value="setAction($event)"
      :rules="[
                val => !v.modelValue.type.required.$invalid || 'Required'
              ]"
    />
    <q-splitter :model-value="10"/>
    <q-card
      bordered
      flat
    >
      <q-card-section
        v-if="!modelValue.type"
        class="text-italic text-grey-7"
      >
        Select action to view options...
      </q-card-section>
      <q-card-section
        v-else
        class="q-px-sm q-py-none q-pa-sm"
      >
        <q-tab-panels
          :model-value="modelValue.type"
          animated
        >
          <q-tab-panel
            name="callUrl"
            class="q-gutter-y-md q-px-none"
          >
            <q-select
              :model-value="modelValue.requestMethod"
              :options="urlRequestMethods"
              filled
              hide-bottom-space
              label="Request method*"
              @update:model-value="setValue('requestMethod', $event)"
              :rules="[
                val => !v.modelValue.requestMethod.required.$invalid || 'Required'
              ]"
            />
            <q-input
              :model-value="modelValue.url"
              filled
              hide-bottom-space
              label="URL*"
              placeholder="https://google.com"
              @update:model-value="setValue('url', $event)"
              :rules="[
                val => !v.modelValue.url.required.$invalid || 'Required',
                val => !v.modelValue.url.url.$invalid || 'URL format invalid',
                val => !v.modelValue.url.maxLength.$invalid || 'Max length: 255'
              ]"
            />
          </q-tab-panel>
          <q-tab-panel
            name="playAudio"
            class="q-gutter-y-md q-px-none"
          >
            <q-file :model-value="selectedFile"
                    accept=".wav"
                    bottom-slots
                    filled
                    :clearable="!!previousFileName"
                    :display-value="fileName"
                    hide-bottom-space
                    label="Audio (max. 20 MB)"
                    max-file-size="20971520"
                    @update:model-value="setSelectedFile($event)"
                    :rules="[
                      val => !v.selectedFile.required || !v.selectedFile.required.$invalid || 'Required'
                    ]"
            >
              <template v-slot:prepend>
                <q-icon
                  name="cloud_upload"
                  @click.stop
                />
              </template>
            </q-file>
            <q-item>
              <q-item-section avatar>
                <q-icon color="teal" name="volume_up"/>
              </q-item-section>
              <q-item-section>
                <q-slider
                  :label-value="'Volume: ' + modelValue.volume + '%'"
                  :max="100"
                  :min="0"
                  :model-value="modelValue.volume"
                  color="teal"
                  label
                  label-always
                  @update:model-value="setValue('volume', $event)"
                />
              </q-item-section>
            </q-item>
            <q-checkbox
              :model-value="!!modelValue.onRepeat"
              label="On repeat"
              @update:model-value="setValue('onRepeat', $event)"
            />
          </q-tab-panel>
          <q-tab-panel
            name="execPy"
            class="q-gutter-y-sm q-px-none"
          >
            <p>
              Executes a python file. The docker image uses python 3.
              You can also view the console output of your program in real time.
            </p>
            <q-card bordered flat>
              <q-expansion-item
                v-model="installedPythonLibraries.loaded"
                label="Installed python libraries"
                @show="getInstalledPythonLibraries()"
              >
                <q-inner-loading
                  :showing="installedPythonLibraries.loading"
                  label="Fetching Libraries"
                  style="display: contents"
                  transition-hide="none"
                />
                <div v-if="!installedPythonLibraries.loading">
                  <q-splitter
                    :model-value="10"
                    horizontal
                  />
                  <table
                    v-if="installedPythonLibraries.libraries.length !== 0"
                    style="width: 100%"
                  >
                    <thead>
                    <tr>
                      <th>Library</th>
                      <th>Version</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr
                      v-for="info of installedPythonLibraries.libraries"
                      :key="info.name"
                    >
                      <td>{{ info.name }}</td>
                      <td>{{ info.version }}</td>
                    </tr>
                    </tbody>
                  </table>
                  <p
                    v-else
                    class="q-pa-sm"
                  >
                    No libraries installed
                  </p>
                </div>
              </q-expansion-item>
            </q-card>
            <q-file :model-value="selectedFile"
                    accept=".py"
                    bottom-slots
                    filled
                    :clearable="!!previousFileName"
                    :display-value="fileName"
                    hide-bottom-space
                    label="Python (max. 20 MB)"
                    max-file-size="20971520"
                    @update:model-value="setSelectedFile($event)"
                    :rules="[
                      val => !v.selectedFile.required || !v.selectedFile.required.$invalid || 'Required'
                    ]"
            >
              <template v-slot:prepend>
                <q-icon
                  name="cloud_upload"
                  @click.stop
                />
              </template>
            </q-file>
          </q-tab-panel>
          <q-tab-panel
            name="doNothing"
          >
            This action does nothing. But it will cancel other running actions that don't share execution-groups with it
          </q-tab-panel>
        </q-tab-panels>
      </q-card-section>
    </q-card>
  </div>
</template>

<script>

import useVuelidate from '@vuelidate/core'
import EventActionService from '../services/eventaction.service'
import SystemService from '../services/system.service'
import { maxLength, maxValue, minValue, required, url } from '@vuelidate/validators'
import { eventActionTriggerDisplayNames } from '../mixins/constants'
import { mdiInformation } from '@quasar/extras/mdi-v5'

export default {
  name: 'CEventActionEditorForm',
  mixins: [eventActionTriggerDisplayNames],
  setup () {
    return {
      v: useVuelidate(),
      mdiInformation: mdiInformation
    }
  },
  props: {
    modelValue: {
      type: Object,
      required: true
    },
    selectedFile: {
      type: Object
    },
    disable: {
      type: Boolean,
      default: false
    },
    previousEventActionType: {
      type: String,
      required: false
    },
    previousFileName: {
      type: String,
      required: false
    }
  },
  created () {
    this.groupsLoading = true
    EventActionService.getAllExecutionGroups()
      .then(groups => {
        this.groupsLoading = false
        this.existingExecutionGroups = groups
      })
  },
  data () {
    return {
      installedPythonLibraries: {
        libraries: [],
        loading: false
      },
      showHelpExecutionGroup: false,
      groupsLoading: false,
      existingExecutionGroups: [],
      currentExecutionGroupFilter: '',
      urlRequestMethods: ['GET', 'POST', 'PUT', 'DELETE'],
      existingActions: [{
        label: 'Call URL',
        value: 'callUrl'
      }, {
        label: 'Play audio file',
        value: 'playAudio'
      }, {
        label: 'Execute python script',
        value: 'execPy'
      }, {
        label: 'Do nothing',
        value: 'doNothing'
      }]
    }
  },
  methods: {
    getInstalledPythonLibraries () {
      return
      this.installedPythonLibraries.loading = true
      const vm = this
      SystemService.getInstalledPythonLibraries()
        .then(data => {
          vm.installedPythonLibraries.libraries.splice(0, vm.installedPythonLibraries.libraries.length)
          vm.installedPythonLibraries.libraries.push(...data)
        }).finally(() => {
          vm.installedPythonLibraries.loading = false
        })
    },
    setAction (actionValue) {
      if (this.modelValue.type === actionValue) {
        return
      }
      if (this.selectedFile) {
        this.$emit('update:selectedFile', null)
      }
      if (this.modelValue.fileName) {
        this.setValue('fileName', undefined)
      }
      this.v.modelValue.type.$model = actionValue
      if (actionValue === 'playAudio' && !this.modelValue.volume) {
        this.v.modelValue.volume.$model = 100
      }
      this.$emit('update:modelValue', this.modelValue)
    },
    setSelectedFile (file) {
      this.setValue('fileName', file ? file.name : undefined)
      this.$emit('update:selectedFile', file)
    },
    setValue (attribute, value) {
      this.v.modelValue[attribute].$model = value
      this.$emit('update:modelValue', this.modelValue)
    },
    onFilterExecGroups (inputValue, doneFn, abortFn) {
      this.currentExecutionGroupFilter = inputValue
      doneFn()
    },
    // Triggers if the new value field submits an new value through usage of the **enter button**
    onAddNewValueExecGroups (inputValue, doneFn) {
      for (const existingGroup of this.existingExecutionGroups) {
        if (existingGroup.toLowerCase() === inputValue.toLowerCase()) {
          doneFn(existingGroup, 'add-unique')
          return
        }
      }
      doneFn(inputValue, 'add-unique')
    },
    // Triggers if the new new value field of the execGroup has been **Clicked**
    addNewExecGroup (addGroup) {
      this.$refs.executionGroupsSelector.add(addGroup, true)
      this.$refs.executionGroupsSelector.updateInputValue('')
    }
  },
  validations () {
    const val = {
      modelValue: {
        comment: {
          maxLength: maxLength(40)
        },
        executionGroups: {},
        trigger: {
          required
        },
        type: {
          required
        },
        requestMethod: {},
        url: {},
        fileName: {},
        onRepeat: {},
        volume: {}
      }
    }
    if (this.modelValue.type === 'callUrl') {
      val.modelValue.requestMethod = {
        required
      }
      val.modelValue.url = {
        required,
        url,
        maxLength: maxLength(255)
      }
    }
    if (['execPy', 'playAudio'].some(x => x === this.modelValue.type)) {
      val.selectedFile = {}
      val.modelValue.fileName = {
        maxLength: maxLength(255)
      }
      if (!this.previousEventActionType || this.modelValue.type !== this.previousEventActionType) {
        val.selectedFile = {
          required
        }
        val.modelValue.fileName = {
          required,
          maxLength: maxLength(255)
        }
      }
    }
    if (this.modelValue.type === 'playAudio') {
      val.modelValue.onRepeat = {}
      val.modelValue.volume = {
        required,
        minValue: minValue(0),
        maxValue: maxValue(100)
      }
    }
    return val
  },
  watch: {
    'v.$invalid': {
      immediate: true,
      handler (value) {
        if (!value) {
          this.$emit('valid')
        } else {
          this.$emit('invalid')
        }
      }
    }
  },
  computed: {
    executionGroupOptions () {
      const executionGroupOptions = []
      if (!this.currentExecutionGroupFilter) {
        return this.existingExecutionGroups
      }
      for (const existingGroup of this.existingExecutionGroups) {
        if (existingGroup.toLowerCase().includes(this.currentExecutionGroupFilter.toLowerCase())) {
          executionGroupOptions.push(existingGroup)
        }
      }
      return executionGroupOptions
    },
    isExecutionGroupFilterUnique () {
      let unique = true
      if (this.modelValue.executionGroups) {
        unique &&= !this.modelValue.executionGroups
          .some(x => x.toLowerCase() === this.currentExecutionGroupFilter.toLowerCase())
      }
      unique &&= !this.executionGroupOptions
        .some(x => x.toLowerCase() === this.currentExecutionGroupFilter.toLowerCase())
      return unique
    },
    fileName () {
      if (
        this.previousEventActionType &&
        this.modelValue.type === this.previousEventActionType &&
        !this.modelValue.fileName
      ) {
        return this.previousFileName
      }
      return this.modelValue.fileName
    }
  }
}
</script>

<style scoped>

</style>
