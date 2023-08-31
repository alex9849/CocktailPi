<template>
  <q-form
    ref="userEditorForm"
    class="q-gutter-y-md"
    greedy
    @submit.prevent="$emit('submit')"
  >
    <q-input
      outlined
      :loading="loading"
      :disable="loading || disabled"
      :model-value="editUs"
      hide-bottom-space
      label="Username"
      @update:model-value="e => setValue('username', e)"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading || disabled"
      :model-value="modelValue.password"
      hide-bottom-space
      :rules="[
        val => !passwordRequired || !v.modelValue.password.required.$invalid || 'Required',
        val => !v.modelValue.password.minLength.$invalid || 'Minimal length 6',
        val => !v.modelValue.password.minLength.$invalid || 'Maximal length 40']"
      @update:model-value="e => setValue('password', e)"
      label="Password"
      :type="showPassword? 'text':'password'"
    >
      <template v-slot:append>
        <q-icon :name="showPassword? mdiEyeOff:mdiEye" @click="showPassword = !showPassword"/>
      </template>
    </q-input>
    <q-select
      outlined
      map-options
      hide-bottom-space
      :model-value="modelValue.adminLevel"
      v-if="!isSelf"
      :options="roles"
      @update:model-value="e => setValue('adminLevel', e.value)"
      :disable="loading || disabled"
      label="Role"
    />
    <q-checkbox
      v-if="!isSelf"
      :model-value="!modelValue.accountNonLocked"
      @update:model-value="e => setValue('accountNonLocked', !e)"
      :disable="loading || disabled"
      label="Locked"
    />
  </q-form>
</template>

<script>
import { mdiEye, mdiEyeOff } from '@quasar/extras/mdi-v5'
import { maxLength, minLength, required } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'UserEditorForm',
  props: {
    modelValue: {
      type: Object,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    isSelf: {
      type: Boolean,
      default: false
    },
    passwordRequired: {
      type: Boolean,
      default: false
    }
  },
  emits: ['submit', 'update:modelValue', 'valid', 'invalid'],
  setup () {
    return {
      v: useVuelidate(),
      mdiEye: mdiEye,
      mdiEyeOff: mdiEyeOff
    }
  },
  methods: {
    setValue (attribute, value) {
      this.v.modelValue[attribute].$model = value
      this.$emit('update:modelValue', this.modelValue)
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
  }
}
</script>

<style scoped>
</style>
