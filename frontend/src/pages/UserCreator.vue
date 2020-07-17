<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="User Management" :to="{name: 'usermanagement'}" />
      <q-breadcrumbs-el label="Create user" />
    </q-breadcrumbs>
    <h5>Create user</h5>
    <q-card
      flat
    >
      <user-editor-form
        v-model="newUser"
        :loading="loading"
        @valid="isValid = true"
        @invalid="isValid = false"
      >
        <template slot="below">
          <div class="q-pa-md q-gutter-sm">
            <q-btn
              style="width: 100px"
              color="negative"
              label="Abort"
              no-caps
              :to="{name: 'usermanagement'}"
            />
            <q-btn
              type="submit"
              style="width: 100px"
              color="positive"
              label="Create"
              no-caps
              :disable="loading || !isValid"
              @click="createUser"
            />
          </div>
        </template>
      </user-editor-form>
    </q-card>
  </q-page>
</template>

<script>
  import UserEditorForm from "../components/UserEditorForm";
  import User from "../models/User"
  import userService from "../services/user.service"

  export default {
    name: "UserCreator",
    components: {UserEditorForm},
    data() {
      return {
        newUser: new User('', '', '',
          '', false, '', '', ['user']),
        isValid: false,
        loading: false
      }
    },
    methods: {
      createUser() {
        this.loading = true;
        userService.createUser(this.newUser)
          .then(() => {
            this.loading = false;
            this.$q.notify({
              type: 'positive',
              message: 'User created successfully'
            });
            this.$router.push({name: 'usermanagement'})
          }).catch(error => {
          this.loading = false;
          this.$q.notify({
            type: 'negative',
            message: 'Couldn\'t create user. Please try again later!'
          });
        })
      }
    }
  }
</script>

<style scoped>

</style>
