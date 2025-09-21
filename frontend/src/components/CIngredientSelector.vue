<template>
  <q-select
    :loading="loading"
    :dense="dense"
    :model-value="selected"
    @update:model-value="$emit('update:selected', $event)"
    :bg-color="bgColor"
    use-input
    :color="color"
    :dark="dark"
    :rounded="rounded"
    :outlined="outlined"
    :clearable="clearable"
    :disable="disable"
    hide-dropdown-icon
    :filled="filled"
    :label="formLabel"
    :options="ingredientOptions"
    :emit-value="emitValue"
    option-label="name"
    option-value="id"
    input-debounce="0"
    :error-message="errorMessage"
    :error="error"
    :map-options="mapOptions"
    :use-chips="useChips"
    :multiple="multiple"
    @filter="filterIngredients"
    @filter-abort="abortFilterIngredients"
    :rules="rules"
    :hide-bottom-space="hideBottomSpace"
  >
    <template v-slot:option="scope">
      <q-item
        v-bind="scope.itemProps"
      >
        <q-item-section>
          {{ scope.opt.name }}
          <slot name="afterIngredientName" :scope="scope">
            <q-item-label v-if="getAlcoholContentString(scope.opt)" caption>
              {{ getAlcoholContentString(scope.opt) }}
            </q-item-label>
          </slot>
        </q-item-section>
      </q-item>
    </template>
    <template v-slot:label v-if="$slots.label">
      <slot name="label"/>
    </template>
  </q-select>
</template>

<script>
import IngredientService from '../services/ingredient.service'

export default {
  name: 'CIngredientSelector',
  props: {
    selected: {
      type: [Object, Array]
    },
    dark: {
      type: Boolean,
      default: false
    },
    label: {
      type: String
    },
    dense: {
      type: Boolean,
      default: false
    },
    hideBottomSpace: {
      type: Boolean,
      default: false
    },
    rounded: {
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
    filled: {
      type: Boolean,
      default: false
    },
    outlined: {
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
    color: {
      type: String,
      default: undefined
    },
    filterManualIngredients: {
      type: Boolean,
      default: false
    },
    filterAutomaticIngredients: {
      type: Boolean,
      default: false
    },
    onlyGroupChildren: {
      type: Number,
      required: false
    },
    filterIngredientGroups: {
      type: Boolean,
      default: false
    },
    noInputOptions: {
      type: Array,
      default: () => []
    },
    afterFetchFilterFunction: {
      type: Function,
      default: x => x
    },
    error: {
      type: Boolean,
      default: false
    },
    errorMessage: {
      type: String,
      required: false
    }
  },
  emits: ['update:selected'],
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
      const optionsCopy = this.fetchedOptions.slice()
      const perfectMatch = optionsCopy.find(x => x.name.toLowerCase() === this.stringInput.toLowerCase())
      if (perfectMatch) {
        const perfectMatchIndex = optionsCopy.indexOf(perfectMatch)
        optionsCopy.splice(perfectMatchIndex, 1)
        optionsCopy.unshift(perfectMatch)
      }

      return optionsCopy
    },
    formLabel () {
      if (this.label) {
        return this.label
      }
      return this.$t('component.ingredient_selector.default_label')
    }
  },
  methods: {
    getAlcoholContentString (ingredient) {
      if (ingredient.type === 'group') {
        if (ingredient.minAlcoholContent === ingredient.maxAlcoholContent) {
          if (!ingredient.minAlcoholContent) {
            return null
          }
          return this.$t('component.ingredient_selector.alc_content',
            { nr: ingredient.minAlcoholContent })
        }
        return this.$t('component.ingredient_selector.alc_content_range',
          { min: ingredient.minAlcoholContent, max: ingredient.maxAlcoholContent })
      }
      if (ingredient.alcoholContent === 0) {
        return null
      }
      return this.$t('component.ingredient_selector.alc_content',
        { nr: ingredient.alcoholContent })
    },
    filterIngredients (val, update, abort) {
      this.stringInput = val
      if (val.length < 2) {
        update()
        return
      }
      IngredientService.getIngredientsFilter(val, this.filterManualIngredients, this.filterAutomaticIngredients,
        this.filterIngredientGroups, this.onlyGroupChildren, null, null, null)
        .then(ingredients => {
          update(() => {
            this.fetchedOptions = this.afterFetchFilterFunction(ingredients)
          })
        }, () => abort)
    },
    abortFilterIngredients () {
    }
  }
}
</script>
