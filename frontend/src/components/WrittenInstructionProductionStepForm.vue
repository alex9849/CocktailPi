<template>
  <div>
    <q-input
      v-model.trim="value.message"
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

import {maxLength, required} from "vuelidate/lib/validators";

export default {
  name: "WrittenInstructionProductionStepForm",
  props: {
    value: {
      type: Object,
      required: false
    }
  },
  validations() {
    let validations = {
      value: {
        message: {
          required,
          maxLength: maxLength(500)
        },
      }
    };
    return validations;
  }
}
</script>

<style scoped>

</style>
