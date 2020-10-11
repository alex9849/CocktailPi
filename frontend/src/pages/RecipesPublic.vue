<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el v-if="categoryName" label="Public recipes" :to="{name: 'publicrecipes'}"/>
      <q-breadcrumbs-el v-else label="Public recipes"/>
      <q-breadcrumbs-el v-if="categoryName" :label="categoryName"/>
    </q-breadcrumbs>
    <h5>Public recipes</h5>
    <div class="q-pa-md">
      <c-recipe-search-list
        :isOwnRecipes="false"
        :categoryId="$route.params.cid"
      />
    </div>
  </q-page>
</template>

<script>
  import CRecipeSearchList from "../components/CRecipeSearchList";
  import CategoryService from "../services/category.service"

  export default {
    name: "PublicRecipes",
    components: {CRecipeSearchList},
    data() {
      return {
        categoryName: null
      }
    },
    created() {
      let categoryId = this.$route.params.cid;
      this.setCategory(categoryId);
    },
    watch: {
      '$route.params.cid': function (value) {
        this.setCategory(value);
      }
    },
    methods: {
      setCategory(id) {
        if(!id) {
          this.categoryName = null;
        } else {
          this.categoryName = "Loading...";
          CategoryService.getCategory(id)
            .then(data => this.categoryName = data.name);
        }
      }
    }
  }
</script>

<style scoped>
</style>
