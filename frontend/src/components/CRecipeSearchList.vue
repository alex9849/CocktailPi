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
          <TopButtonArranger style="padding: 10px">
            <q-btn
              v-if="onlyOwnRecipes && isRecipeCreatorRole"
              color="negative"
              label="Delete selected recipes"
              no-caps
              :disable="loading"
              @click="openDeleteDialog"
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
              color="info"
              label="Refresh"
              no-caps
              :disable="loading"
              :loading="loading"
              @click="onRefreshButton"
            />
          </TopButtonArranger>
          <div
            class="searchBarSlot rounded-borders shadow-2 innerpadding"
            style="background-color: #fafafa; padding: 10px; margin-block: 10px"
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
                      class="row justify-evenly q-col-gutter-sm"
                      style="padding: 10px"
                    >
                      <q-checkbox
                        v-model="searchOptions.fabricableWithOwnedIngredients"
                        class="col-12 col-sm-6"
                      >
                        Fabricable with owned ingredients
                      </q-checkbox>
                      <q-checkbox
                        v-model="searchOptions.automaticallyFabricable"
                        class="col-12 col-sm-6"
                      >
                        Can be fabricated fully automatic
                      </q-checkbox>
                      <c-ingredient-selector
                        v-model="searchOptions.containsIngredients"
                        class="col-12 col-sm-8"
                        dense
                        label="Contains ingredients"
                        multiple
                        emit-value
                        map-options
                        use-chips
                      />
                      <q-select
                        v-model="searchOptions.orderBy"
                        class="col-12 col-sm-4"
                        label="Order by"
                        emit-value
                        map-options
                        round
                        outlined
                        dense
                        :options="orderByOptions"
                      />
                    </div>
                    <div
                      class="row justify-center"
                      style="padding-bottom: 10px"
                    >
                      <q-btn
                        color="red"
                        label="Reset filters"
                        @click="resetFilters()"
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
                  v-model="searchOptions.query"
                  outlined
                  label="Search"
                  dense
                  bg-color="white"
                  @keypress.enter="() => {pagination.page = 0; updateRoute(); fetchRecipes();}"
                >
                  <template slot="after">
                    <q-btn
                      text-color="black"
                      color="white"
                      :icon="mdiMagnify"
                      @click="() => {pagination.page = 0; updateRoute(); fetchRecipes();}"
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
        :value="pagination.page + 1"
        @input="onPageClick($event - 1)"
        color="grey-8"
        :max="pagination.totalPages"
        :max-pages="9"
        :boundary-numbers="true"
        size="md"
        outline
        direction-links
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
        <ul style="padding: 0; list-style: none">
          <li
            :key="index"
            v-for="(recipe, index) in deleteRecipes"
          >
            {{ recipe.name }}
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
import JsUtils from "src/services/JsUtils";
import TopButtonArranger from "components/TopButtonArranger";

export default {
  name: "CRecipeSearchList",
  components: {TopButtonArranger, CIngredientSelector, CRecipeList, CQuestion},
  props: {
    onlyOwnRecipes: {
      type: Boolean,
      default: false
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
      loading: false,
      deleteLoading: false,
      deleteDialog: false,
      deleteRecipes: [],
      isFilterExpanded: false,
      searchOptions: this.defaultSearchOptions(),
      pagination: {
        page: 0,
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
    const filterFromRoute = this.filterFromRoute();
    this.searchOptions = filterFromRoute.searchOptions;
    this.pagination.page = filterFromRoute.page
    this.fetchRecipes();
  },
  watch: {
    categoryId() {
      this.fetchRecipes();
    }
  },
  methods: {
    defaultSearchOptions() {
      return {
        query: '',
        automaticallyFabricable: false,
        fabricableWithOwnedIngredients: false,
        containsIngredients: [],
        orderBy: null
      }
    },
    filterFromRoute() {
      const queryParams = this.$route.query;
      return {
        searchOptions: {
          query: !!queryParams.query ? queryParams.query : '',
          automaticallyFabricable: !!queryParams.automaticallyFabricable ?
            (queryParams.automaticallyFabricable === 'true') : false,
          fabricableWithOwnedIngredients: !!queryParams.fabricableWithOwnedIngredients ?
            (queryParams.fabricableWithOwnedIngredients === 'true') : false,
          containsIngredients: !!queryParams.containsIngredients? queryParams.containsIngredients : [],
          orderBy: queryParams.orderBy
        },
        page: !!queryParams.page? Number(queryParams.page) : 0
      }
    },
    closeDeleteDialog() {
      this.deleteDialog = false;
    },
    openDeleteDialog() {
      this.deleteDialog = true;
    },
    resetFilters() {
      this.searchOptions = this.defaultSearchOptions();
    },
    deleteSelected() {
      this.deleteLoading = true;
      let vm = this;
      const deletePromises = [];
      this.deleteRecipes.forEach(recipe => {
        deletePromises.push(RecipeService.deleteRecipe(recipe.id))
      })
      Promise.all(deletePromises)
        .then(() => {
          vm.closeDeleteDialog();
          vm.deleteLoading = false;
          vm.fetchRecipes();
        }, err => {
          vm.deleteLoading = false;
          vm.fetchRecipes();
        })
    },
    onRefreshButton() {
      this.loading = true;
      this.recipes = [];
      let vm = this;
      setTimeout(() => {
        vm.fetchRecipes()
      }, 500);
    },
    updateRoute() {
      let query = {
        page: this.pagination.page
      };
      query = Object.assign(query, this.searchOptions);
      query = JsUtils.cleanObject(query);
      this.$router.replace({name: this.$route.name, query});
    },
    onPageClick(page) {
      if (this.pagination.page !== page) {
        this.pagination.page = page;
        this.updateRoute();
        this.fetchRecipes();
      }
    },
    fetchRecipes() {
      this.recipes = [];
      this.loading = true;
      return RecipeService.getRecipes(this.pagination.page,
        this.onlyOwnRecipes ? this.user.id : null,
        this.onlyOwnRecipes ? null : true,
        this.searchOptions.automaticallyFabricable,
        this.searchOptions.fabricableWithOwnedIngredients,
        this.searchOptions.containsIngredients,
        this.searchOptions.query,
        this.categoryId,
        this.searchOptions.orderBy
      ).then(page => {
        this.recipes = page.content;
        this.pagination.totalPages = page.totalPages;
        this.pagination.page = page.number;
        this.loading = false;
      }, error => {
        this.loading = false;
      });
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
</style>
