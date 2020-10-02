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
        @click="openDeleteDialog(true)"
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
        @click="onRefreshButton"
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
            {{ props.row.currentIngredient? props.row.currentIngredient.name : "Empty" }}
          </q-td>
          <q-td
            key="actions"
            class="q-pa-md q-gutter-x-sm"
            :props="props"
          >
            <q-btn
              :icon="mdiPlay"
              color="green"
              @click="onClickCleanPump(props.row)"
              dense
              rounded
              :loading="isCleaning(props.row.id)"
            >
              <q-tooltip>
                pump up
              </q-tooltip>
            </q-btn>
            <q-btn
              :icon="mdiPencilOutline"
              color="info"
              @click="showEditDialog(props.row)"
              dense
              rounded
            >
              <q-tooltip>
                Edit
              </q-tooltip>
            </q-btn>
            <q-btn
              :icon="mdiDelete"
              color="red"
              @click="() => {deletePumps.push(props.row); openDeleteDialog(false);}"
              dense
              rounded
            >
              <q-tooltip>
                Delete
              </q-tooltip>
            </q-btn>
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
          <q-banner v-if="editOptions.editErrorMessage !== ''" rounded dense class="text-white bg-red-5"
                    style="margin: 10px">
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
                  @click="onClickSavePump"
                />
              </div>
            </template>
          </pump-editor-form>
        </q-card-section>
      </q-card>
    </q-dialog>
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
          v-if="deletePumps.length === 0"
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
            v-for="(pump, index) in deletePumps"
          >
            Pump #{{ pump.id }}
          </li>
        </ul>
      </template>
    </c-question>
  </q-page>
</template>

<script>

  import {mdiDelete, mdiPencilOutline, mdiPlay} from "@quasar/extras/mdi-v5";
  import {mapGetters} from "vuex";
  import PumpEditorForm from "../components/PumpEditorForm";
  import PumpService from "../services/pump.service"
  import CQuestion from "../components/CQuestion";

  export default {
    name: "PumpManagement",
    components: {PumpEditorForm, CQuestion},
    data() {
      return {
        deleteDialog: false,
        deletePumps: [],
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
        pumps: [],
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
      this.initialize();
    },
    methods: {
      onRefreshButton() {
        this.isLoading = true;
        let vm = this;
        setTimeout(() => {
          vm.initialize()
        }, 500);
      },
      initialize() {
        this.isLoading = true;
        PumpService.getAllPumps()
          .then(pumps => {
            this.pumps = pumps;
            this.isLoading = false;
          })
      },
      onClickCleanPump(pump) {
        PumpService.cleanPump(pump)
          .catch(error => {
            this.$q.notify({
              type: 'negative',
              message: error.response.data.message
            });
          })
      },
      openDeleteDialog(forSelectedPumps) {
        if (forSelectedPumps) {
          this.deletePumps.push(...this.selected);
        }
        this.deleteDialog = true;
      },
      closeDeleteDialog() {
        this.deletePumps.splice(0, this.deletePumps.length);
        this.deleteDialog = false;
      },
      deleteSelected() {
        this.deleteLoading = true;
        let toDelete = this.deletePumps.length;
        let deleted = 0;
        let vm = this;
        let afterDelete = function () {
          if (deleted === toDelete) {
            vm.$q.notify({
              type: 'positive',
              message: ((vm.deletePumps.length > 1)? "Pumps" : "Pump") + " deleted successfully"
            });
            vm.closeDeleteDialog();
            vm.deleteLoading = false;
            vm.initialize();
          }
        };
        this.deletePumps.forEach(pump => {
          PumpService.deletePump(pump)
            .then(() => {
              deleted++;
              afterDelete();
            }, err => {
              vm.deleteLoading = false;
              vm.initialize();
            })
        });
        afterDelete();
      },
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
      },
      onClickSavePump() {
        this.editOptions.editPumpSaving = true;
        let vm = this;
        let onSuccess = function () {
          vm.editOptions.editPumpSaving = false;
          vm.editOptions.editErrorMessage = "";
          vm.$q.notify({
            type: 'positive',
            message: "Pump " + (vm.isEditIngredientNew ? "created" : "updated") + " successfully"
          });
          vm.closeEditDialog();
          vm.initialize();
        };

        let onError = function (error) {
          vm.editOptions.editPumpSaving = false;
          vm.editOptions.editErrorMessage = error.response.data.message;
          vm.$q.notify({
            type: 'negative',
            message: error.response.data.message
          });
        };

        if (this.isEditPumpNew) {
          PumpService.createPump(this.editOptions.editPump)
            .then(onSuccess, error => onError(error));
        } else {
          PumpService.updatePump(this.editOptions.editPump)
            .then(onSuccess, error => onError(error));
        }
      }
    },
    computed: {
      ...mapGetters({
        isCleaning: 'pumpLayout/isCleaning'
      }),
      isEditPumpNew() {
        return this.editOptions.editPump.id === -1;
      },
      deleteQuestionMessage() {
        if (this.deletePumps.length === 0) {
          return "No pumps selected!";
        }
        if (this.deletePumps.length === 1) {
          return "The following pump will be deleted:";
        }
        return "The following pumps will be deleted:";
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
