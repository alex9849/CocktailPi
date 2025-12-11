<template>
  <q-page padding>
    <div class="page-content">
      <h5>{{ headline }}</h5>
      <q-banner v-if="form.error !== ''" rounded dense class="text-white bg-red-5" style="margin: 3px">
        {{ form.error }}
      </q-banner>
      <q-card
        flat
        bordered
        class="q-pa-md bg-card-body text-card-body"
      >
        <q-form
          class="q-col-gutter-md"
          @submit.prevent="onClickSave"
        >
          <div class="col-12">
            <q-card
              flat
              bordered
              class="bg-card-item-group text-card-item-group"
            >
              <q-card-section
                class="q-gutter-y-md"
              >
                <q-input
                  outlined
                  :dark="color.cardItemGroupDark"
                  v-model:model-value="v.editUser.username.$model"
                  :disable="form.loading || form.disable"
                  hide-bottom-space
                  :label="$t('page.user_editor.form.columns.username')"
                />
                <q-input
                  outlined
                  :dark="color.cardItemGroupDark"
                  v-model:model-value="v.editUser.password.$model"
                  :disable="form.loading || form.disable"
                  hide-bottom-space
                  :label="$t('page.user_editor.form.columns.password')"
                  :type="form.showPassword? 'text':'password'"
                >
                  <template v-slot:append>
                    <q-icon :name="form.showPassword? mdiEyeOff:mdiEye"
                            @click="form.showPassword = !form.showPassword"/>
                  </template>
                </q-input>
                <q-select
                  outlined
                  :dark="color.cardItemGroupDark"
                  v-model:model-value="v.editUser.adminLevel.$model"
                  map-options
                  emit-value
                  hide-bottom-space
                  v-if="!isSelfUser"
                  :options="form.roles"
                  :disable="form.loading || form.disable"
                  :label="$t('page.user_editor.form.columns.role')"
                />
                <q-checkbox
                  :dark="color.cardItemGroupDark"
                  v-if="!isSelfUser"
                  :model-value="!v.editUser.accountNonLocked.$model"
                  @update:model-value="e => v.editUser.accountNonLocked.$model = !e"
                  :disable="form.loading || form.disable"
                  :label="$t('page.user_editor.form.columns.locked')"
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
                :label="$t('page.user_editor.form.edit_btn_label')"
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
                :label="$t('page.user_editor.form.save_btn_label')"
                no-caps
                :disable="v.editUser.$invalid"
                :loading="form.loading"
              />
              <q-btn
                style="width: 100px"
                color="negative"
                :label="$t('page.user_editor.form.abort_btn_label')"
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
            label: this.$t('page.user_editor.form.roles.user')
          }, {
            value: 1,
            label: this.$t('page.user_editor.form.roles.recipe_creator')
          }, {
            value: 2,
            label: this.$t('page.user_editor.form.roles.pump_ingredient_editor')
          }, {
            value: 3,
            label: this.$t('page.user_editor.form.roles.admin')
          }, {
            value: 4,
            label: this.$t('page.user_editor.form.roles.super_admin')
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
    let user
    let disableForm = false
    try {
      if (to.name === 'myprofile') {
        disableForm = true
        user = await UserService.getMe()
      } else {
        user = await UserService.getUser(to.params.userId)
      }
    } catch (e) {
      if (e.response.status === 404) {
        next({ name: '404Page' })
        return
      }
    }
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
      mdiEye,
      mdiEyeOff
    }
  },
  computed: {
    ...mapGetters({
      getUser: 'auth/getUser',
      color: 'appearance/getNormalColors'
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
        return this.$t('page.user_editor.headline_create')
      }
      if (this.isProfile) {
        return this.$t('page.user_editor.headline_profile')
      }
      return this.$t('page.user_editor.headline_edit')
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
        if (this.$route.name === 'myprofile') {
          promise = UserService.updateMe(updateRequest)
        } else {
          promise = UserService.updateUser(updateRequest, this.editUser.id)
        }
      }
      if (this.isProfile) {
        promise = promise.then(() => {
          this.form.profileEdit = false
          this.form.error = ''
          this.updateStoreUser()
          this.$q.notify({
            type: 'positive',
            message: this.$t('page.user_editor.notifications.profile_updated')
          })
        })
      } else {
        promise = promise.then(() => {
          let msg
          if (this.isNewUser) {
            msg = this.$t('page.user_editor.notifications.user_created')
          } else {
            msg = this.$t('page.user_editor.notifications.user_updated')
          }
          this.$q.notify({
            type: 'positive',
            message: msg
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
