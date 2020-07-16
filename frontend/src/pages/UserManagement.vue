<template>
  <q-page padding>
    <h5>User Management</h5>
    <div class="q-pa-md q-gutter-sm" style="display: flex; flex-direction: row-reverse;">
      <q-btn
        class="mr-4 mt-4"
        color="negative"
        label="Delete selected users"
        no-caps
      />
      <q-btn
        color="positive"
        label="Create user"
        no-caps
      />
    </div>
    <q-table
      :data="data"
      :columns="colums"
      hide-bottom

      selection="multiple"
      :selected.sync="selected"
      :pagination="{rowsPerPage: 0}"
      :table-style="{margin: '15px'}"
      style="background-color: #f3f3fa"
    >
      <template v-slot:body="props">
        <q-tr :props="props" :class="(props.rowIndex % 2 === 0)? 'row1':'row2'">
          <q-td auto-width style="text-align: center" >
            <q-checkbox v-model="props.selected"/>
          </q-td>
          <q-td key="username" :props="props">
            {{ props.row.username}}
          </q-td>
          <q-td key="isLocked" :props="props">
            <q-icon
              v-if="!props.row.isLocked"
              size="sm"
              :name="mdiCheckCircle"
            />
            <q-icon
              v-else
              size="sm"
              :name="mdiCheckboxBlankCircleOutline"
            />
          </q-td>
          <q-td key="fullname" :props="props">
            {{ props.row.firstname }} {{ props.row.lastname }}
          </q-td>
          <q-td key="email" :props="props">
            {{ props.row.email}}
          </q-td>
          <q-td key="isadmin" :props="props">
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
          <q-td key="actions" :props="props">
            <q-btn label="test"/>
          </q-td>
        </q-tr>
      </template>
      <template v-slot:bottom-row>
        <td style="color: #b5b5b5">
          {{ data.length }} Nutzer gesamt
        </td>
        <td/>
        <td/>
        <td/>
        <td/>
        <td/>
      </template>
    </q-table>
  </q-page>
</template>

<script>
  import {mdiCheckboxBlankCircleOutline, mdiCheckCircle} from '@mdi/js';

  export default {
    name: "UserManagement",
    created() {
      this.mdiCheckCircle = mdiCheckCircle;
      this.mdiCheckboxBlankCircleOutline = mdiCheckboxBlankCircleOutline;
    },
    data() {
      return {
        selected: [],
        data: [
          {
            "id": 1,
            "username": "alex9849",
            "email": "alexander@liggesmeyer.net",
            "firstname": "Alexander",
            "lastname": "Liggesmeyer",
            "isLocked": false,
            "role": ["admin", "user"]
          }, {
            "id": 2,
            "username": "test",
            "email": "test",
            "firstname": "test",
            "lastname": "test",
            "isLocked": true,
            "role": ["user"]
          }
        ],
        colums: [
          {name: 'username', label: 'Username', field: 'username', align: 'left'},
          {name: 'isLocked', label: 'Active', field: 'isLocked', align: 'center'},
          {name: 'fullname', label: 'Full name', field: '', align: 'left'},
          {name: 'email', label: 'E-Mail', field: 'email', align: 'left'},
          {name: 'isadmin', label: 'Admin', field: '', align: 'center'},
          { name: 'actions', label: 'Actions', field: '', align:'center'}
        ]
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
