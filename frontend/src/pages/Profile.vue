<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="My Profile"/>
    </q-breadcrumbs>
    <h5>My profile</h5>
    <q-banner v-if="error !== ''" rounded dense class="text-white bg-red-5" style="margin: 3px">
      {{ error }}
    </q-banner>
    <user-editor-form
      v-model="editUser"
      :loading="loading"
      :disabled="!editMode || sending"
      is-profile
      @valid="isValid = true"
      @invalid="isValid = false"
    >
      <template v-slot:below>
        <div class="q-ma-y-md" style="margin-left: 4px">
          <q-btn
            style="width: 100px"
            color="grey"
            no-caps
            v-if="!editMode"
            @click="editMode = true"
          >
            Edit
          </q-btn>
          <q-btn
            style="width: 100px; margin-right: 10px"
            color="negative"
            no-caps
            :disable="sending"
            v-if="editMode"
            @click="stopEditMode"
          >
            Abort
          </q-btn>
          <q-btn
            style="width: 100px"
            color="positive"
            @click="sendUpdateUser"
            no-caps
            :loading="sending"
            :disable="!isValid"
            v-if="editMode"
          >
            Save
          </q-btn>
        </div>
      </template>
    </user-editor-form>
  </q-page>
</template>

<script>
  import UserEditorForm from "../components/UserEditorForm";
  import userService from "../services/user.service"

  export default {
    name: "Profile",
    components: {UserEditorForm},
    data() {
      return {
        user: {},
        editUser: {},
        loading: false,
        sending: false,
        editMode: false,
        isValid: false,
        error: ''
      }
    },
    created() {
      this.loading = true;
      userService.getMe()
        .then(user => {
          user.password = '';
          this.user = user;
          this.editUser = Object.assign({}, user);
          this.loading = false;
        })
    },
    methods: {
      getRandomString(length) {
        var randomChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        var result = '';
        for (var i = 0; i < length; i++) {
          result += randomChars.charAt(Math.floor(Math.random() * randomChars.length));
        }
        return result;
      },
      stopEditMode() {
        this.editMode = false;
        this.editUser = Object.assign({}, this.user);
      },
      sendUpdateUser() {
        this.sending = true;
        let updateUser = Object.assign({}, this.editUser);
        let updatePassword = !!this.editUser.password || this.editUser.password !== '';
        if (!updatePassword) {
          updateUser.password = this.getRandomString(22);
        }
        userService.updateMe({
          updatePassword,
          userDto: updateUser
        }).then(() => {
          this.sending = false;
          this.stopEditMode();
          this.error = '';
          this.$q.notify({
            type: 'positive',
            message: 'Profile updated'
          });
        }).catch(error => {
          this.sending = false;
          this.error = error.response.data.message;
          this.$q.notify({
            type: 'negative',
            message: 'Couldn\'t update profile. ' + error.response.data.message
          });
        })
      }
    }
  }
</script>

<style scoped>

</style>
