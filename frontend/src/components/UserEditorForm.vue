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
      v-model="value.username"
      @input="() => {$emit('input', value); $v.value.username.$touch();}"
      label="Username"
      :rules="[
        val => $v.value.username.required || 'Required',
        val => $v.value.username.minLength || 'Minimal length 3',
        val => $v.value.username.maxLength || 'Maximal length 20']"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading || disabled"
      v-model="value.firstname"
      label="Firstname"
      @input="() => {$emit('input', value); $v.value.firstname.$touch();}"
      :rules="[
        val => $v.value.firstname.required || 'Required',
        val => $v.value.firstname.maxLength || 'Maximal length 20']"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading || disabled"
      v-model="value.lastname"
      @input="() => {$emit('input', value); $v.value.lastname.$touch();}"
      label="Lastname"
      :rules="[
        val => $v.value.lastname.required || 'Required',
        val => $v.value.lastname.maxLength || 'Maximal length 20']"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading || disabled"
      v-model="value.email"
      @input="() => {$emit('input', value); $v.value.email.$touch();}"
      :rules="[
        val => $v.value.email.required || 'Required',
        val => $v.value.email.email || 'Email required',
        val => $v.value.email.maxLength || 'Maximal length 5']"
      label="E-Mail"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading || disabled"
      v-model="value.password"
      @input="() => {$emit('input', value); $v.value.password.$touch();}"
      :rules="[
        val => !passwordRequired || $v.value.password.required || 'Required',
        val => $v.value.password.minLength || 'Minimal length 6',
        val => $v.value.password.minLength || 'Maximal length 40']"
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
      :value="value.adminLevel"
      v-if="!isProfile && !isSelf"
      :options="roles"
      @input="e => {value.adminLevel = e.value; $emit('input', value); $v.value.adminLevel.$touch();}"
      :disable="loading || disabled"
      label="Role"
    />
    <q-checkbox
      v-if="!isProfile && !isSelf"
      :value="!value.accountNonLocked"
      @input="e => {value.accountNonLocked = !e; $emit('input', value)}"
      :disable="loading || disabled"
      label="Locked"
    />
    <slot name="below"/>
  </q-form>
</template>

<script>
  import {mdiEye, mdiEyeOff} from '@quasar/extras/mdi-v5';
  import {email, maxLength, minLength, required} from 'vuelidate/lib/validators'

  export default {
    name: "UserEditorForm",
    props: {
      value: {
        type: Object,
        required: true,
      },
      loading: {
        type: Boolean,
        default: false
      },
      disabled: {
        type: Boolean,
        default: false
      },
      isProfile: {
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
    data() {
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
    validations() {
      let validations = {
        value: {
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
          }
        }
      };
      if(this.passwordRequired) {
        validations.value.password.required = required;
      }
      return validations;
    },
    watch: {
      '$v.value.$invalid': function _watch$vValue$invalid(value) {
        if (!value) {
          this.$emit('valid');
        } else {
          this.$emit('invalid');
        }
      }
    },
    created() {
      this.mdiEye = mdiEye;
      this.mdiEyeOff = mdiEyeOff;
    }
  }
</script>

<style scoped>
  .innerpadding * {
    padding: 3px;
  }
</style>
