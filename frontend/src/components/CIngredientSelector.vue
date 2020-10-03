<template>
  <q-select
    outlined
    :loading="loading"
    :dense="dense"
    :value="value"
    :bg-color="bgColor"
    @input="$emit('input', $event)"
    use-input
    :clearable="clearable"
    :disable="disable"
    hide-dropdown-icon
    :label="label"
    :options="ingredientOptions"
    option-label="name"
    option-value="id"
    input-debounce="0"
    @filter="filterIngredients"
    @filter-abort="abortFilterIngredients"
    :rules="rules"
  >
    <template v-slot:option="scope">
      <q-item
        v-bind="scope.itemProps"
        v-on="scope.itemEvents"
      >
        <q-item-section>
          {{ scope.opt.name }}
          <slot name="afterIngredientName" :scope="scope">
            <q-item-label v-if="scope.opt.alcoholContent !== 0" caption>
              {{ scope.opt.alcoholContent }}% alcohol content
            </q-item-label>
          </slot>
        </q-item-section>
      </q-item>
    </template>
  </q-select>
</template>

<script>
  import IngridientService from "../services/ingredient.service";

  export default {
    name: "CIngredientSelector",
    props: {
      value: {
        type: Object
      },
      label: {
        type: String,
        default: 'Ingredient'
      },
      dense: {
        type: Boolean,
        default: false
      },
      loading: {
        type: Boolean,
        default: false
      },
      disable: {
        type: Boolean,
        default: false
      },
      clearable: {
        type: Boolean,
        default: false
      },
      rules: {
        type: Array,
        default: () => []
      },
      bgColor: {
        type: String,
        default: undefined
      },
      noInputOptions: {
        type: Array,
        default: () => []
      }
    },
    data() {
      return {
        fetchedOptions: [],
        stringInput: '',
        selectedIngredient: this.value
      }
    },
    computed: {
      ingredientOptions() {
        if(this.stringInput.length < 1) {
          return this.noInputOptions;
        }
        return this.fetchedOptions;
      }
    },
    methods: {
      filterIngredients(val, update, abort) {
        this.stringInput = val;
        if(val.length < 1) {
          update();
          return;
        }
        IngridientService.getIngredientsFilter(val)
          .then(ingridients => {
            update(() => {
              this.fetchedOptions = ingridients;
            })
          }, () => abort)
      },
      abortFilterIngredients() {
      }
    }
  }
</script>
