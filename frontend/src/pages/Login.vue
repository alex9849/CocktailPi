<template>
    <div class="q-pa-md">
      <div class="row">
        <div class="col"/>
        <div class="col-5" style="max-width: 500px">
          <q-card square bordered class="q-pa-lg shadow-1">
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
                  :rules="[ val => val && val.length > 0 || 'Username required']"
                />
              </q-card-section>
              <q-separator/>
              <q-card-actions align="center">
                <q-btn size="lg" class="full-width" unelevated type="submit" color="primary">Login</q-btn>
              </q-card-actions>
            </q-form>
          </q-card>
        </div>
        <div class="col"/>
      </div>
    </div>

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
