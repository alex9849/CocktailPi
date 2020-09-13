<template>
  <q-form>
    <q-input
      v-model="value.timePerClInMs"
    />
    <q-input
      v-model="value.tubeFillingQuantityInMl"
    />
    <q-input
      v-model="value.gpioPin"
    />
    <ingredient-selector
      v-model="value.currentIngredient"
    />
  </q-form>
</template>

<script>
  import IngredientSelector from "./IngredientSelector";

  export default {
    name: "PumpEditorForm",
    components: {IngredientSelector},
    props: {
      value: {
        type: Object,
        required: true
      }
    }
  }
</script>

<style scoped>

</style>
