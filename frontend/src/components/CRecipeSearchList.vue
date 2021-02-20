<template>
  <div>
    <c-recipe-list
      :recipes="recipes"
      :loading="loading"
      @selectionChange="deleteRecipes = $event"
      :selectable="onlyOwnRecipes"
    >
      <template slot="top">
        <div style="margin-bottom: 10px">
          <div class="row" style="padding: 10px">
            <div class="col"/>
            <div class="col q-gutter-x-sm" style="display: contents; max-width: max-content">
              <q-btn
                color="info"
                label="Refresh"
                no-caps
                :disable="loading"
                :loading="loading"
                @click="onRefreshButton"
              />
              <q-btn
                v-if="isRecipeCreatorRole"
                color="positive"
                label="Create recipe"
                no-caps
                :disable="loading"
                :to="{name: 'recipeadd'}"
              />
              <q-btn
                v-if="onlyOwnRecipes && isRecipeCreatorRole"
                color="negative"
                label="Delete selected recipes"
                no-caps
                :disable="loading"
                @click="openDeleteDialog"
              />
            </div>
          </div>
          <div
            class="searchBarSlot rounded-borders shadow-2 innerpadding"
            :style="'background-color: ' + searchBarColor"
          >
            <div
              class="row"
            >
              <div
                class="col"
                style="display: flex; align-items: center">
                <b>Search-options</b>
              </div>
              <div class="col"/>
            </div>
            <div
              class="row"
            >
              <q-card
                flat bordered
                style="width: 100%"
              >
                <q-card-section style="padding: 0px">
                  <q-expansion-item
                    style="width: 100%"
                    label="Expert-Search"
                    v-model="isFilterExpanded"
                    :header-class="isFilterExpanded? 'bg-grey-2' : ''"
                  >
                    <div
                      class="row justify-evenly"
                      style="padding: 10px"
                    >
                      <q-checkbox
                        v-model="unappliedSearchData.inBar"
                      >
                        Orderable with owned ingredients
                      </q-checkbox>
                      <q-checkbox
                        v-model="unappliedSearchData.orderable"
                      >
                        Orderable
                      </q-checkbox>
                      <c-ingredient-selector
                        v-model="unappliedSearchData.containsIngredients"
                        dense
                        multiple
                        emit-value
                        map-options
                        use-chips
                      />
                      <q-select
                        v-model="unappliedSearchData.orderBy"
                        label="Order by"
                        emit-value
                        map-options
                        round
                        outlined
                        dense
                        :options="orderByOptions"
                        style="min-width: 200px"
                      />
                    </div>
                  </q-expansion-item>
                </q-card-section>
              </q-card>
            </div>
            <div
              class="row"
            >
              <div class="col" style="display: block">
                <q-input
                  v-model="unappliedSearchData.query"
                  outlined
                  label="Search"
                  dense
                  bg-color="white"
                  @keypress.enter="() => {pagination.page = 1; updateRecipes();}"
                >
                  <template slot="after">
                    <q-btn
                      text-color="black"
                      color="white"
                      :icon="mdiMagnify"
                      @click="() => {pagination.page = 1; updateRecipes();}"
                    />
                  </template>
                </q-input>
              </div>
            </div>
          </div>
        </div>
      </template>
    </c-recipe-list>
    <div class="row justify-center q-mt-md">
      <q-pagination
        :value="pagination.page"
        @input="page => {if(pagination.page !== page) {pagination.page = page; updateRecipes();}}"
        color="grey-8"
        :max="pagination.totalPages"
        :max-pages="9"
        :boundary-numbers="true"
        size="sm"
      />
    </div>
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
          v-if="deleteRecipes.length === 0"
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
            v-for="(recipe, index) in deleteRecipes"
          >
            {{recipe.name}}
          </li>
        </ul>
      </template>
    </c-question>
  </div>
</template>
<script>
import {mdiMagnify} from '@quasar/extras/mdi-v5';
import RecipeService from "../services/recipe.service"
import {mapGetters} from "vuex";
import CRecipeList from "../components/CRecipeList";
import CQuestion from "./CQuestion";
import CIngredientSelector from "components/CIngredientSelector";

export default {
  name: "CRecipeSearchList",
  components: {CIngredientSelector, CRecipeList, CQuestion},
  props: {
    onlyOwnRecipes: {
      type: Boolean,
      default: false
    },
    searchBarColor: {
      type: String,
      default: '#fafafa'
      },
      categoryId: {
        type: Number
      }
    },
    data() {
      return {
        orderByOptions: [{
          label: 'Name asc',
          value: 'nameAsc'
        }, {
          label: 'Name desc',
          value: 'nameDesc'
        }, {
          label: 'Last update',
          value: 'lastUpdateAsc'
        }, {
          label: 'Least update',
          value: 'lastUpdateDesc'
        }, {
          label: 'Author asc',
          value: 'authorAsc'
        }, {
          label: 'Author desc',
          value: 'authorDesc'
        }],
        unappliedSearchData: {
          query: '',
          orderable: false,
          containsIngredients: [],
          inBar: false,
          orderBy: null
        },
        loading: false,
        deleteLoading: false,
        deleteDialog: false,
        deleteRecipes: [],
        isFilterExpanded: false,
        searchOptions: {
          query: '',
          orderable: null,
          inBar: null,
          containsIngredients: [],
          orderBy: null
        },
        pagination: {
          page: 1,
          totalPages: 1
        },
        recipes: []
      }
    },
    created() {
      this.mdiMagnify = mdiMagnify;
      if (this.$route.query.page) {
        this.pagination.page = Number(this.$route.query.page);
        this.pagination.totalPages = Number(this.$route.query.page);
      }
      if (this.$route.query.search) {
        this.searchOptions.query = this.$route.query.query;
        this.unappliedSearchData.query = this.$route.query.query;
      }
      this.fetchRecipes();
    },
    watch: {
      '$route.query': {
        deep: true,
        handler: function (newValue) {
          this.pagination.page = newValue.page ? Number(newValue.page) : 1;
          delete newValue.page;
          this.searchOptions = Object.assign({}, newValue);
          this.fetchRecipes();
        }
      },
      categoryId() {
        this.recipes = [];
        this.searchOptions.query = "";
        this.fetchRecipes();
      }
    },
    methods: {
      closeDeleteDialog() {
        this.deleteDialog = false;
      },
      openDeleteDialog() {
        this.deleteDialog = true;
      },
      deleteSelected() {
        this.deleteLoading = true;
        let toDelete = this.deleteRecipes.length;
        let deleted = 0;
        let vm = this;
        let afterDelete = function() {
          if(deleted === toDelete) {
            vm.closeDeleteDialog();
            vm.deleteLoading = false;
            vm.fetchRecipes();
          }
        };
        this.deleteRecipes.forEach(recipe => {
          RecipeService.deleteRecipe(recipe)
            .then(() => {
              deleted++;
              afterDelete();
            }, err => {
              vm.deleteLoading = false;
              vm.fetchRecipes();
            })
        });
        afterDelete();
      },
      onRefreshButton() {
        this.loading = true;
        let vm = this;
        setTimeout(() => {
          vm.fetchRecipes()
        }, 500);
      },
      updateRecipes() {
        let query = {
          page: this.pagination.page
        };
        this.searchOptions = Object.assign({}, this.unappliedSearchData);
        query = Object.assign(query, this.unappliedSearchData);
        for (let propName in query) {
          if (query.hasOwnProperty(propName) && !query[propName]) {
            delete query[propName];
          }
        }
        this.$router.push({name: this.$route.name, query});
      },
      fetchRecipes() {
        this.loading = true;
        RecipeService.getRecipes(this.pagination.page,
          this.onlyOwnRecipes ? this.user.id : null,
          this.onlyOwnRecipes ? null : true,
          this.searchOptions.orderable,
          this.searchOptions.inBar,
          this.searchOptions.containsIngredients,
          this.searchOptions.query,
          this.categoryId,
          this.searchOptions.orderBy
        )
          .then(pageable => {
            this.recipes = pageable.content;
            this.pagination.totalPages = pageable.totalPages;
            this.loading = false;
          }, error => {
            this.loading = false;
          })
      }
    },
    computed: {
      ...mapGetters({
        isRecipeCreatorRole: 'auth/isRecipeCreator',
        user: 'auth/getUser'
      }),
      deleteQuestionMessage() {
        if (this.deleteRecipes.length === 0) {
          return "No recipes selected!";
        }
        if (this.deleteRecipes.length === 1) {
          return "The following recipe will be deleted:";
        }
        return "The following recipes will be deleted:";
      }
    }
  }
</script>

<style scoped>
  .searchBarSlot {
    margin-block: 10px;
    padding: 10px;
  }
</style>
