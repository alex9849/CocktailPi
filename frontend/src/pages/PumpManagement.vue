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
        @click="showEditDialog()"
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
      :pagination="{rowsPerPage: 0, sortBy: 'id'}"
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
            key="id"
            :props="props"
          >
            {{ props.row.id }}
          </q-td>
          <q-td
            key="timePerClInMs"
            :props="props"
          >
            {{ props.row.timePerClInMs }} ms
          </q-td>
          <q-td
            key="tubeCapacityInMl"
            :props="props"
          >
            {{ props.row.tubeCapacityInMl }} ml
          </q-td>
          <q-td
            key="gpioPin"
            :props="props"
          >
            {{ props.row.gpioPin }}
          </q-td>
          <q-td
            key="currentIngredient"
            :props="props"
          >
            {{ props.row.currentIngredient? props.row.currentIngredient.name : "Leer" }}
          </q-td>
          <q-td
            key="actions"
            class="q-pa-md q-gutter-x-sm"
            :props="props"
          >
            <q-btn
              :icon="mdiPlay"
              color="green"
              @click=""
              dense
              rounded
            />
            <q-btn
              :icon="mdiPencilOutline"
              color="info"
              @click="showEditDialog(props.row)"
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
    <q-dialog
      v-model="editOptions.editDialog"
      :persistent="editOptions.editIngredientSaving"
      @hide="closeEditDialog"
    >
      <q-card style="width: 500px">
        <q-card-section class="text-center">
          <h5 style="margin-bottom: 10px">{{ editDialogHeadline }}</h5>
          <q-splitter
            horizontal
            :value="10"
          />
          <q-banner v-if="editOptions.editErrorMessage !== ''" rounded dense class="text-white bg-red-5" style="margin: 10px">
            {{ editOptions.editErrorMessage }}
          </q-banner>
          <pump-editor-form
            class="innerpadding"
            v-model="editOptions.editPump"
            :persistent="editOptions.editPumpSaving"
            @hide="closeEditDialog"
            @valid="editOptions.valid = true"
            @invalid="editOptions.valid = false"
          >
            <template v-slot:below>
              <div class="q-pa-md q-gutter-sm">
                <q-btn
                  style="width: 100px"
                  color="negative"
                  label="Abort"
                  no-caps
                  @click="closeEditDialog"
                />
                <q-btn
                  type="submit"
                  style="width: 100px"
                  color="positive"
                  label="Save"
                  no-caps
                  :disable="editOptions.editPumpSaving || !editOptions.valid"
                  :loading="editOptions.editPumpSaving"
                  @click=""
                />
              </div>
            </template>
          </pump-editor-form>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script>

  import {mdiDelete, mdiPencilOutline, mdiPlay} from "@quasar/extras/mdi-v5";
  import PumpEditorForm from "../components/PumpEditorForm";

  export default {
    name: "PumpManagement",
    components: {PumpEditorForm},
    data() {
      return {
        deleteDialog: false,
        deleteUser: [],
        deleteLoading: false,
        isLoading: false,
        editOptions: {
          editErrorMessage: "",
          editPumpSaving: false,
          editDialog: false,
          valid: false,
          editPump: {
            id: -1,
            timePerClInMs: '',
            tubeCapacityInMl: '',
            gpioPin: '',
            currentIngredient: null
          },
          newPump: {
            id: -1,
            timePerClInMs: '',
            tubeCapacityInMl: '',
            gpioPin: '',
            currentIngredient: null
          }
        },
        pumps: [{
          id: 1,
          timePerClInMs: 1000,
          tubeCapacityInMl: 50,
          gpioPin: 12,
          currentIngredient: {
            id: 1,
            name: 'Jack-Daniels',
            alcoholContent: 40
          }
        }],
        selected: [],
        columns: [
          {name: 'id', label: 'Nr', field: 'id', align: 'left', sortable: true},
          {name: 'timePerClInMs', label: 'Time per Cl', field: 'timePerClInMs', align: 'center'},
          {
            name: 'tubeCapacityInMl',
            label: 'Tube capacity',
            field: 'tubeCapacityInMl',
            align: 'center'
          },
          {name: 'gpioPin', label: 'GPIO-Pin', field: 'gpioPin', align: 'center'},
          {name: 'currentIngredient', label: 'Current Ingredient', field: 'currentIngredient', align: 'center'},
          {name: 'actions', label: 'Actions', field: '', align: 'center'}
        ]
      }
    },
    created() {
      this.mdiDelete = mdiDelete;
      this.mdiPencilOutline = mdiPencilOutline;
      this.mdiPlay = mdiPlay;
    },
    methods: {
      closeEditDialog() {
        this.editOptions.editPump = Object.assign({}, this.editOptions.newPump);
        this.editOptions.editDialog = false;
        this.editOptions.editErrorMessage = "";
      },
      showEditDialog(pump) {
        if (pump) {
          this.editOptions.editPump = Object.assign({}, pump);
        }
        this.editOptions.editDialog = true;
      }
    },
    computed: {
      isEditPumpNew() {
        return this.editOptions.editPump.id === -1;
      },
      editDialogHeadline() {
        if (this.isEditPumpNew) {
          return "Create pump"
        }
        return "Edit pump"
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
