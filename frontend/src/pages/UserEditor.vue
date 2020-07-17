<template>
  <q-page padding>
    <h5>User Management / Edit user</h5>
    <user-editor-form
      v-model="user"
      :loading="loading"
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
            :disable="loading"
            @click="sendUpdateUser()"
          />
        </div>
      </template>
    </user-editor-form>
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
        loading: false
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
          this.$router.push({name: 'usermanagement'})
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
