<template>
  <q-form
    ref="userEditorForm"
    class="innerpadding q-gutter-y-md"
    greedy
    @submit.prevent="$emit('submit')"
  >
    <q-input
      outlined
      :loading="loading"
      :disable="loading || disabled"
      :model-value="modelValue.username"
      :rules="[
        val => !v.modelValue.username.required.$invalid || 'Required',
        val => !v.modelValue.username.minLength.$invalid || 'Minimal length 3',
        val => !v.modelValue.username.maxLength.$invalid || 'Maximal length 20']"
      label="Username"
      @update:model-value="e => setValue('username', e)"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading || disabled"
      :model-value="modelValue.firstname"
      label="Firstname"
      :rules="[
        val => !v.modelValue.firstname.required.$invalid || 'Required',
        val => !v.modelValue.firstname.maxLength.$invalid || 'Maximal length 20']"
      @update:model-value="e => setValue('firstname', e)"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading || disabled"
      :model-value="modelValue.lastname"
      :rules="[
        val => !v.modelValue.lastname.required.$invalid || 'Required',
        val => !v.modelValue.lastname.maxLength.$invalid || 'Maximal length 20']"
      label="Lastname"
      @update:model-value="e => setValue('lastname', e)"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading || disabled"
      :model-value="modelValue.email"
      :rules="[
        val => !v.modelValue.email.required.$invalid || 'Required',
        val => !v.modelValue.email.email.$invalid || 'Email required',
        val => !v.modelValue.email.maxLength.$invalid || 'Maximal length 5']"
      @update:model-value="e => setValue('email', e)"
      label="E-Mail"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading || disabled"
      :model-value="v.modelValue.password.$model"
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
    <slot name="below"/>
  </q-form>
</template>

<script>
import { mdiEye, mdiEyeOff } from '@quasar/extras/mdi-v5'
import { email, maxLength, minLength, required } from '@vuelidate/validators'
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
    setValue(attribute, value) {
      this.v.modelValue[attribute].$model = value;
      this.$emit('update:modelValue', this.modelValue)
    }
  },
  data () {
    return {
      showPassword: false,
      roles: [
        {
          value: 0,
          label: 'User'
        }, {
          value: 1,
          label: 'Recipe-Creator'
        }, {
          value: 2,
          label: 'Pump-Ingredient-Editor'
        }, {
          value: 3,
          label: 'Admin'
        }
      ]
    }
  },
  validations () {
    const validations = {
      modelValue: {
        username: {
          required,
          minLength: minLength(3),
          maxLength: maxLength(20)
        },
        firstname: {
          required,
          maxLength: maxLength(20)
        },
        lastname: {
          required,
          maxLength: maxLength(20)
        },
        email: {
          required,
          email,
          maxLength: maxLength(50)
        },
        password: {
          minLength: minLength(6),
          maxLength: maxLength(40)
        },
        adminLevel: {
          required
        },
        accountNonLocked: {}
      }
    }
    if (this.passwordRequired) {
      validations.modelValue.password.required = required
    }
    return validations
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
  .innerpadding * {
    padding: 3px;
  }
</style>
