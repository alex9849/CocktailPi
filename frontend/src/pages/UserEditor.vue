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
            type="submit"
            style="width: 100px"
            color="negative"
            label="Abort"
            no-caps
          />
          <q-btn
            style="width: 100px"
            color="positive"
            label="Save"
            no-caps
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
