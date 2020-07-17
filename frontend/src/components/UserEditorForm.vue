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
      :disable="loading"
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
      :disable="loading"
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
      :disable="loading"
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
      :disable="loading"
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
      :disable="loading"
      v-model="value.password"
      @input="() => {$emit('input', value); $v.value.password.$touch();}"
      :rules="[
        val => $v.value.password.minLength || 'Minimal length 6',
        val => $v.value.password.minLength || 'Maximal length 40']"
      label="Password"
      :type="showPassword? 'text':'password'"
    >
      <template v-slot:append>
        <q-icon :name="showPassword? mdiEyeOff:mdiEye" @click="showPassword = !showPassword"/>
      </template>
    </q-input>
    <q-checkbox
      :value="isAdmin"
      @input="change => {if(value.role) {change? value.role.push('admin'):value.role = value.role.filter(e => e !== 'admin'); $emit('input', value)}}"
      :disable="loading"
      label="Admin-permissions"
    />
    <q-checkbox
      v-model="value.locked"
      :disable="loading"
      label="Locked"
      @input="$emit('input', value)"
    />
    <slot name="below"/>
  </q-form>
</template>

<script>
  import {mdiEye, mdiEyeOff} from '@mdi/js';
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
      }
    },
    data() {
      return {
        showPassword: false
      }
    },
    validations: {
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
        }
      }
    },
    watch: {
      '$v.value.$invalid': function _watch$vValue$invalid(value) {
        if(!value) {
          this.$emit('valid');
        } else {
          this.$emit('invalid');
        }
      }
    },
    computed: {
      isAdmin() {
        if (!this.value.role) {
          return undefined;
        }
        return this.value.role.includes('admin')
      }
    },
    created() {
      this.emailString = emailString;
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
