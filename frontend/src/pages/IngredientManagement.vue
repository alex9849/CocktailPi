<template>
  <q-page class="page-content" padding>
    <h5>Ingredient Management</h5>
    <c-ingredient-management/>
  </q-page>
</template>

<script>
import CIngredientManagement from 'components/CIngredientManagement'

export default {
  name: 'IngredientManagement',
  components: { CIngredientManagement }
}
</script>

<style scoped>
</style>
