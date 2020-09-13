<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="Pumps"/>
    </q-breadcrumbs>
    <h5>Pumps</h5>
    <div class="q-pa-md q-gutter-sm" style="display: flex; flex-direction: row-reverse;">
      <q-btn
        color="negative"
        label="Delete selected pumps"
        no-caps
        :disable="isLoading"
        @click=""
      />
      <q-btn
        color="positive"
        label="Add pump"
        no-caps
        :disable="isLoading"
      />
      <q-btn
        color="info"
        label="Refresh"
        :disable="isLoading"
        :loading="isLoading"
        @click=""
        no-caps
      />
    </div>
    <q-table
      :columns="columns"
      :data="pumps"
      :loading="isLoading"
      :selected.sync="selected"
      selection="multiple"
      hide-bottom
      :pagination="{rowsPerPage: 0, sortBy: 'name'}"
      :table-style="{margin: '15px'}"
      style="background-color: #f3f3fa"
    >
      <template v-slot:body="props">
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
            key="actions"
            class="q-pa-md q-gutter-x-sm"
            :props="props"
          >
            <q-btn
              :icon="mdiPencilOutline"
              text-color="white"
              :style="{backgroundColor: '#31ccec'}"
              @click=""
              dense
              rounded
            />
            <q-btn
              :icon="mdiDelete"
              color="red"
              @click=""
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
          {{ pumps.length }} pump(s) in total
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
  </q-page>
</template>

<script>
    export default {
        name: "Settings",
      data() {
        return {
          deleteDialog: false,
          deleteUser: [],
          deleteLoading: false,
          isLoading: false,
          pumps: [],
          selected: [],
          columns: [
            {name: 'Nr', label: 'Nr', field: 'nr', align: 'left'},
            {name: 'timePerClInMs', label: 'Time per Cl', field: 'timePerClInMs', align: 'center'},
            {name: 'tubeFillingQuantity', label: 'Tube filling quantity', field: 'tubeFillingQuantity', align: 'center'},
            {name: 'gpioPin', label: 'GPIO-Pin', field: 'gpioPin', align: 'center'},
            {name: 'currentIngredient', label: 'Current Ingredient', field: 'currentIngredient', align: 'center'},
            { name: 'actions', label: 'Actions', field: '', align:'center'}
          ]
        }
      }
    }
</script>

<style scoped>

</style>
