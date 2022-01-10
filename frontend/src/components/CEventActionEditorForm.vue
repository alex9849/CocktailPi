<template>
  <div
    class="q-gutter-y-sm"
  >
    <q-select
      :disable="disable"
      :model-value="modelValue.trigger"
      :options="triggerOptions"
      emit-value
      filled
      label="Trigger"
      map-options
      @update:model-value="setValue('trigger', $event)"
    />
    <transition
      appear
      enter-active-class="animated bounceIn"
    >
      <div>
        <q-splitter :model-value="10" />
        <q-tab-panels
          v-if="modelValue.trigger !== null"
          :model-value="modelValue.trigger"
          animated
        >
          <q-tab-panel
            name="demo1"
          >
            demo1 Lorem ipsum bla bla bla
          </q-tab-panel>
          <q-tab-panel
            name="demo2"
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
import { required } from '@vuelidate/validators'

export default {
  name: 'CEventActionEditorForm',
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
  data () {
    return {
      triggerOptions: [{
        label: 'Demo 1',
        value: 'demo1'
      }, {
        label: 'Demo 2',
        value: 'demo2'
      }]
    }
  },
  methods: {
    setValue (attribute, value) {
      this.v.modelValue[attribute].$model = value
      this.$emit('update:modelValue', this.modelValue)
    }
  },
  validations () {
    return {
      modelValue: {
        trigger: {
          required
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
