<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="User Management"/>
    </q-breadcrumbs>
    <h5>User Management</h5>
    <TopButtonArranger style="padding: 10px">
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
    </TopButtonArranger>
    <q-table
      :data="data"
      :columns="colums"
      hide-bottom
      ref="userTable"
      :loading="isLoading"
      selection="multiple"
      :selected.sync="selected"
      :pagination="{rowsPerPage: 0, sortBy: 'id'}"
    >
      <template v-slot:body-selection="scope">
        <div class="text-center">
          <q-checkbox
            v-if="getUser.id !== scope.row.id"
            v-model="scope.selected"
          />
        </div>
      </template>
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
      <template v-slot:body-cell-fullname="props">
        <q-td
          key="fullname"
          :props="props"
        >
          {{ props.row.firstname }} {{ props.row.lastname }}
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
            @click="() => {deleteUsers.push(props.row); openDeleteDialog(false);}"
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
      <template v-slot:header-selection="selected">
        <q-checkbox
          :value="isAllSelectedCeckboxState"
          @input="v => v? selectAll() : $refs.userTable.clearSelection()"
        />
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
import {mapGetters} from "vuex";
import userService from '../services/user.service'
import CQuestion from "../components/CQuestion";
import TopButtonArranger from "components/TopButtonArranger";

export default {
    name: "UserManagement",
    components: {TopButtonArranger, CQuestion},
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
          {name: 'nonLocked', label: 'Active', field: 'nonLocked', align: 'center'},
          {name: 'fullname', label: 'Full name', field: '', align: 'left'},
          {name: 'email', label: 'E-Mail', field: 'email', align: 'left'},
          {name: 'role', label: 'Role', field: 'role', align: 'center'},
          {name: 'actions', label: 'Actions', field: '', align: 'center'}
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
    created() {
      this.mdiCheckCircle = mdiCheckCircle;
      this.mdiCheckboxBlankCircleOutline = mdiCheckboxBlankCircleOutline;
      this.mdiDelete = mdiDelete;
      this.mdiPencilOutline = mdiPencilOutline;
      this.initialize();
    },
    computed: {
      ...mapGetters({
        getUser: 'auth/getUser'
      }),
      deleteQuestionMessage() {
        if (this.deleteUsers.length === 0) {
          return "No users selected!";
        }
        if (this.deleteUsers.length === 1) {
          return "The following user will be deleted:";
        }
        return "The following users will be deleted:";
      },
      isAllSelectedCeckboxState() {
        if(this.selected.length === 0) {
          return false;
        }
        if(this.selected.length === this.data.length - 1) {
          return true;
        }
        return undefined;
      }
    },
    methods: {
      selectAll() {
        let toSelect = this.data.filter(x => x.id !== this.getUser.id);
        this.selected.push(...toSelect);
      },
      onRefreshButton() {
        this.isLoading = true;
        let vm = this;
        setTimeout(() => {
          vm.initialize()
        }, 500);
      },
      openDeleteDialog(forSelectedUsers) {
        if (forSelectedUsers) {
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
        let afterDelete = function () {
          if (deleted === toDelete) {
            vm.closeDeleteDialog();
            vm.deleteLoading = false;
            vm.initialize();
            vm.$q.notify({
              type: 'positive',
              message: 'User(s) deleted successfully'
            });
          }
        };
        this.deleteUsers.forEach(user => {
          userService.deleteUser(user)
            .then(() => {
              deleted++;
              afterDelete();
            }, err => {
              vm.deleteLoading = false;
              vm.initialize();
              vm.$q.notify({
                type: 'negative',
                message: 'Couldn\'t delete user(s). ' + error.response.data.message
              });
            })
        });
        afterDelete();
      },
      initialize() {
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
