<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el label="My recipes"/>
    </q-breadcrumbs>
    <h5>My Recipes</h5>

    <div class="q-pa-md">
      <c-recipe-list
        :recipes="recipes"
        :loading="loading"
      >
        <template slot="top-right">
          <div class="q-gutter-x-sm" style="display: inherit">
            <q-input
              v-model="searchName"
              outlined
              label="Search"
              dense
              @keypress.enter="updateRecipes"
            />
            <q-btn
              text-color="black"
              color="info"
              :icon="mdiMagnify"
              @click="updateRecipes"
              rounded
            />
            <q-btn
              color="positive"
              label="Create recipe"
              no-caps
              :to="{name: 'recipeadd'}"
            />
          </div>
        </template>
      </c-recipe-list>

      <div class="row justify-center q-mt-md">
        <q-pagination
          :value="pagination.page"
          @input="page => {pagination.page = page; updateRecipes();}"
          color="grey-8"
          :max="pagination.totalPages"
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
  import {mapGetters} from "vuex";
  import CRecipeList from "../components/CRecipeList";

  export default {
    components: {CRecipeList},
    data() {
      return {
        searchName: '',
        activeSearchName: '',
        loading: false,
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
        this.activeSearchName = this.$route.query.search;
        this.searchName = this.$route.query.search;
      }
      this.fetchRecipes();
    },
    watch: {
      '$route.query.page'(newValue) {
        this.pagination.page = newValue? Number(newValue):1;
        this.fetchRecipes();
      },
      '$route.query.search'(newValue) {
        this.activeSearchName = newValue?newValue:"";
        this.fetchRecipes();
      }
    },
    methods: {
      updateRecipes() {
        this.activeSearchName = this.searchName;
        let query = {
          page: this.pagination.page
        };
        if (this.activeSearchName && this.activeSearchName.length !== 0) {
          query.search = this.activeSearchName;
        }
        this.$router.push({name: 'myrecipes', query});
      },
      fetchRecipes() {
        this.loading = true;
        RecipeService.getRecipes(this.pagination.page, this.user.id, null, this.activeSearchName)
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

</style>
