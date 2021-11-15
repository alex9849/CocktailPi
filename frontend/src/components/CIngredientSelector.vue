<template>
  <q-select
    outlined
    behavior="menu"
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
    :emit-value="emitValue"
    option-label="name"
    option-value="id"
    input-debounce="0"
    :map-options="mapOptions"
    :use-chips="useChips"
    :multiple="multiple"
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
import IngridientService from '../services/ingredient.service'

export default {
  name: 'CIngredientSelector',
  props: {
    value: {
      type: Object | Array
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
    multiple: {
      type: Boolean,
      default: false
    },
    emitValue: {
      type: Boolean,
      default: false
    },
    mapOptions: {
      type: Boolean,
      default: false
    },
    useChips: {
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
    filterManualIngredients: {
      type: Boolean,
      default: false
    },
    noInputOptions: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
      fetchedOptions: [],
      stringInput: '',
      selectedIngredient: this.value
    }
  },
  computed: {
    ingredientOptions () {
      if (this.stringInput.length < 2) {
        return this.noInputOptions
      }
      return this.fetchedOptions
    }
  },
  methods: {
    filterIngredients (val, update, abort) {
      this.stringInput = val
      if (val.length < 2) {
        update()
        return
      }
      IngridientService.getIngredientsFilter(val, this.filterManualIngredients)
        .then(ingridients => {
          update(() => {
            this.fetchedOptions = ingridients
          })
        }, () => abort)
    },
    abortFilterIngredients () {
    }
  }
}
</script>
