<template>
  <q-page class="backgroundImage window-height window-width row justify-center items-center">
    <q-card square bordered class="q-pa-lg shadow-1" style="min-width: 400px">
      <q-form
        @submit="onSubmit">
        <q-card-section style="text-align: center">
          <div class="text-h4">
            <img src="../assets/logo-full.svg" width="80px"/>
            <p>Login</p>
          </div>
        </q-card-section>
        <q-card-section>
          <transition
            appear
            enter-active-class="animated shakeX"
          >
            <q-banner :key="transitionTrigger" v-if="passwordWrong" rounded dense class="text-white bg-red">
              Username or password wrong!
              <template v-slot:avatar>
                <q-icon :name="mdiAlert" color="bg-red"/>
              </template>
            </q-banner>
          </transition>
          <br/>
          <q-input
            :disable="loading"
            filled
            label="Username"
            v-model="loginRequest.username"
            lazy-rules
            :rules="[ val => val && val.length > 0 || 'Username required']"
          >
            <template v-slot:prepend>
              <q-icon :name="mdiEmail"/>
            </template>
          </q-input>
          <q-input
            :disable="loading"
            filled
            label="Password"
            type="password"
            v-model="loginRequest.password"
            lazy-rules
            :rules="[ val => val && val.length > 0 || 'Password required']"
          >
            <template v-slot:prepend>
              <q-icon :name="mdiOnepassword"/>
            </template>
          </q-input>
          <q-checkbox
            label="Remember me"
            v-model="loginRequest.remember"
          />
        </q-card-section>
        <q-separator/>
        <q-card-section align="center">
          <q-btn :loading="loading" size="lg" class="full-width" type="submit" color="primary">Login</q-btn>
        </q-card-section>
      </q-form>
    </q-card>
  </q-page>
</template>

<script>
  import LoginRequest from '../models/LoginRequest';
  import {mdiAlert, mdiEmail, mdiOnepassword} from '@quasar/extras/mdi-v5';

  export default {
    name: "Login",
    data() {
      return {
        loginRequest: new LoginRequest('', '', false),
        loading: false,
        passwordWrong: false,
        transitionTrigger: false
      }
    },
    created() {
      this.mdiEmail = mdiEmail;
      this.mdiOnepassword = mdiOnepassword;
      this.mdiAlert = mdiAlert;
    },
    methods: {
      onSubmit() {
        this.loading = true;
        this.$store.dispatch('auth/login', this.loginRequest)
          .then(() => {
            this.loading = false;
            if(this.$route.query.redirectTo) {
              this.$router.push(this.$route.query.redirectTo);
            } else {
              this.$router.push({name: 'dashboard'});
            }
          }).catch(err => {
            this.loading = false;
            this.showPasswordWrong();
        });
      },
      showPasswordWrong() {
        this.passwordWrong = true;
        this.transitionTrigger = !this.transitionTrigger;
      }
    }
  }
</script>

<style scoped>
  .backgroundImage {
    background: url('../assets/login-background.jpg') no-repeat center center fixed;
    -webkit-background-size: cover;
    -moz-background-size: cover;
    -o-background-size: cover;
    background-size: cover;
  }
</style>
