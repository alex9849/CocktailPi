<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="Ingredient Management"/>
    </q-breadcrumbs>
    <h5>Ingredients you own</h5>
    <div class="q-pa-md q-gutter-sm" style="display: flex; flex-direction: row-reverse;">
      <q-btn
        color="negative"
        label="Delete selected ingredients"
        :disable="loading"
        @click=""
        no-caps
      />
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
        @click=""
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
            >
              <q-tooltip>
                Edit
              </q-tooltip>
            </q-btn>
            <q-btn
              :icon="mdiDelete"
              color="red"
              @click=""
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
      @clickAbort="() => {editOptions.addIngredient = null}"
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
      style="padding-top: 15px"
    >Ingredients you should buy</h5>
  </q-page>
</template>

<script>
import CIngredientSelector from "components/CIngredientSelector";
import {required} from "vuelidate/lib/validators";
import CEditDialog from "components/CEditDialog";

export default {
  name: "MyBar",
  components: {CEditDialog, CIngredientSelector},
  data() {
    return {
      columns: [
        {name: 'name', label: 'Ingredient', field: 'name', align: 'center'},
        {name: 'alcoholContent', label: 'Alcohol content', field: 'alcoholContent', align: 'center'},
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
        saving: false,
        editDialog: false,
        valid: false,
        addIngredient: null
      }
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

@media screen and (min-width: 600px) {
  .with-desktop {
    width: 500px;
  }
}

.row2 {
  background-color: #f3f3fa;
}
</style>
