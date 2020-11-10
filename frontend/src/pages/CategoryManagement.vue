<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="CategoryManagement"/>
    </q-breadcrumbs>
    <h5>CategoryManagement</h5>
    <div class="q-pa-md q-gutter-sm" style="display: flex; flex-direction: row-reverse;">
      <q-btn
        color="negative"
        label="Delete selected categories"
        :disable="loading"
        @click="openDeleteDialog(true)"
        no-caps
      />
      <q-btn
        color="positive"
        label="Create category"
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
      :data="categories"
      :loading="loading"
      :selected.sync="selected"
      selection="multiple"
      hide-bottom
      :pagination="{rowsPerPage: 0, sortBy: 'name'}"
      no-data-label="No categories found"
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
              @click="() => {deleteOptions.deleteCategory.push(props.row); openDeleteDialog(false);}"
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
          {{ categories.length + ' ' + ((categories.length === 1)? 'category' : 'categories')}} in total
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
          <q-banner v-if="deleteOptions.deleteErrorMessage !== ''" rounded dense class="text-white bg-red-5" style="margin: 10px">
            {{ deleteOptions.deleteErrorMessage }}
          </q-banner>
        </div>
      </template>
      <template v-slot:buttons>
        <q-btn
          v-if="deleteOptions.deleteCategory.length === 0"
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
            v-for="(category, index) in deleteOptions.deleteCategory"
          >
            {{category.name}}
          </li>
        </ul>
      </template>
    </c-question>
    <c-edit-dialog
      v-model="editOptions.editDialog"
      :error-message="editOptions.editErrorMessage"
      :title="editDialogHeadline"
      :saving="editOptions.editCategorySaving"
      :valid="editOptions.valid"
      @clickAbort="closeEditDialog"
      @clickSave="onClickSaveCategory"
    >
      <q-input
        label="Name"
        outlined
        :disable="editOptions.editCategorySaving"
        v-model="editOptions.editCategory.name"
        filled
        @input="$v.editOptions.editCategory.name.$touch()"
        :rules="[
                val => $v.editOptions.editCategory.name.required || 'Required',
                val => $v.editOptions.editCategory.name.maxLength || 'Max 15'
              ]"
      />
    </c-edit-dialog>

  </q-page>
</template>

<script>
import {mdiDelete, mdiPencilOutline} from '@quasar/extras/mdi-v5';
import CategoryService from "../services/category.service";
import CQuestion from "../components/CQuestion";
import {maxLength, required} from "vuelidate/lib/validators";
import CEditDialog from "components/CEditDialog";

export default {
    name: "CategoryManagement",
    components: {CEditDialog, CQuestion},
    data() {
      return {
        columns: [
          {name: 'name', label: 'Category', field: 'name', align: 'center'},
          {name: 'actions', label: 'Actions', field: '', align: 'center'}
        ],
        categories: [],
        selected: [],
        loading: false,
        deleteOptions: {
          deleteCategory: [],
          deleteErrorMessage: "",
          deleteLoading: false,
          deleteDialog: false
        },
        editOptions: {
          editErrorMessage: "",
          editCategorySaving: false,
          editDialog: false,
          valid: false,
          editCategory: {
            id: -1,
            name: "",
            alcoholContent: 0
          },
          newCategory: {
            id: -1,
            name: "",
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
      showEditDialog(category) {
        if (category) {
          this.editOptions.editCategory = Object.assign({}, category);
        }
        this.editOptions.editDialog = true;
      },
      closeEditDialog() {
        this.editOptions.editCategory = Object.assign({}, this.editOptions.newCategory);
        this.editOptions.editDialog = false;
        this.editOptions.editErrorMessage = "";
      },
      onClickSaveCategory() {
        this.editOptions.editCategorySaving = true;
        let vm = this;
        let onSuccess = function () {
          vm.editOptions.editCategorySaving = false;
          vm.editOptions.editErrorMessage = "";
          vm.$q.notify({
            type: 'positive',
            message: "Category " + (vm.iseditCategoryNew ? "created" : "updated") + " successfully"
          });
          vm.closeEditDialog();
          vm.fetchAll();
        };

        let onError = function (error) {
          vm.editOptions.editCategorySaving = false;
          vm.editOptions.editErrorMessage = error.response.data.message;
          vm.$q.notify({
            type: 'negative',
            message: error.response.data.message
          });
        };

        if (this.iseditCategoryNew) {
          CategoryService.createCategory(this.editOptions.editCategory)
            .then(onSuccess, error => onError(error));
        } else {
          CategoryService.updateCategory(this.editOptions.editCategory)
            .then(onSuccess, error => onError(error));
        }
      },
      deleteSelected() {
        this.deleteOptions.deleteLoading = true;
        let toDelete = this.deleteOptions.deleteCategory.length;
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
              message: (toDelete > 1 ? "Categories" : "Category") + " deleted successfully"
            });
          }
        };
        this.deleteOptions.deleteCategory.forEach(category => {
          CategoryService.deleteCategory(category)
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
          this.deleteOptions.deleteCategory.push(...this.selected);
        }
        this.deleteOptions.deleteDialog = true;
      },
      closeDeleteDialog() {
        this.deleteOptions.deleteCategory.splice(0, this.deleteOptions.deleteCategory.length);
        this.deleteOptions.deleteDialog = false;
        this.deleteOptions.deleteErrorMessage = "";
      },
      fetchAll() {
        this.loading = true;
        CategoryService.getAllCategories()
          .then(categories => {
            this.loading = false;
            this.categories = categories;
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
          editCategory: {
            name: {
              required,
              maxLength: maxLength(15)
            }
          }
        }
      };
      return validations;
    },
    watch: {
      '$v.editOptions.editCategory.$invalid': function _watch$vEditOptionseditCategory$invalid(value) {
        this.editOptions.valid = !value;
      }
    },
    computed: {
      deleteQuestionMessage() {
        if (this.deleteOptions.deleteCategory.length === 0) {
          return "No categories selected!";
        }
        if (this.deleteOptions.deleteCategory.length === 1) {
          return "The following category will be deleted:";
        }
        return "The following categories will be deleted:";
      },
      iseditCategoryNew() {
        return this.editOptions.editCategory.id === -1;
      },
      editDialogHeadline() {
        if (this.iseditCategoryNew) {
          return "Create category"
        }
        return "Edit category"
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
