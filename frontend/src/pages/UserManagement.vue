<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="User Management"/>
    </q-breadcrumbs>
    <h5>User Management</h5>
    <div class="q-pa-md q-gutter-sm" style="display: flex; flex-direction: row-reverse;">
      <q-btn
        color="negative"
        label="Delete selected users"
        no-caps
        :disable="isLoading"
        @click="openDeleteDialog(true)"
      />
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
    </div>
    <q-table
      :data="data"
      :columns="colums"
      hide-bottom
      :loading="isLoading"
      selection="multiple"
      :selected.sync="selected"
      :pagination="{rowsPerPage: 0}"
      :table-style="{margin: '15px'}"
      style="background-color: #f3f3fa"
    >
      <template
        v-slot:body="props"
      >
        <q-tr
          :props="props"
          :class="(props.rowIndex % 2 === 1)? 'row1':'row2'"
        >
          <q-td
            auto-width
            style="text-align: center"
          >
            <q-checkbox
              v-model="props.selected"
            />
          </q-td>
          <q-td
            key="username"
            :props="props"
          >
            {{ props.row.username}}
          </q-td>
          <q-td
            key="isLocked"
            :props="props"
          >
            <q-icon
              v-if="!props.row.locked"
              size="sm"
              :name="mdiCheckCircle"
            />
            <q-icon
              v-else
              size="sm"
              :name="mdiCheckboxBlankCircleOutline"
            />
          </q-td>
          <q-td
            key="fullname"
            :props="props"
          >
            {{ props.row.firstname }} {{ props.row.lastname }}
          </q-td>
          <q-td
            key="email"
            :props="props"
          >
            {{ props.row.email}}
          </q-td>
          <q-td
            key="isadmin"
            :props="props"
          >
            <q-icon
              v-if="props.row.role.includes('admin')"
              size="sm"
              :name="mdiCheckCircle"
            />
            <q-icon
              v-else
              size="sm"
              :name="mdiCheckboxBlankCircleOutline"
            />
          </q-td>
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
            />
            <q-btn
              :icon="mdiDelete"
              @click="() => {deleteUsers.push(props.row); openDeleteDialog(false);}"
              color="red"
              dense
              rounded
            />
          </q-td>
        </q-tr>
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
    <c-question
      :question="deleteQuestionMessage"
      ok-color="red"
      ok-button-text="Delete"
      :loading="deleteLoading"
      v-model="deleteDialog"
      @clickOk="deleteSelected"
      @clickAbort="closeDeleteDialog"
    >
      <template v-slot:buttons>
        <q-btn
          v-if="deleteUsers.length === 0"
          color="grey"
          style="width: 150px"
          @click="closeDeleteDialog"
        >
          Ok
        </q-btn>
      </template>
      <template v-slot:default>
        <ul style="padding: 0">
          <li
            :key="index"
            v-for="(user, index) in deleteUsers"
          >
            {{user.username}} ({{ user.email }})
          </li>
        </ul>
      </template>
    </c-question>
  </q-page>
</template>

<script>
  import {mdiCheckboxBlankCircleOutline, mdiCheckCircle, mdiDelete, mdiPencilOutline} from '@quasar/extras/mdi-v5';
  import userService from '../services/user.service'
  import CQuestion from "../components/CQuestion";

  export default {
    name: "UserManagement",
    components: {CQuestion},
    data() {
      return {
        deleteDialog: false,
        deleteUsers: [],
        deleteLoading: false,
        selected: [],
        isLoading: false,
        data: [],
        colums: [
          {name: 'username', label: 'Username', field: 'username', align: 'left'},
          {name: 'isLocked', label: 'Active', field: 'isLocked', align: 'center'},
          {name: 'fullname', label: 'Full name', field: '', align: 'left'},
          {name: 'email', label: 'E-Mail', field: 'email', align: 'left'},
          {name: 'isadmin', label: 'Admin', field: '', align: 'center'},
          { name: 'actions', label: 'Actions', field: '', align:'center'}
        ]
      }
    },
    created() {
      this.mdiCheckCircle = mdiCheckCircle;
      this.mdiCheckboxBlankCircleOutline = mdiCheckboxBlankCircleOutline;
      this.mdiDelete = mdiDelete;
      this.mdiPencilOutline = mdiPencilOutline;
      this.fetchUsers();
    },
    computed: {
      deleteQuestionMessage() {
        if(this.deleteUsers.length === 0) {
          return "No users selected!";
        }
        if(this.deleteUsers.length === 1) {
          return "The following user will be deleted:";
        }
        return "The following users will be deleted:";
      }
    },
    methods: {
      onRefreshButton() {
        this.isLoading = true;
        let vm = this;
        setTimeout(() => {
          vm.fetchUsers()
        }, 500);
      },
      openDeleteDialog(forSelectedUsers) {
        if(forSelectedUsers) {
          this.deleteUsers.push(...this.selected);
        }
        this.deleteDialog = true;
      },
      closeDeleteDialog() {
        this.deleteUsers.splice(0, this.deleteUsers.length);
        this.deleteDialog = false;
      },
      deleteSelected() {
        this.deleteLoading = true;
        let toDelete = this.deleteUsers.length;
        let deleted = 0;
        let vm = this;
        let afterDelete = function() {
          if(deleted === toDelete) {
            vm.closeDeleteDialog();
            vm.deleteLoading = false;
            vm.fetchUsers();
          }
        };
        this.deleteUsers.forEach(user => {
          userService.deleteUser(user)
            .then(() => {
              deleted++;
              afterDelete();
            }, err => {
              vm.deleteLoading = false;
              vm.fetchUsers();
            })
        });
        afterDelete();
      },
      fetchUsers() {
        this.isLoading = true;
        userService.getAllUsers()
          .then(users => {
            this.data = users;
            this.isLoading = false;
          }, err => {
            this.loading = false;
          })
      }
    }
  }
</script>

<style scoped>
  .row1 {
    background-color: #fafafa;
  }
  .row2 {
    background-color: #f3f3fa;
  }

</style>
