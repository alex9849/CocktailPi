<template>
  <div>
    <q-input
      :model-value="modelValue.message"
      :rules="[
        val => !v.modelValue.message.required.$invalid || 'Required',
        val => !v.modelValue.message.maxLength.$invalid || 'Max 500'
      ]"
      tcype="textarea"
      label="Instruction"
      counter
      outlined
      autogrow
      @update:model-value="v.modelValue.message.$model = $event; $emit('update:modelValue', modelValue)"
    />
  </div>
</template>

<script>

import {maxLength, required} from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'WrittenInstructionProductionStepForm',
  props: {
    modelValue: {
      type: Object,
      required: false
    }
  },
  emits: ['update:modelValue', 'valid', 'invalid'],
  setup () {
    return { v: useVuelidate() }
  },
  watch: {
    'v.modelValue.$invalid': {
      handler (value) {
        if (!value) {
          this.$emit('valid')
        } else {
          this.$emit('invalid')
        }
      }
    }
  },
  validations () {
    return {
      modelValue: {
        message: {
          required,
          maxLength: maxLength(500)
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
