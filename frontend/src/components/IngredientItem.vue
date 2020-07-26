<template>
  <div>
    <q-item
      v-for="(productionStep, index) in value.recipeIngredients"
      class="list-group-item"
      :key="index"
    >
      <q-item-section avatar>
        <q-avatar color="grey">{{ index + 1}}.</q-avatar>
      </q-item-section>
      <q-item-section v-if="productionStep.length > 1">hi</q-item-section>
      <div v-else>
        <q-item-section>ho</q-item-section>
      </div>
      <!--
      <q-item-section>
        {{ ingredient.amount }}ml {{ ingredient.ingredient.name }}
      </q-item-section>
      <q-item-section side>
        <q-btn
          :icon="mdiPencilOutline"
          @click="showIngredientEditor(ingredient)"
          dense
          flat
          rounded
        />
      </q-item-section>
      <q-item-section side>
        <q-btn
          :icon="mdiDelete"
          @click="value.recipeIngredients = value.recipeIngredients.filter(x => x !== ingredient)"
          dense
          flat
          rounded
        />
      </q-item-section>-->
    </q-item>
  </div>
</template>

<script>
  export default {
    name: "IngredientItem",
    props: {
      ingredient: {
        type: Object,
        required: true
      }
    }
  }
</script>

<style scoped>

</style>
