<template>
  <q-input
    outlined
    :label="label"
    readonly
    :dark="dark"
    :model-value="modelValue"
  >
    <template v-slot:append>
      <q-btn
        icon="colorize"
        label="Pick"
        no-caps
        :style="{backgroundColor: modelValue, color: textColor}"
      >
        <q-popup-proxy
          transition-show="scale"
          transition-hide="scale"
        >
          <q-color
            no-header-tabs
            format-model="hex"
            :model-value="modelValue"
            @update:modelValue="$emit('update:modelValue', $event)"
          />
        </q-popup-proxy>
      </q-btn>
    </template>
  </q-input>
</template>

<script>
import { calcTextColor } from 'src/mixins/utils'

export default {
  name: 'CColorSelectorField',
  props: {
    modelValue: {
      required: true
    },
    label: {
      type: String
    },
    dark: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:modelValue'],
  computed: {
    textColor () {
      return calcTextColor(this.modelValue)
    }
  }
}
</script>

<style scoped>

</style>
