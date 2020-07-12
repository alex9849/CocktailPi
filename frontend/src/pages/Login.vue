<template>
    <q-page>
      <div class="window-height window-width row justify-center items-center">
      <q-card square bordered class="q-pa-lg shadow-1" style="min-width: 400px">
        <q-form
          @submit="onSubmit">
          <q-card-section style="text-align: center">
            <div class="text-h4">
              Login
            </div>
          </q-card-section>
          <q-card-section>
            <q-input
              filled
              label="Username"
              v-model="user.username"
              lazy-rules
              :rules="[ val => val && val.length > 0 || 'Username required']"
            />

            <q-input
              filled
              label="Password"
              type="password"
              v-model="user.password"
              v-validate="'required'"
              lazy-rules
              :rules="[ val => val && val.length > 0 || 'Password required']"
            />
          </q-card-section>
          <q-separator/>
          <q-card-section align="center">
            <q-btn size="lg" class="full-width"  type="submit" color="primary">Login</q-btn>
          </q-card-section>
        </q-form>
      </q-card>
      </div>
    </q-page>
</template>

<script>
  import User from '../models/user';

  export default {
    name: "Login",
    data() {
      return {
        user: new User('', '', ''),
        loading: false
      }
    },
    methods: {
      onSubmit() {

      }
    }
  }
</script>

<style scoped>

</style>
