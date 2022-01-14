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
                val => !v.modelValue.comment.maxLength.$invalid || 'Max 40'
              ]"
      filled
      hide-bottom-space
      label="Comment"
      outlined
      @update:model-value="setValue('comment', $event)"
    />
    <q-select
      ref="executionGroupsSelector"
      :disable="disable || groupsLoading"
      :model-value="modelValue.executionGroups"
      :options="executionGroupOptions"
      filled
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
      filled
      label="Trigger"
      map-options
      @update:model-value="setValue('trigger', $event)"
    />
    <q-select
      :disable="disable || groupsLoading"
      :model-value="modelValue.type"
      :options="existingActions"
      emit-value
      filled
      label="Action"
      map-options
      @update:model-value="setAction($event)"
    />
    <transition
      appear
      enter-active-class="animated bounceIn"
    >
      <div>
        <q-splitter :model-value="10" />
        <q-tab-panels
          v-if="modelValue.type !== null"
          :model-value="modelValue.type"
          animated
        >
          <q-tab-panel
            name="callUrl"
          >
            demo1 Lorem ipsum bla bla bla
          </q-tab-panel>
          <q-tab-panel
            name="playAudio"
          >
            demo2 Lorem ipsum blubb blubb blubb
          </q-tab-panel>
          <q-tab-panel
            name="execPy"
          >
            demo2 Lorem ipsum blubb blubb blubb
          </q-tab-panel>
        </q-tab-panels>
      </div>
    </transition>
  </div>
</template>

<script>

import useVuelidate from '@vuelidate/core'
import EventActionService from '../services/eventaction.service'
import { maxLength, required } from '@vuelidate/validators'
import { eventActionTriggerDisplayNames } from '../mixins/constants'

export default {
  name: 'CEventActionEditorForm',
  mixins: [eventActionTriggerDisplayNames],
  setup () {
    return {
      v: useVuelidate()
    }
  },
  props: {
    modelValue: {
      type: Object,
      required: true
    },
    disable: {
      type: Boolean,
      default: false
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
      groupsLoading: false,
      existingExecutionGroups: [],
      currentExecutionGroupFilter: '',
      existingActions: [{
        label: 'Call URL',
        value: 'callUrl'
      }, {
        label: 'Play audio file',
        value: 'playAudio'
      }, {
        label: 'Execute python script',
        value: 'execPy'
      }]
    }
  },
  methods: {
    setAction (actionValue) {
      if (this.modelValue.type === actionValue) {
        return
      }
      this.v.modelValue.type.$model = actionValue
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
    return {
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
        }
      }
    }
  },
  watch: {
    'v.modelValue.$invalid': {
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
    }
  }
}
</script>

<style scoped>

</style>
