<template>
  <q-page class="page-content" padding>
    <h5>{{ $t('page.user_mgmt.headline') }}</h5>
    <TopButtonArranger>
      <q-btn
        color="positive"
        :label="$t('page.user_mgmt.create_user_btn_label')"
        no-caps
        :disable="isLoading"
        :to="{name: 'usercreator'}"
      />
      <q-btn
        color="info"
        :label="$t('page.user_mgmt.refresh_users_btn_label')"
        :disable="isLoading"
        :loading="isLoading"
        @click="onRefreshButton"
        no-caps
      />
    </TopButtonArranger>
    <div
      class="q-py-md"
    >
      <q-table
        :dark="color.cardBodyDark"
        :rows="data"
        :columns="colums"
        hide-bottom
        ref="userTable"
        :loading="isLoading"
        :pagination="{rowsPerPage: 0, sortBy: 'id'}"
      >
        <template v-slot:body-cell-nonLocked="props">
          <q-td :props="props"
                key="nonLocked"
          >
            <q-icon
              v-if="props.row.accountNonLocked"
              size="sm"
              :name="mdiCheckCircle"
            />
            <q-icon
              v-else
              size="sm"
              :name="mdiCheckboxBlankCircleOutline"
            />
          </q-td>
        </template>
        <template v-slot:body-cell-role="props">
          <q-td
            key="role"
            :props="props"
          >
            {{ roles.find(x => x.value === props.row.adminLevel).label }}
          </q-td>
        </template>
        <template v-slot:body-cell-actions="props">
          <q-td
            class="q-pa-md q-gutter-x-sm"
            key="actions"
            :props="props"
          >
            <q-btn
              v-if="props.row.adminLevel <= getUser.adminLevel"
              :icon="mdiPencilOutline"
              text-color="white"
              :style="{backgroundColor: '#31ccec'}"
              @click="$router.push({name: 'usereditor', params: {
                userId: props.row.id
              }})"
              dense
              rounded
            >
              <q-tooltip>
                {{ $t('page.user_mgmt.edit_user_btn_tooltip') }}
              </q-tooltip>
            </q-btn>
            <q-btn
              :icon="mdiDelete"
              v-if="getUser.id !== props.row.id && props.row.adminLevel <= getUser.adminLevel"
              @click="() => $refs.deleteDialog.openForItems([props.row])"
              color="red"
              dense
              rounded
            >
              <q-tooltip>
                {{ $t('page.user_mgmt.delete_user_btn_tooltip') }}
              </q-tooltip>
            </q-btn>
          </q-td>
        </template>
        <template
          v-slot:bottom-row
        >
          <td
            style="color: #b5b5b5"
          >
            {{ $t('page.user_mgmt.user_table.nr_users', {nr: data.length}) }}
          </td>
          <td rowspan="5"/>
        </template>
        <template
          v-slot:loading
        >
          <q-inner-loading
            :dark="color.cardBodyDark"
            showing
            color="info"
          />
        </template>
      </q-table>
    </div>
    <c-delete-warning
      ref="deleteDialog"
      :delete-method="deleteUser"
      :list-point-method="x => x.username"
      :headline="$t('page.user_mgmt.delete_dialog.headline')"
      @deleteFailure="fetchAll"
      @deleteSuccess="onDeleteSuccess"
    />
  </q-page>
</template>

<script>
import { mdiCheckboxBlankCircleOutline, mdiCheckCircle, mdiDelete, mdiPencilOutline } from '@quasar/extras/mdi-v5'
import { mapGetters } from 'vuex'
import UserService from '../services/user.service'
import TopButtonArranger from 'components/TopButtonArranger'
import CDeleteWarning from 'components/CDeleteWarning'

export default {
  name: 'UserManagement',
  components: { CDeleteWarning, TopButtonArranger },
  data () {
    return {
      isLoading: false,
      data: [],
      colums: [
        { name: 'username', label: this.$t('page.user_mgmt.user_table.columns.username'), field: 'username', align: 'center' },
        { name: 'nonLocked', label: this.$t('page.user_mgmt.user_table.columns.active'), field: 'nonLocked', align: 'center' },
        { name: 'role', label: this.$t('page.user_mgmt.user_table.columns.role'), field: 'role', align: 'center' },
        { name: 'actions', label: this.$t('page.user_mgmt.user_table.columns.actions'), field: '', align: 'center' }
      ],
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
        }, {
          value: 4,
          label: 'Super-Admin'
        }
      ]
    }
  },
  created () {
    this.mdiCheckCircle = mdiCheckCircle
    this.mdiCheckboxBlankCircleOutline = mdiCheckboxBlankCircleOutline
    this.mdiDelete = mdiDelete
    this.mdiPencilOutline = mdiPencilOutline
    this.fetchAll()
  },
  computed: {
    ...mapGetters({
      getUser: 'auth/getUser',
      color: 'appearance/getNormalColors'
    })
  },
  methods: {
    fetchAll () {
      this.isLoading = true
      UserService.getAllUsers()
        .then(users => {
          this.data = users
          this.isLoading = false
        }, () => {
          this.loading = false
        })
    },
    deleteUser (id) {
      return UserService.deleteUser(id)
    },
    onDeleteSuccess () {
      this.fetchAll()
    },
    onRefreshButton () {
      this.isLoading = true
      const vm = this
      setTimeout(() => {
        vm.fetchAll()
      }, 500)
    }
  }
}
</script>

<style scoped>
</style>
