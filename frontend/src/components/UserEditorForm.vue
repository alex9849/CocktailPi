<template>
  <q-form class="innerpadding q-gutter-y-sm">
    <q-input
      outlined
      :loading="loading"
      :disable="loading"
      v-model="value.username"
      @input="$emit('input', value)"
      label="Username"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading"
      v-model="value.firstname"
      label="Firstname"
      @input="$emit('input', value)"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading"
      v-model="value.lastname"
      label="Lastname"
      @input="$emit('input', value)"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading"
      v-model="value.email"
      @input="$emit('input', value)"
      label="E-Mail"
    />
    <q-input
      outlined
      :loading="loading"
      :disable="loading"
      v-model="value.password"
      @input="$emit('input', value)"
      label="Password"
      :type="showPassword? 'text':'password'"
    >
      <template v-slot:append>
        <q-icon :name="showPassword? mdiEyeOff:mdiEye" @click="showPassword = !showPassword"/>
      </template>
    </q-input>
    <q-checkbox
      v-model="value.adminPermissions"
      :disable="loading"
      label="Admin-permissions"
      @input="$emit('input', value)"
    />
    <q-checkbox
      v-model="value.locked"
      :disable="loading"
      label="Locked"
      @input="$emit('input', value)"
    />
  </q-form>
</template>

<script>
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
    methods: {},
    created() {
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
