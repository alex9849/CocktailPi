<template>
  <div>
    <q-input
      :model-value="modelValue.message"
      :rules="[
        val => !v.modelValue.message.required.$invalid || $t('errors.field_required'),
        val => !v.modelValue.message.maxLength.$invalid || $t('errors.max_letters', {nr: 500})
      ]"
      tcype="textarea"
      :label="$t('component.prod_step_editor_instruction.instruction_label')"
      counter
      outlined
      autogrow
      @update:model-value="v.modelValue.message.$model = $event; $emit('update:modelValue', modelValue)"
    />
  </div>
</template>

<script>

import { maxLength, required } from '@vuelidate/validators'
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
