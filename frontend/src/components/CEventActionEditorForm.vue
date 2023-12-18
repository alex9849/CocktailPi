<template>
  <div
    class="q-gutter-y-sm"
  >
    <q-inner-loading
      :showing="initializing"
    />
    <q-input
      :disable="disable || initializing"
      :model-value="modelValue.comment"
      :rules="[
                val => !v.modelValue.comment.maxLength.$invalid || $t('errors.max_letters', {nr: 40})
              ]"
      filled
      hide-bottom-space
      :label="$t('component.event_action_editor_form.comment_label')"
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
      :disable="disable || initializing"
      :model-value="modelValue.executionGroups"
      :options="executionGroupOptions"
      filled
      hide-bottom-space
      input-debounce="0"
      :label="$t('component.event_action_editor_form.execution_groups.label')"
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
              <div
                class="inline-block"
                v-html="$t('component.event_action_editor_form.execution_groups.create_new', {group: currentExecutionGroupFilter})"
              />
            </q-item-section>
          </q-item>
          <q-item v-else>
            <q-item-section>
              <div class="text-grey-7 text-italic text-center">
                {{ $t('component.event_action_editor_form.execution_groups.write_to_create') }}
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
              <div
                class="inline-block"
                v-html="$t('component.event_action_editor_form.execution_groups.create_new', {group: currentExecutionGroupFilter})"
              />
            </q-item-section>
          </q-item>
          <q-item v-else>
            <q-item-section>
              <div class="text-grey-7 text-italic text-center">
                {{ $t('component.event_action_editor_form.execution_groups.write_to_create') }}
              </div>
            </q-item-section>
          </q-item>
        </div>
      </template>
    </q-select>
    <q-select
      :disable="disable || initializing"
      :model-value="modelValue.trigger"
      :options="eventActionTriggerDisplayNames"
      emit-value
      hide-bottom-space
      filled
      :label="$t('component.event_action_editor_form.trigger_label')"
      map-options
      @update:model-value="setValue('trigger', $event)"
      :rules="[
                val => !v.modelValue.trigger.required.$invalid || $t('errors.field_required')
              ]"
    />
    <q-select
      :disable="disable || initializing"
      :model-value="modelValue.type"
      :options="existingActions"
      emit-value
      hide-bottom-space
      filled
      :label="$t('component.event_action_editor_form.action.label')"
      map-options
      @update:model-value="setAction($event)"
      :rules="[
                val => !v.modelValue.type.required.$invalid || $t('errors.field_required')
              ]"
    />
    <q-separator />
    <q-card
      bordered
      flat
    >
      <q-card-section
        v-if="!modelValue.type"
        class="text-italic text-grey-7"
      >
        {{ $t('component.event_action_editor_form.action.select_action') }}
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
              :label="$t('component.event_action_editor_form.action.call_url.request_method_label')"
              @update:model-value="setValue('requestMethod', $event)"
              :rules="[
                val => !v.modelValue.requestMethod.required.$invalid || $t('errors.field_required')
              ]"
            />
            <q-input
              :model-value="modelValue.url"
              filled
              hide-bottom-space
              :label="$t('component.event_action_editor_form.action.call_url.url_label')"
              placeholder="https://google.com"
              @update:model-value="setValue('url', $event)"
              :rules="[
                val => !v.modelValue.url.required.$invalid || $t('errors.field_required'),
                val => !v.modelValue.url.url.$invalid || $t('errors.not_valid_url'),
                val => !v.modelValue.url.maxLength.$invalid || $t('errors.max_letters', {nr: 255})
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
                    :label="$t('component.event_action_editor_form.action.audio.file_label')"
                    max-file-size="20971520"
                    @update:model-value="setSelectedFile($event)"
                    :rules="[
                      val => !v.selectedFile.required || !v.selectedFile.required.$invalid || $t('errors.field_required')
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
                  :label-value="$t('component.event_action_editor_form.action.audio.volume', {nr: modelValue.volume})"
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
            <q-select
              :model-value="modelValue.soundDevice"
              :options="audioDevices"
              :rules="[
                val => !v.modelValue.soundDevice.required.$invalid || $t('errors.field_required')
              ]"
              filled
              hide-bottom-space
              :label="$t('component.event_action_editor_form.action.audio.output_device_label')"
              @update:model-value="setValue('soundDevice', $event)"
            />
            <q-checkbox
              :model-value="!!modelValue.onRepeat"
              :label="$t('component.event_action_editor_form.action.audio.on_repeat_label')"
              @update:model-value="setValue('onRepeat', $event)"
            />
          </q-tab-panel>
          <q-tab-panel
            name="execPy"
            class="q-gutter-y-sm q-px-none"
          >
            <p>
              {{ $t('component.event_action_editor_form.action.python.desc') }}
            </p>
            <p
              class="q-pa-sm"
              v-html="$t('component.event_action_editor_form.action.python.install_lib_tip')"
            />
            <q-card bordered flat>
              <q-expansion-item
                v-model="installedPythonLibraries.loaded"
                label="Installed python libraries"
                @show="getInstalledPythonLibraries()"
              >
                <q-inner-loading
                  :showing="installedPythonLibraries.loading"
                  :label="$t('component.event_action_editor_form.action.python.fetching_libs')"
                  style="display: contents"
                  transition-hide="none"
                />
                <div v-if="!installedPythonLibraries.loading">
                  <q-separator />
                  <table
                    v-if="installedPythonLibraries.libraries.length !== 0"
                    style="width: 100%"
                  >
                    <thead>
                    <tr>
                      <th>
                        {{ $t('component.event_action_editor_form.action.python.lib_list_lib') }}
                      </th>
                      <th>
                        {{ $t('component.event_action_editor_form.action.python.lib_list_version') }}
                      </th>
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
                    {{ $t('component.event_action_editor_form.action.python.no_libs_found') }}
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
                    :label="$t('component.event_action_editor_form.action.python.python_file_label')"
                    max-file-size="20971520"
                    @update:model-value="setSelectedFile($event)"
                    :rules="[
                      val => !v.selectedFile.required || !v.selectedFile.required.$invalid || $t('errors.field_required')
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
            {{ $t('component.event_action_editor_form.action.nothing.desc') }}
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
      mdiInformation
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
    this.initializing = true
    const execGroupsPromise = EventActionService.getAllExecutionGroups()
      .then(groups => {
        this.existingExecutionGroups = groups
      })
    const audioDevices = SystemService.getAudioDevices()
      .then(devices => {
        this.audioDevices = devices
      })
    Promise.all([execGroupsPromise, audioDevices])
      .then(() => {
        this.initializing = false
      })
  },
  data () {
    return {
      installedPythonLibraries: {
        libraries: [],
        loading: false
      },
      showHelpExecutionGroup: false,
      initializing: false,
      existingExecutionGroups: [],
      audioDevices: [],
      currentExecutionGroupFilter: '',
      urlRequestMethods: ['GET', 'POST', 'PUT', 'DELETE'],
      existingActions: [{
        label: this.$t('component.event_action_editor_form.action.options.call_url'),
        value: 'callUrl'
      }, {
        label: this.$t('component.event_action_editor_form.action.options.audio'),
        value: 'playAudio'
      }, {
        label: this.$t('component.event_action_editor_form.action.options.python'),
        value: 'execPy'
      }, {
        label: this.$t('component.event_action_editor_form.action.options.nothing'),
        value: 'doNothing'
      }]
    }
  },
  methods: {
    getInstalledPythonLibraries () {
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
        soundDevice: {},
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
      val.modelValue.soundDevice = {
        required
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
