<template>
  <div>
    <q-input
      v-model="value.message"
      @input="() => {$emit('input', value); $v.value.message.$touch();}"
      tcype="textarea"
      label="Instruction"
      counter
      outlined
      autogrow
      :rules="[
        val => $v.value.message.required || 'Required',
        val => $v.value.message.maxLength || 'Max 500'
      ]"
    />
  </div>
</template>

<script>

import { maxLength, required } from 'vuelidate/lib/validators'

export default {
  name: 'WrittenInstructionProductionStepForm',
  props: {
    value: {
      type: Object,
      required: false
    }
  },
  emits: ['input', 'valid', 'invalid'],
  watch: {
    value: {
      immediate: true,
      handler () {
        this.$v.value.$touch()
        if (this.$v.value.$invalid) {
          this.$emit('invalid')
        } else {
          this.$emit('valid')
        }
      }
    },
    '$v.value.$invalid': function _watch$vValue$invalid (value) {
      if (!value) {
        this.$emit('valid')
      } else {
        this.$emit('invalid')
      }
    }
  },
  validations () {
    const validations = {
      value: {
        message: {
          required,
          maxLength: maxLength(500)
        }
      }
    }
    return validations
  }
}
</script>

<style scoped>

</style>
