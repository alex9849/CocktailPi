<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="My recipes"/>
    </q-breadcrumbs>
    <h5 style="margin-bottom: 0">My Recipes</h5>

    <div class="q-pa-md">
      <q-table
        :data="recipes"
        :columns="columns"
        row-key="name"
        :visible-columns="['name']"
        flat
        :pagination.sync="pagination"
        hide-pagination
        hide-header

      >
        <template v-slot:top-right>
          <div class="q-gutter-x-sm"  style="display: inherit">
            <q-input
              v-model="searchName"
              outlined
              label="Search"
              dense
            />
            <q-btn
              text-color="black"
              color="info"
              :icon="mdiMagnify"
              rounded
            />
          </div>
        </template>
        <template v-slot:body="props" >
          <q-card
            @click="$router.push({name: 'recipedetails', params: {id: props.row.id}})"
            style="cursor: pointer; margin: 10px;"
          >
            <q-card-section
              style="padding: 10px"
            >
              <div
                class="row"
              >
                <q-img
                  src="../assets/cocktail-solid.png"
                  :ratio="16/9"
                  class="col rounded-borders"
                  style="max-width: 225px; max-height: 180px"
                />
                <div class="col" style="padding-left: 10px; position: relative">
                  <h5
                    style="margin: 0; padding-bottom: 10px;"
                  >
                    <b>{{ props.row.name }}</b>
                  </h5>
                  <div>
                    {{ props.row.shortDescription }}
                  </div>

                  <div class="row" style="position: absolute; bottom: 0; left: 0; right: 0; padding-inline: 10px">
                    <div class="col">
                      Ingredients:
                      <q-chip v-if="index < 4" v-for="(name, index) in uniqueIngredientNames(props.row.recipeIngredients)">
                        {{ index !== 3?name:'...' }}
                      </q-chip>
                    </div>
                    <div class="col" style="text-align: right; max-width: max-content">
                      by {{ props.row.owner.username }}
                    </div>
                  </div>
                </div>
              </div>
            </q-card-section>
          </q-card>
        </template>
      </q-table>

      <div class="row justify-center q-mt-md">
        <q-pagination
          v-model="pagination.page"
          color="grey-8"
          :max="pagesNumber"
          :max-pages="9"
          :boundary-numbers="true"
          size="sm"
        />
      </div>
    </div>
  </q-page>
</template>

<script>
  import {mdiMagnify} from '@quasar/extras/mdi-v5';
  import RecipeService from "../services/recipe.service"

  export default {
    data () {
      return {
        searchName: '',
        pagination: {
          sortBy: 'name',
          descending: false,
          page: 1,
          rowsPerPage: 8
          // rowsNumber: xx if getting data from a server
        },
        columns: [
          {
            name: 'name',
            label: 'Name',
            align: 'left',
            field: row => row.name,
            format: val => `${val}`,
            sortable: true
          },
          { name: 'shortDescription', align: 'center', label: 'Short description', field: 'shortDescription', sortable: true },
          { name: 'owner', label: 'Owner', field: 'owner.username', sortable: true }
        ],
        recipes: []
      }
    },
    methods: {
      uniqueIngredientNames(productionSteps) {
        console.log(productionSteps);
        let unique = new Set();
        for(let productionStep of productionSteps) {
          for(let ingredient of productionStep) {
            unique.add(ingredient.ingredient.name);
          }
        }
        return Array.from(unique.values());
      },
      fetchRecipes() {
        RecipeService.getRecipes()
          .then(recipes => this.recipes = recipes)
      }
    },
    computed: {
      pagesNumber () {
        return Math.ceil(this.recipes.length / this.pagination.rowsPerPage)
      }
    },
    created() {
      this.mdiMagnify = mdiMagnify;
      this.fetchRecipes();
    }
  }
</script>

<style scoped>

</style>
