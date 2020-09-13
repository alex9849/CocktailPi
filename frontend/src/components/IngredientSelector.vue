<template>
  <q-select
    outlined
    v-model="selectedIngredient"
    use-input
    clearable
    hide-dropdown-icon
    label="Ingredient"
    :options="ingredientOptions"
    option-label="name"
    option-value="id"
    input-debounce="0"
    @filter="filterIngredients"
    @filter-abort="abortFilterIngredients"
  >
    <template v-slot:option="scope">
      <q-item
        v-bind="scope.itemProps"
        v-on="scope.itemEvents"
      >
        <q-item-section>
          {{ scope.opt.name }}
          <q-item-label v-if="scope.opt.alcoholContent !== 0" caption>
            {{ scope.opt.alcoholContent }}% alcohol content
          </q-item-label>
        </q-item-section>
      </q-item>
    </template>
  </q-select>
</template>

<script>
  import IngridientService from "../services/ingredient.service";

  export default {
    name: "IngredientSelector",
    props: {
      value: {
        type: Object,
        required: true
      }
    },
    data() {
      return {
        ingredientOptions: [],
        selectedIngredient: null
      }
    },
    watch: {
      selectedIngredient() {
        this.$emit('input', this.selectedIngredient)
      }
    },
    methods: {
      filterIngredients(val, update, abort) {
        if(val.length < 1) {
          abort();
          return;
        }
        IngridientService.getIngredientsFilter(val)
          .then(ingridients => {
            update(() => {
              this.ingredientOptions = ingridients;
            })
          }, () => abort)
      },
      abortFilterIngredients() {
      }
    }
  }
</script>
