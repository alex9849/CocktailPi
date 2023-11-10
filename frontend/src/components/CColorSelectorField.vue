<template>
  <q-input
    outlined
    :label="label"
    readonly
    :model-value="modelValue"
  >
    <template v-slot:append>
      <q-btn
        icon="colorize"
        label="Pick"
        no-caps
        :style="{backgroundColor: modelValue}"
      >
        <q-popup-proxy
          transition-show="scale"
          transition-hide="scale">
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
export default {
  name: 'CColorSelectorField',
  props: {
    modelValue: {
      required: true
    },
    label: {
      type: String
    }
  },
  emits: ['update:modelValue']
}
</script>

<style scoped>

</style>
