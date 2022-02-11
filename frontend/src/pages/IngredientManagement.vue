<template>
  <q-page class="page-content" padding>
    <h5>Ingredient Management</h5>
    <h6>Ingredient groups</h6>
    <c-ingredient-group-management/>
    <h6>Ingredients</h6>
    <c-ingredient-management/>
  </q-page>
</template>

<script>
import CIngredientManagement from 'components/CIngredientManagement'
import CIngredientGroupManagement from 'components/CIngredientGroupManagement'

export default {
  name: 'IngredientManagement',
  components: { CIngredientGroupManagement, CIngredientManagement }
}
</script>

<style scoped>
</style>
