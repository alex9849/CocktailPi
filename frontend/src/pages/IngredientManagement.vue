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
      :pagination="{rowsPerPage: 0}"
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
            {{ props.row.alcoholContent }}
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
            />
            <q-btn
              :icon="mdiDelete"
              color="red"
              @click="() => {deleteIngredients.push(props.row); openDeleteDialog(false);}"
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
          {{ ingredients.length }} ingredients in total
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
          v-if="deleteIngredients.length === 0"
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
            v-for="(ingredient, index) in deleteIngredients"
          >
            {{ingredient.name}}
          </li>
        </ul>
      </template>
    </c-question>
    <q-dialog
      :value="editDialog"
      @hide="closeEditDialog"
    >
      <q-card style="width: 500px">
        <q-card-section class="text-center">
          <h5 style="margin-bottom: 10px">{{ editDialogHeadline }}</h5>
          <q-form class="innerpadding">
            <q-input
              label="Name"
              outlined
              v-model="editIngredient.name"
              filled
            />
            <q-input
              label="Alcohol content"
              outlined
              v-model="editIngredient.alcoholContent"
              filled
              type="number"
            />
            <div class="q-pa-md q-gutter-sm">
              <q-btn
                color="grey"
                label="Abort"
                style="width: 150px"
                @click="closeEditDialog"
              />
              <q-btn
                color="green"
                label="Save"
                style="width: 150px"
                @click="saveEditIngredient"
              />
            </div>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script>
  import {mdiDelete, mdiPencilOutline} from '@quasar/extras/mdi-v5';
  import IngredientService from "../services/ingredient.service";
  import CQuestion from "../components/CQuestion";

  export default {
    name: "IngredientManagement",
    components: {CQuestion},
    data() {
      return {
        columns: [
          {name: 'name', label: 'Ingredient', field: 'name', align: 'center'},
          {name: 'alcoholContent', label: 'Alcohol content', field: 'alcoholContent', align: 'center'},
          { name: 'actions', label: 'Actions', field: '', align:'center'}
        ],
        ingredients: [],
        selected: [],
        deleteIngredients: [],
        loading: false,
        editDialog: false,
        editIngredient: {
          id: -1,
          name: "",
          alcoholContent: 0
        },
        newIngredient: {
          id: -1,
          name: "",
          alcoholContent: 0
        },
        deleteLoading: false,
        deleteDialog: false
      }
    },
    methods: {
      onRefresh() {
        this.loading = true;
        let vm = this;
        setTimeout(vm.fetchAll, 500);
      },
      showEditDialog(ingredient) {
        if(ingredient) {
          this.editIngredient = Object.assign({}, ingredient);
        }
        this.editDialog = true;
      },
      closeEditDialog() {
        this.editIngredient = Object.assign({}, this.newIngredient);
        this.editDialog = false;
      },
      saveEditIngredient() {

      },
      deleteSelected() {

      },
      openDeleteDialog(forSelected) {
        if(forSelected) {
          this.deleteIngredients.push(...this.selected);
        }
        this.deleteDialog = true;
      },
      closeDeleteDialog() {
        this.deleteIngredients.splice(0, this.deleteIngredients.length);
        this.deleteDialog = false;
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
    computed: {
      deleteQuestionMessage() {
        if(this.deleteIngredients.length === 0) {
          return "No ingredients selected!";
        }
        if(this.deleteIngredients.length === 1) {
          return "The following ingredient will be deleted:";
        }
        return "The following ingredients will be deleted:";
      },
      editDialogHeadline() {
        if(this.editIngredient.id === -1) {
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
