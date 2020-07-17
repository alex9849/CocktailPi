<template>
  <q-page padding>
    <user-editor-form
      v-model="user"
      :loading="loading"
    >
      <template v-slot:below>

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
        loading: false
      }
    },
    created() {
      this.loading = true;
      userService.getMe()
        .then(user => {
          this.user = user;
          this.loading = false;
        })
    }
  }
</script>

<style scoped>

</style>
