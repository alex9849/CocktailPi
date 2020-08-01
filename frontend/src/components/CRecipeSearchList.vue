<template>
  <div>
    <c-recipe-list
      :recipes="recipes"
      :loading="loading"
    >
      <template slot="top">
        <div>
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
                color="positive"
                label="Create recipe"
                no-caps
                :disable="loading"
                :to="{name: 'recipeadd'}"
              />
              <q-btn
                color="negative"
                label="Delete selected recipes"
                no-caps
                :disable="loading"
                :to="{name: 'recipeadd'}"
              />
            </div>
          </div>
          <div
            class="row searchBarSlot rounded-borders shadow-2"
            :style="'background-color: ' + searchBarColor"
          >
            <div
              class="col"
              style="display: flex; align-items: center">
              <b>Display-options</b>
            </div>
            <div class="col"/>
            <div class="col q-gutter-x-sm" style="display: contents; max-width: max-content">
              <q-input
                v-model="unappliedSearchData.searchName"
                outlined
                label="Search"
                dense
                bg-color="white"
                @keypress.enter="() => {pagination.page = 1; updateRecipes();}"
              />
              <q-btn
                text-color="black"
                color="white"
                label="Search"
                :icon="mdiMagnify"
                @click="() => {pagination.page = 1; updateRecipes();}"
              />
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
  </div>
</template>
<script>
  import {mdiMagnify} from '@quasar/extras/mdi-v5';
  import RecipeService from "../services/recipe.service"
  import {mapGetters} from "vuex";
  import CRecipeList from "../components/CRecipeList";

  export default {
    name: "CRecipeSearchList",
    components: {CRecipeList},
    props: {
      isOwnRecipes: {
        type: Boolean,
        default: false
      },
      searchBarColor: {
        type: String,
        default: '#fafafa'
      }
    },
    data() {
      return {
        unappliedSearchData: {
          searchName: '',
        },
        loading: false,
        searchOptions: {
          searchName: '',
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
        this.searchOptions.searchName = this.$route.query.search;
        this.unappliedSearchData.searchName = this.$route.query.search;
      }
      this.fetchRecipes();
    },
    watch: {
      '$route.query.page'(newValue) {
        this.pagination.page = newValue ? Number(newValue) : 1;
        this.fetchRecipes();
      },
      '$route.query.search'(newValue) {
        this.searchOptions.searchName = newValue ? newValue : "";
        this.fetchRecipes();
      }
    },
    methods: {
      onRefreshButton() {
        this.loading = true;
        let vm = this;
        setTimeout(() => {
          vm.fetchRecipes()
        }, 500);
      },
      updateRecipes() {
        this.searchOptions.searchName = this.unappliedSearchData.searchName;
        let query = {
          page: this.pagination.page
        };
        if (this.searchOptions.searchName) {
          query.search = this.searchOptions.searchName;
        }
        this.$router.push({name: this.$route.name, query});
      },
      fetchRecipes() {
        this.loading = true;
        RecipeService.getRecipes(this.pagination.page, this.isOwnRecipes ? this.user.id : null, this.isOwnRecipes ? null : true, this.searchOptions.searchName)
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
        user: 'auth/getUser'
      })
    }
  }
</script>

<style scoped>
  .searchBarSlot {
    margin-block: 10px;
    padding: 10px;
  }
</style>
