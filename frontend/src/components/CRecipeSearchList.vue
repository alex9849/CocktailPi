<template>
  <div>
    <c-recipe-list
      :recipes="recipes"
      :loading="loading"
    >
      <template slot="top-right">
        <div class="q-gutter-x-sm" style="display: inherit">
          <q-input
            v-model="unappliedSearchData.searchName"
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
        this.pagination.page = newValue? Number(newValue):1;
        this.fetchRecipes();
      },
      '$route.query.search'(newValue) {
        this.searchOptions.searchName = newValue?newValue:"";
        this.pagination.page = 1;
        this.fetchRecipes();
      }
    },
    methods: {
      updateRecipes() {
        this.searchOptions.searchName = this.unappliedSearchData.searchName;
        let query = {
          page: this.pagination.page
        };
        if (this.searchOptions.searchName && this.searchOptions.searchName.length !== 0) {
          query.search = this.searchOptions.searchName;
        }
        this.$router.push({name: this.$route.name, query});
      },
      fetchRecipes() {
        this.loading = true;
        RecipeService.getRecipes(this.pagination.page, this.isOwnRecipes?this.user.id:null, this.isOwnRecipes?null:true, this.searchOptions.searchName)
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
