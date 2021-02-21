<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="Ingredient Management"/>
    </q-breadcrumbs>
    <h5>Ingredients you own</h5>
    <TopButtonArranger style="padding: 10px">
      <q-btn
        color="positive"
        label="Add ingredient"
        :disable="loading"
        @click="editOptions.editDialog = true"
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
    </TopButtonArranger>
    <q-table
      :columns="columns"
      :data="ingredients"
      :loading="loading"
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
            key="actions"
            class="q-pa-md q-gutter-x-sm"
            :props="props"
          >
            <q-btn
              :icon="mdiDelete"
              color="red"
              @click="onRemoveOwnedIngredient(props.row.id)"
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
    <c-edit-dialog
      v-model="editOptions.editDialog"
      :error-message="editOptions.editErrorMessage"
      title="Add Ingredient"
      :saving="editOptions.saving"
      :valid="editOptions.valid"
      @clickAbort="closeEditDialog"
      @clickSave="onAddOwnedIngredient"
    >
      <c-ingredient-selector
        label="Ingredient"
        :disable="editOptions.saving"
        v-model="editOptions.addIngredient"
        @input="$v.editOptions.addIngredient.$touch()"
        :rules="[val => $v.editOptions.addIngredient.required || 'Required']"
      />
    </c-edit-dialog>

    <h5
      style="padding-top: 15px; padding-bottom: 15px"
    >
      Ingredients you should buy
    </h5>
    <q-card flat class="bg-grey-4">
      <q-card-section class="text-center">
        Feature not implemented yet!
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script>
import CIngredientSelector from "components/CIngredientSelector";
import {required} from "vuelidate/lib/validators";
import CEditDialog from "components/CEditDialog";
import UserService from "../services/user.service";
import {mdiDelete} from '@quasar/extras/mdi-v5';
import TopButtonArranger from "components/TopButtonArranger";

export default {
  name: "MyBar",
  components: {TopButtonArranger, CEditDialog, CIngredientSelector},
  data() {
    return {
      columns: [
        {name: 'name', label: 'Ingredient', field: 'name', align: 'center'},
        {name: 'alcoholContent', label: 'Alcohol content', field: 'alcoholContent', align: 'center'},
        {name: 'actions', label: 'Actions', field: '', align: 'center'}
      ],
      ingredients: [],
      loading: false,
      editOptions: {
        editErrorMessage: "",
        saving: false,
        editDialog: false,
        valid: false,
        addIngredient: null
      }
    }
  },
  created() {
    this.loading = true;
    this.updateOwnedIngredients()
      .finally(() => this.loading = false);
    this.mdiDelete = mdiDelete;
  },
  methods: {
    onRefresh() {
      this.loading = true;
      this.updateOwnedIngredients()
        .finally(() => this.loading = false);

    },
    updateOwnedIngredients() {
      return UserService.getMyOwnedIngredients()
        .then(ingredients => this.ingredients = ingredients)

    },
    closeEditDialog() {
      this.editOptions.editDialog = false;
      this.editOptions.addIngredient = null;
    },
    onAddOwnedIngredient() {
      let vm = this;
      let onSuccess = function () {
        vm.editOptions.editErrorMessage = "";
        vm.$q.notify({
          type: 'positive',
          message: "Ingredient added successfully"
        });
        vm.closeEditDialog();
        vm.onRefresh();
      };

      let onError = function (error) {
        vm.editOptions.editErrorMessage = error.response.data.message;
        vm.$q.notify({
          type: 'negative',
          message: error.response.data.message
        });
      };

      this.editOptions.saving = true
      UserService.addToMyOwnedIngredients(this.editOptions.addIngredient.id)
        .then(onSuccess, onError)
        .finally(() => this.editOptions.saving = false);

    },
    onRemoveOwnedIngredient(id) {
      UserService.removeFromMyOwnedIngredients(id)
        .then(() => {
          this.onRefresh();
          this.$q.notify({
            type: 'positive',
            message: "Ingredient removed successfully"
          });
          }, err => {
          this.$q.notify({
            type: 'negative',
            message: err.response.data.message
          });
        });
    }
  },
  watch: {
    '$v.editOptions.addIngredient.$invalid': function _watch$vEditOptionsEditIngredient$invalid(value) {
      this.editOptions.valid = !value;
    }
  },
  validations() {
    let validations = {
      editOptions: {
        addIngredient: {
          required
        }
      }
    };
    return validations;
  },
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
