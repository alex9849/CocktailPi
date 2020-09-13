<template>
  <q-form>
    <q-input
      label="Nr"
      v-model="value.nr"
    />
    <q-input
      label="Pump time per cl in ms"
      v-model="value.timePerClInMs"
    />
    <q-input
      label="tube capacity in ml"
      v-model="value.tubeCapacityInMl"
    />
    <q-input
      label="GPIO-Pin"
      v-model="value.gpioPin"
    />
    <c-ingredient-selector
      label="Current ingredient"
      v-model="value.currentIngredient"
    />
  </q-form>
</template>

<script>

  import CIngredientSelector from "./CIngredientSelector";

  export default {
    name: "PumpEditorForm",
    components: {CIngredientSelector},
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
