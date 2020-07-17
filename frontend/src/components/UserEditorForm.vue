<template>
  <q-form
    class="innerpadding q-gutter-y-sm"
    @submit.prevent="$emit('submit')"
  >
    <q-input
      outlined
      :loading="loading"
      :disable="loading"
      v-model="value.username"
      :lazy-rules="true"
      :rules="[v => minLengthString(v, 3), v => maxLengthString(v, 20), notEmptyString]"
      @input="$emit('input', value)"
      label="Username"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading"
      :lazy-rules="true"
      v-model="value.firstname"
      :rules="[v => maxLengthString(v, 20),notEmptyString]"
      label="Firstname"
      @input="$emit('input', value)"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading"
      v-model="value.lastname"
      :lazy-rules="true"
      :rules="[v => maxLengthString(v, 20),notEmptyString]"
      label="Lastname"
      @input="$emit('input', value)"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading"
      v-model="value.email"
      :lazy-rules="true"
      :rules="[v => maxLengthString(v, 50), notEmptyString, emailString]"
      @input="$emit('input', value)"
      label="E-Mail"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading"
      :lazy-rules="true"
      v-model="value.password"
      :rules="[v => minLengthString(v, 6), v => maxLengthString(v, 40)]"
      @input="$emit('input', value)"
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
  import {emailString, maxLengthString, minLengthString, notEmptyString} from "../services/formRules";
  import {mdiEye, mdiEyeOff} from '@mdi/js';

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
    computed: {
      isAdmin() {
        if(!this.value.role) {
          return undefined;
        }
        return this.value.role.includes('admin')
      }
    },
    created() {
      this.notEmptyString = notEmptyString;
      this.minLengthString = minLengthString;
      this.maxLengthString = maxLengthString;
      this.emailString = emailString;
      this.mdiEye = mdiEye;
      this.mdiEyeOff = mdiEyeOff;
    }
  }
</script>

<style scoped>
  .innerpadding * {
    padding: 5px;
  }
</style>
