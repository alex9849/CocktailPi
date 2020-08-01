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
        no-caps
      />
      <q-btn
        color="positive"
        label="Create ingredient"
        no-caps
      />
      <q-btn
        color="info"
        label="Refresh"
        no-caps
      />
    </div>
    <q-table
      :columns="columns"
      :data="ingredients"
      no-data-label="No ingredients found"
    >
    </q-table>

  </q-page>
</template>

<script>
  export default {
    name: "IngredientManagement",
    data() {
      return {
        columns: [
          {name: 'name', label: 'Ingredient', field: 'name', align: 'center'},
          {name: 'alcoholContent', label: 'Alcohol content', field: 'alcoholContent', align: 'center'},
          { name: 'actions', label: 'Actions', field: '', align:'center'}
        ],
        ingredients: []
      }
    }
  }
</script>

<style scoped>

</style>
