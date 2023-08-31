<template>
  <q-page padding>
    <h5>Edit user</h5>
    <div class="page-content">
      <q-banner v-if="form.error !== ''" rounded dense class="text-white bg-red-5" style="margin: 3px">
        {{ form.error }}
      </q-banner>
      <q-card
        flat
        bordered
        class="q-pa-md bg-card-primary"
      >
        <q-form
          class="q-col-gutter-md"
          @submit.prevent="onClickSave"
        >
          <div class="col-12">
            <q-card
              flat
              bordered
              class="bg-white"
            >
              <q-card-section
                class="q-gutter-y-md"
              >
                <q-input
                  outlined
                  v-model:model-value="v.editUser.username.$model"
                  :disable="form.loading || form.disable"
                  hide-bottom-space
                  label="Username"
                />
                <q-input
                  outlined
                  v-model:model-value="v.editUser.password.$model"
                  :disable="form.loading || form.disable"
                  hide-bottom-space
                  label="Password"
                  :type="form.showPassword? 'text':'password'"
                >
                  <template v-slot:append>
                    <q-icon :name="form.showPassword? mdiEyeOff:mdiEye"
                            @click="form.showPassword = !form.showPassword"/>
                  </template>
                </q-input>
                <q-select
                  outlined
                  v-model:model-value="v.editUser.adminLevel.$model"
                  map-options
                  emit-value
                  hide-bottom-space
                  v-if="!isSelfUser"
                  :options="form.roles"
                  :disable="form.loading || form.disable"
                  label="Role"
                />
                <q-checkbox
                  v-if="!isSelfUser"
                  :model-value="!v.editUser.accountNonLocked.$model"
                  @update:model-value="e => v.editUser.accountNonLocked.$model = !e"
                  :disable="form.loading || form.disable"
                  label="Locked"
                />
              </q-card-section>
            </q-card>
          </div>
          <div class="col-12">
            <q-card-actions
              v-if="isProfile && !form.profileEdit"
              class="q-pa-none"
            >
              <q-btn
                style="width: 100px"
                color="grey"
                no-caps
                @click="form.profileEdit = true; form.disable = false"
                label="Edit"
              />
            </q-card-actions>
            <q-card-actions
              v-else
              class="q-pa-none"
            >
              <q-btn
                type="submit"
                style="width: 100px"
                color="positive"
                label="Save"
                no-caps
                :disable="v.editUser.$invalid"
                :loading="form.loading"
              />
              <q-btn
                style="width: 100px"
                color="negative"
                label="Abort"
                no-caps
                :disable="form.loading"
                @click="onClickAbort"
              />
            </q-card-actions>
          </div>
        </q-form>
      </q-card>
    </div>
  </q-page>
</template>

<script>
import UserService from '../services/user.service'
import store from '../store'
import { mapActions, mapGetters } from 'vuex'
import useVuelidate from '@vuelidate/core'
import { mdiEye, mdiEyeOff } from '@quasar/extras/mdi-v5'
import { maxLength, minLength, required } from '@vuelidate/validators'

export default {
  name: 'UserEditor',
  data () {
    return {
      user: {
        username: '',
        password: '',
        adminLevel: '',
        accountNonLocked: true
      },
      editUser: {
        id: -1,
        username: '',
        password: '',
        adminLevel: '',
        accountNonLocked: true
      },
      form: {
        disable: false,
        profileEdit: false,
        showPassword: false,
        loading: false,
        error: '',
        roles: [
          {
            value: 0,
            label: 'User'
          }, {
            value: 1,
            label: 'Recipe-Creator'
          }, {
            value: 2,
            label: 'Pump-Ingredient-Editor'
          }, {
            value: 3,
            label: 'Admin'
          }
        ]
      }
    }
  },
  async beforeRouteEnter (to, from, next) {
    if (to.name === 'usercreator') {
      next()
      return
    }
    let userId
    let disableForm = false
    if (to.name === 'myprofile') {
      disableForm = true
      userId = store().getters['auth/getUser'].id
    } else {
      userId = to.params.userId
    }
    const user = await UserService.getUser(userId)
    next(vm => {
      vm.user = user
      vm.user.password = ''
      vm.editUser = user
      vm.editUser.password = ''
      vm.form.disable = disableForm
    })
  },
  setup () {
    return {
      v: useVuelidate(),
      mdiEye: mdiEye,
      mdiEyeOff: mdiEyeOff
    }
  },
  computed: {
    ...mapGetters({
      getUser: 'auth/getUser'
    }),
    isNewUser () {
      return this.$route.name === 'usercreator'
    },
    isSelfUser () {
      return this.user.id === this.getUser.id
    },
    isProfile () {
      return this.$route.name === 'myprofile'
    },
    headline () {
      if (this.isNewUser) {
        return 'Create user'
      }
      if (this.isProfile) {
        return 'My profile'
      }
      return 'Edit user'
    }
  },
  methods: {
    ...mapActions({
      updateStoreUser: 'auth/fetchCurrentUser'
    }),
    onClickAbort () {
      if (this.isProfile) {
        this.form.profileEdit = false
        this.form.disable = true
        this.editUser = Object.assign({}, this.user)
      } else {
        this.$router.push({ name: 'usermanagement' })
      }
    },
    onClickSave () {
      if (this.v.editUser.$invalid) {
        return
      }
      this.loading = true
      const updateUser = Object.assign({}, this.editUser)
      const updatePassword = !!this.editUser.password || this.editUser.password !== ''
      if (!updatePassword) {
        updateUser.password = this.getRandomString(22)
      }

      let promise
      if (this.isNewUser) {
        promise = UserService.createUser(updateUser)
      } else {
        const updateRequest = {
          updatePassword,
          userDto: updateUser
        }
        promise = UserService.updateUser(updateRequest, this.editUser.id)
      }
      if (this.isProfile) {
        promise = promise.then(() => {
          this.form.profileEdit = false
          this.form.error = ''
          this.updateStoreUser()
          this.$q.notify({
            type: 'positive',
            message: 'Profile updated'
          })
        })
      } else {
        promise = promise.then(() => {
          this.$q.notify({
            type: 'positive',
            message: this.isNewUser ? 'User created successfully' : 'User updated successfully'
          })
          this.$router.push({ name: 'usermanagement' })
        })
      }

      promise.catch(error => {
        this.error = error.response.data.message
      })
        .finally(() => {
          this.loading = false
          if (this.isProfile) {
            this.form.disable = true
          }
        })
    },
    getRandomString (length) {
      const randomChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
      let result = ''
      for (let i = 0; i < length; i++) {
        result += randomChars.charAt(Math.floor(Math.random() * randomChars.length))
      }
      return result
    }
  },
  validations () {
    const validations = {
      editUser: {
        username: {
          required,
          minLength: minLength(3),
          maxLength: maxLength(20)
        },
        password: {
          minLength: minLength(6),
          maxLength: maxLength(40)
        },
        adminLevel: {
          required
        },
        accountNonLocked: {}
      }
    }
    if (this.passwordRequired) {
      validations.editUser.password.required = required
    }
    return validations
  }
}
</script>

<style scoped>

</style>
