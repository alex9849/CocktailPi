<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="User Management" :to="{name: 'usermanagement'}"/>
      <q-breadcrumbs-el label="Edit user"/>
    </q-breadcrumbs>
    <h5>Edit user</h5>
    <q-banner v-if="error !== ''" rounded dense class="text-white bg-red-5" style="margin: 3px">
      {{ error }}
    </q-banner>
    <q-card
      flat
    >
      <user-editor-form
        v-model="user"
        :loading="loading"
        :is-self="userId === getUser.id"
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
              label="Save"
              no-caps
              :disable="loading || !isValid"
              @click="sendUpdateUser()"
            />
          </div>
        </template>
      </user-editor-form>
    </q-card>
  </q-page>
</template>

<script>
import UserEditorForm from "../components/UserEditorForm";
import userService from "../services/user.service"
import {mapGetters} from "vuex";

export default {
    name: "UserEditor",
    components: {UserEditorForm},
    data() {
      return {
        user: {},
        userId: this.$route.params.userId,
        loading: false,
        isValid: false,
        error: ''
      }
    },
    computed: {
      ...mapGetters({
        getUser: 'auth/getUser'
      }),
    },
    methods: {
      sendUpdateUser() {
        this.loading = true;
        let updateUser = Object.assign({}, this.user);
        let updatePassword = !!this.user.password || this.user.password !== '';
        if (!updatePassword) {
          updateUser.password = this.getRandomString(22);
        }
        userService.updateUser({
          updatePassword,
          userDto: updateUser
        }).then(() => {
          this.loading = false;
          this.$q.notify({
            type: 'positive',
            message: 'User updated successfully'
          });
          this.$router.push({name: 'usermanagement'})
        }).catch(error => {
          this.loading = false;
          this.error = error.response.data.message;
          this.$q.notify({
            type: 'negative',
            message: 'Couldn\'t update user. ' + error.response.data.message
          });
        })
      },
      getRandomString(length) {
        var randomChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        var result = '';
        for (var i = 0; i < length; i++) {
          result += randomChars.charAt(Math.floor(Math.random() * randomChars.length));
        }
        return result;
      }
    },
    created() {
      this.user = this.$route.meta.user;
      this.user.password = '';
    }
  }
</script>

<style scoped>

</style>
