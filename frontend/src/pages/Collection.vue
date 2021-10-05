<template>
  <q-page padding>
    <q-breadcrumbs>
      <q-breadcrumbs-el :to="{name: 'mycollections'}" label="My collections"/>
      <q-breadcrumbs-el label="Collection name"/>
    </q-breadcrumbs>
    <h5>Collection name</h5>
    <div class="row q-col-gutter-sm">
      <div class="col-12 col-sm-8 col-md-9">
        <c-recipe-list
          list-body-color="transparent"
          flat
          :recipes="[]"
          class="rounded-borders q-card q-card--bordered q-card--flat no-shadow"
        />
      </div>
      <div class="col-12 col-sm-4 col-md-3">
        <q-card class="rounded-borders"
                bordered style="background-color: #f3f3fa"
        >
          <q-card-section>
            test
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script>
import CRecipeList from "components/CRecipeList";

export default {
  name: "Collection",
  components: {CRecipeList},
}
</script>

<style scoped>

</style>
