<template>
  <q-page class="page-content" padding>
    <h5>User Management</h5>
    <TopButtonArranger>
      <q-btn
        color="positive"
        label="Create user"
        no-caps
        :disable="isLoading"
        :to="{name: 'usercreator'}"
      />
      <q-btn
        color="info"
        label="Refresh"
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
                Edit
              </q-tooltip>
            </q-btn>
            <q-btn
              :icon="mdiDelete"
              v-if="getUser.id !== props.row.id"
              @click="() => $refs.deleteDialog.openForItems([props.row])"
              color="red"
              dense
              rounded
            >
              <q-tooltip>
                Delete
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
            {{ data.length }} user(s) in total
          </td>
          <td rowspan="5"/>
        </template>
        <template
          v-slot:loading
        >
          <q-inner-loading
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
      item-name-plural="users"
      item-name-singular="user"
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
        { name: 'username', label: 'Username', field: 'username', align: 'center' },
        { name: 'nonLocked', label: 'Active', field: 'nonLocked', align: 'center' },
        { name: 'role', label: 'Role', field: 'role', align: 'center' },
        { name: 'actions', label: 'Actions', field: '', align: 'center' }
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
      getUser: 'auth/getUser'
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
