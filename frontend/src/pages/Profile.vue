<template>
  <q-page class="page-content" padding>
    <h5>My profile</h5>
    <q-banner v-if="error !== ''" rounded dense class="text-white bg-red-5" style="margin: 3px">
      {{ error }}
    </q-banner>
    <user-editor-form
      v-model="editUser"
      :loading="loading"
      :disabled="!editMode || sending"
      is-self
      @valid="isValid = true"
      @invalid="isValid = false"
    >
      <template v-slot:below>
        <div class="q-ma-y-md" style="margin-left: 4px">
          <q-btn
            style="width: 100px"
            color="grey"
            no-caps
            :disable="loading"
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
            @click="onClickSafe"
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
import {mapActions, mapGetters} from "vuex";

export default {
  name: "Profile",
  components: {UserEditorForm},
  data() {
    return {
      editUser: {},
      loading: false,
      sending: false,
      editMode: false,
      isValid: false,
      error: ''
    }
  },
  computed: {
    ...mapGetters({
      user: 'auth/getUser'
    })
  },
  watch: {
    user: {
      immediate: true,
      handler() {
        this.resetEditUser()
      }
    }
  },
  methods: {
    ...mapActions({
      sendUpdateUser: 'auth/updateCurrentUser'
    }),
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
      this.resetEditUser();
    },
    resetEditUser() {
      this.editUser = Object.assign({
        password: ''
      }, this.user);
    },
    onClickSafe() {
      this.sending = true;
      let updateUser = Object.assign({}, this.editUser);
      let updatePassword = !!this.editUser.password || this.editUser.password !== '';
      if (!updatePassword) {
        updateUser.password = this.getRandomString(22);
      }
      this.sendUpdateUser({
        updatePassword,
        userDto: updateUser
      }).then(() => {
        this.stopEditMode();
        this.error = '';
        this.$q.notify({
          type: 'positive',
          message: 'Profile updated'
        });
      }).catch(error => {
        this.error = error.response.data.message;
        this.$q.notify({
          type: 'negative',
          message: 'Couldn\'t update profile. ' + error.response.data.message
        });
      }).finally(() => {
        this.sending = false;
      })
    }
  }
}
</script>

<style scoped>

</style>
