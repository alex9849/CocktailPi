<template>
  <q-card square bordered class="q-pa-lg shadow-1" style="min-width: 400px">
    <q-form
      @submit="onSubmit">
      <q-card-section style="text-align: center">
        <div class="text-h4">
          <img src="../assets/logo-full.svg" style="width: 80px"/>
          <p>{{ $t('page.login.headline') }}</p>
        </div>
      </q-card-section>
      <q-card-section>
        <transition
          appear
          enter-active-class="animated shakeX"
        >
          <q-banner :key="transitionTrigger" v-if="!!error" rounded dense class="text-white bg-red">
            {{ error }}
            <template v-slot:avatar>
              <q-icon :name="mdiAlert" color="bg-red"/>
            </template>
          </q-banner>
        </transition>
        <br/>
        <q-input
          v-if="!!$q.platform.is.cordova"
          label="Server address"
          placeholder="https://myserver.net/"
          :disable="loading"
          v-model="serverAddress"
          :rules="[
            val => v.serverAddress.required || $t('errors.field_required'),
            val => v.serverAddress.isURL || $t('errors.not_valid_url')
            ]"
          outlined
          lazy-rules
          @input="v.serverAddress.$touch()"
        >
          <template v-slot:prepend>
            <q-icon :name="mdiServer"/>
          </template>
        </q-input>
        <q-input
          v-if="!passwordOnlyLogin"
          :disable="loading"
          outlined
          :label="$t('page.login.username_field_label')"
          v-model="v.loginRequest.username.$model"
          lazy-rules
          :rules="[
              val => !v.loginRequest.username.$error || $t('errors.field_required')
            ]"
        >
          <template v-slot:prepend>
            <q-icon :name="mdiEmail"/>
          </template>
        </q-input>
        <q-input
          :disable="loading"
          outlined
          :label="$t('page.login.password_field_label')"
          type="password"
          v-model="v.loginRequest.password.$model"
          lazy-rules
          :rules="[
              val => !v.loginRequest.password.$error || $t('errors.field_required')
            ]"
        >
          <template v-slot:prepend>
            <q-icon :name="mdiOnepassword"/>
          </template>
        </q-input>
        <q-checkbox
          :label="$t('page.login.remember_me_label')"
          v-if="!$q.platform.is.cordova"
          v-model="loginRequest.remember"
        />
      </q-card-section>
      <q-separator/>
      <q-card-section align="center">
        <q-btn
          :loading="loading"
          size="lg"
          class="full-width"
          type="submit"
          color="primary"
          :disable="disableLogin"
        >
          {{ $t('page.login.login_btn_label') }}
        </q-btn>
      </q-card-section>
    </q-form>
  </q-card>
</template>

<script>
import LoginRequest from '../models/LoginRequest'
import { mdiAlert, mdiEmail, mdiOnepassword, mdiServer } from '@quasar/extras/mdi-v5'
import { helpers, required } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'
import { mapGetters } from 'vuex'

export default {
  name: 'CLoginCard',
  emits: ['loginSuccess'],
  data () {
    return {
      loginRequest: new LoginRequest('', '', !!this.$q.platform.is.cordova),
      loading: false,
      error: '',
      disableLogin: true,
      transitionTrigger: false
    }
  },
  setup () {
    return {
      v: useVuelidate(),
      mdiEmail,
      mdiOnepassword,
      mdiAlert,
      mdiServer
    }
  },
  validations () {
    const isURL = helpers.regex('isURL', /^(?:http(s)?:\/\/)?((localhost)|([\w.-]+(?:\.[\w.-]+)+))(:([1-9]\d{3,4}))?$/gi)
    const serverAddress = {
      required,
      isURL
    }
    const validations = {
      loginRequest: {
        username: {
          requiredIf: () => !this.passwordOnlyLogin
        },
        password: {
          required
        }
      }
    }
    if (this.$q.platform.is.cordova) {
      validations.serverAddress = serverAddress
    }
    return validations
  },
  methods: {
    onSubmit () {
      this.loading = true
      this.$store.dispatch('auth/login', this.loginRequest)
        .then(() => {
          this.$emit('loginSuccess')
          this.loading = false
        }).catch(err => {
          this.loading = false
          if (!!err.response && err.response.status === 401) {
            this.showError(this.$t('page.login.errors.credentials_invalid'))
          } else {
            this.showError(this.$t('page.login.errors.server_unreachable'))
          }
        })
    },
    showError (error) {
      this.error = error
      this.transitionTrigger = !this.transitionTrigger
    }
  },
  watch: {
    'v.$invalid': function _watchv$invalid (value) {
      this.disableLogin = value
    }
  },
  computed: {
    ...mapGetters({
      passwordOnlyLogin: 'appearance/getPasswordOnlyLogin'
    }),
    serverAddress: {
      get () {
        return this.$store.getters['auth/getServerAddress']
      },
      set (value) {
        this.$store.dispatch('auth/serverAddress', value)
      }
    }
  }
}
</script>

<style>
</style>
