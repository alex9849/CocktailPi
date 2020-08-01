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
        no-caps
      />
      <q-btn
        color="info"
        label="Refresh"
        :disable="loading"
        :loading="loading"
        no-caps
      />
    </div>
    <q-table
      :columns="columns"
      :data="ingredients"
      :loading="loading"
      :selected.sync="selected"
      selection="multiple"
      hide-pagination
      :pagination="{rowsPerPage: 0}"
      no-data-label="No ingredients found"
    >
      <template v-slot:body-cell-actions="props">
        <q-td
          class="q-pa-md q-gutter-x-sm"
          :props="props"
        >
          <q-btn
            :icon="mdiPencilOutline"
            text-color="white"
            :style="{backgroundColor: '#31ccec'}"
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
        deleteLoading: false,
        deleteDialog: false
      }
    },
    methods: {
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
      }
    }
  }
</script>

<style scoped>

</style>
