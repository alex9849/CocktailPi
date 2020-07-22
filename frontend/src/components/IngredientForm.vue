<template>
  <q-form>
    <q-select
      outlined
      v-model="value.ingredient"
      name="Ingredient"
      :options="ingredientOptions"
      @filter="filterIngredients"
      @filter-abort="abortFilterIngredients"
    />
    <q-input
      label="Amount (in ml)"
      type="number"
      outlined
      v-model="value.amount"
    />
    <slot name="below"/>
  </q-form>
</template>

<script>
  export default {
    name: "IngredientForm",
    props: {
      value: {
        type: Object,
        required: true
      }
    },
    data() {
      return {
        ingredientOptions: []
      }
    },
    methods: {
      filterIngredients(val, update, abort) {

      },
      abortFilterIngredients() {

      }
    }
  }
</script>

<style scoped>

</style>
