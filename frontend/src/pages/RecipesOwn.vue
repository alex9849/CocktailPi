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
              @keypress.enter="fetchRecipes"
            />
            <q-btn
              text-color="black"
              color="info"
              :icon="mdiMagnify"
              @click="fetchRecipes"
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
          v-model="pagination.page"
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
    data () {
      return {
        searchName: '',
        loading: false,
        pagination: {
          sortBy: 'name',
          descending: false,
          page: 1,
          totalPages: 1
          // rowsNumber: xx if getting data from a server
        },
        recipes: []
      }
    },
    methods: {
      fetchRecipes() {
        this.loading = true;
        RecipeService.getRecipes(this.pagination.page, this.user.id, null, this.searchName)
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
    },
    created() {
      this.mdiMagnify = mdiMagnify;
      this.fetchRecipes();
    }
  }
</script>

<style scoped>

</style>
