<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="Ingredient Management"/>
    </q-breadcrumbs>
    <h5>Ingredient Management</h5>
    <div class="q-pa-md q-gutter-sm" style="display: flex; flex-direction: row-reverse;">
      <q-btn
        color="negative"
        label="Delete selected ingredients"
        :disable="loading"
        @click="openDeleteDialog(true)"
        no-caps
      />
      <q-btn
        color="positive"
        label="Create ingredient"
        :disable="loading"
        @click="showEditDialog(null)"
        no-caps
      />
      <q-btn
        color="info"
        label="Refresh"
        :disable="loading"
        :loading="loading"
        @click="onRefresh"
        no-caps
      />
    </div>
    <q-table
      :columns="columns"
      :data="ingredients"
      :loading="loading"
      :selected.sync="selected"
      selection="multiple"
      hide-bottom
      :pagination="{rowsPerPage: 0, sortBy: 'name'}"
      no-data-label="No ingredients found"
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
            key="name"
            :props="props"
          >
            {{ props.row.name }}
          </q-td>
          <q-td
            key="alcoholContent"
            :props="props"
          >
            {{ props.row.alcoholContent }}%
          </q-td>
          <q-td
            key="syrup"
            :props="props"
          >
            {{ props.row.syrup? 'yes' : 'no' }}
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
              @click="() => {deleteOptions.deleteIngredients.push(props.row); openDeleteDialog(false);}"
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
          {{ ingredients.length }} ingredient(s) in total
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
      :loading="deleteOptions.deleteLoading"
      v-model="deleteOptions.deleteDialog"
      @clickOk="deleteSelected"
      @clickAbort="closeDeleteDialog"
    >
      <template v-slot:error-area>
        <div>
          <q-banner v-if="deleteOptions.deleteIngredients.length !== 0" rounded dense class="text-white bg-red-5"
                    style="margin: 10px">
            This also removes {{ (deleteOptions.deleteIngredients.length > 1)? "these ingredients":"this ingredient" }} from all
            associated recipes!
          </q-banner>
          <q-banner v-if="deleteOptions.deleteErrorMessage !== ''" rounded dense class="text-white bg-red-5" style="margin: 10px">
            {{ deleteOptions.deleteErrorMessage }}
          </q-banner>
        </div>
      </template>
      <template v-slot:buttons>
        <q-btn
          v-if="deleteOptions.deleteIngredients.length === 0"
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
            v-for="(ingredient, index) in deleteOptions.deleteIngredients"
          >
            {{ingredient.name}}
          </li>
        </ul>
      </template>
    </c-question>
    <c-edit-dialog
      v-model="editOptions.editDialog"
      :error-message="editOptions.editErrorMessage"
      :title="editDialogHeadline"
      :saving="editOptions.editIngredientSaving"
      :valid="editOptions.valid"
      @clickAbort="closeEditDialog"
      @clickSave="onClickSaveIngredient"
    >
      <q-input
        label="Name"
        outlined
        :disable="editOptions.editIngredientSaving"
        v-model="editOptions.editIngredient.name"
        filled
        @input="$v.editOptions.editIngredient.name.$touch()"
        :rules="[
                val => $v.editOptions.editIngredient.name.required || 'Required',
                val => $v.editOptions.editIngredient.name.maxLength || 'Max 30'
              ]"
      />
      <q-input
        label="Alcohol content"
        outlined
        :disable="editOptions.editIngredientSaving"
        v-model="editOptions.editIngredient.alcoholContent"
        filled
        type="number"
        @input="$v.editOptions.editIngredient.alcoholContent.$touch()"
        :rules="[
                val => $v.editOptions.editIngredient.alcoholContent.required || 'Required',
                val => $v.editOptions.editIngredient.alcoholContent.minValue || 'Must be positive',
                val => $v.editOptions.editIngredient.alcoholContent.maxValue || 'Max 100'
              ]"
      />
      <q-checkbox
        v-model="editOptions.editIngredient.syrup"
        :disable="editOptions.editIngredientSaving"
        label="is syrup?"
      />
    </c-edit-dialog>
  </q-page>
</template>

<script>
import {mdiDelete, mdiPencilOutline} from '@quasar/extras/mdi-v5';
import IngredientService from "../services/ingredient.service";
import CQuestion from "../components/CQuestion";
import {maxLength, maxValue, minValue, required} from "vuelidate/lib/validators";
import CEditDialog from "components/CEditDialog";

export default {
    name: "IngredientManagement",
    components: {CEditDialog, CQuestion},
    data() {
      return {
        columns: [
          {name: 'name', label: 'Ingredient', field: 'name', align: 'center'},
          {name: 'alcoholContent', label: 'Alcohol content', field: 'alcoholContent', align: 'center'},
          {name: 'syrup', label: 'is syrup?', field: 'syrup', align: 'center'},
          {name: 'actions', label: 'Actions', field: '', align: 'center'}
        ],
        ingredients: [],
        selected: [],
        loading: false,
        deleteOptions: {
          deleteIngredients: [],
          deleteErrorMessage: "",
          deleteLoading: false,
          deleteDialog: false
        },
        editOptions: {
          editErrorMessage: "",
          editIngredientSaving: false,
          editDialog: false,
          valid: false,
          editIngredient: {
            id: -1,
            name: "",
            syrup: false,
            alcoholContent: 0
          },
          newIngredient: {
            id: -1,
            name: "",
            syrup: false,
            alcoholContent: 0
          }
        }
      }
    },
    methods: {
      onRefresh() {
        this.loading = true;
        let vm = this;
        setTimeout(vm.fetchAll, 500);
      },
      showEditDialog(ingredient) {
        if (ingredient) {
          this.editOptions.editIngredient = Object.assign({}, ingredient);
        }
        this.editOptions.editDialog = true;
      },
      closeEditDialog() {
        this.editOptions.editIngredient = Object.assign({}, this.editOptions.newIngredient);
        this.editOptions.editDialog = false;
        this.editOptions.editErrorMessage = "";
      },
      onClickSaveIngredient() {
        this.editOptions.editIngredientSaving = true;
        let vm = this;
        let onSuccess = function () {
          vm.editOptions.editIngredientSaving = false;
          vm.editOptions.editErrorMessage = "";
          vm.$q.notify({
            type: 'positive',
            message: "Ingredient " + (vm.isEditIngredientNew ? "created" : "updated") + " successfully"
          });
          vm.closeEditDialog();
          vm.fetchAll();
        };

        let onError = function (error) {
          vm.editOptions.editIngredientSaving = false;
          vm.editOptions.editErrorMessage = error.response.data.message;
          vm.$q.notify({
            type: 'negative',
            message: error.response.data.message
          });
        };

        if (this.isEditIngredientNew) {
          IngredientService.createIngredient(this.editOptions.editIngredient)
            .then(onSuccess, error => onError(error));
        } else {
          IngredientService.updateIngredient(this.editOptions.editIngredient)
            .then(onSuccess, error => onError(error));
        }
      },
      deleteSelected() {
        this.deleteOptions.deleteLoading = true;
        let toDelete = this.deleteOptions.deleteIngredients.length;
        let deleted = 0;
        let vm = this;
        let afterDelete = function () {
          if (deleted === toDelete) {
            vm.closeDeleteDialog();
            vm.deleteOptions.deleteLoading = false;
            vm.deleteOptions.deleteErrorMessage = "";
            vm.fetchAll();
            vm.$q.notify({
              type: 'positive',
              message: (toDelete > 1 ? "Ingredients" : "Ingredient") + " deleted successfully"
            });
          }
        };
        this.deleteOptions.deleteIngredients.forEach(ingredient => {
          IngredientService.deleteIngredient(ingredient)
            .then(() => {
              deleted++;
              afterDelete();
            }, err => {
              vm.deleteOptions.deleteLoading = false;
              vm.fetchAll();
              vm.deleteOptions.deleteErrorMessage = err.response.data.message;
              vm.$q.notify({
                type: 'negative',
                message: err.response.data.message
              });
            })
        });
        afterDelete();
      },
      openDeleteDialog(forSelected) {
        if (forSelected) {
          this.deleteOptions.deleteIngredients.push(...this.selected);
        }
        this.deleteOptions.deleteDialog = true;
      },
      closeDeleteDialog() {
        this.deleteOptions.deleteIngredients.splice(0, this.deleteOptions.deleteIngredients.length);
        this.deleteOptions.deleteDialog = false;
        this.deleteOptions.deleteErrorMessage = "";
      },
      fetchAll() {
        this.loading = true;
        IngredientService.getIngredients()
          .then(ingredients => {
            this.loading = false;
            this.ingredients = ingredients;
          })
      }
    },
    created() {
      this.mdiDelete = mdiDelete;
      this.mdiPencilOutline = mdiPencilOutline;
      this.fetchAll();
    },
    validations() {
      let validations = {
        editOptions: {
          editIngredient: {
            name: {
              required,
              maxLength: maxLength(30)
            },
            alcoholContent: {
              required,
              minValue: minValue(0),
              maxValue: maxValue(100)
            }
          }
        }
      };
      return validations;
    },
    watch: {
      '$v.editOptions.editIngredient.$invalid': function _watch$vEditOptionsEditIngredient$invalid(value) {
        this.editOptions.valid = !value;
      }
    },
    computed: {
      deleteQuestionMessage() {
        if (this.deleteOptions.deleteIngredients.length === 0) {
          return "No ingredients selected!";
        }
        if (this.deleteOptions.deleteIngredients.length === 1) {
          return "The following ingredient will be deleted:";
        }
        return "The following ingredients will be deleted:";
      },
      isEditIngredientNew() {
        return this.editOptions.editIngredient.id === -1;
      },
      editDialogHeadline() {
        if (this.isEditIngredientNew) {
          return "Create ingredient"
        }
        return "Edit ingredient"
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
