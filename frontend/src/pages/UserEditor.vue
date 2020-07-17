<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="User Management" :to="{name: 'usermanagement'}"/>
      <q-breadcrumbs-el label="Create user"/>
    </q-breadcrumbs>
    <h5>Edit user</h5>
    <q-card
      flat
    >
      <user-editor-form
        v-model="user"
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

  export default {
    name: "UserEditor",
    components: {UserEditorForm},
    data() {
      return {
        user: {},
        userId: this.$route.params.userId,
        loading: false,
        isValid: false
      }
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
          this.$q.notify({
            type: 'negative',
            message: 'Couldn\' create user. Please try again later!'
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
      this.loading = true;
      userService.getUser(this.userId)
        .then(user => {
          user.password = '';
          this.user = user;
          this.loading = false;
        })
    }
  }
</script>

<style scoped>

</style>
