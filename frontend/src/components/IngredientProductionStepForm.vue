<template>
  <div>
    <c-ingredient-selector
      v-model="value.ingredient"
      @input="() => {$emit('input', value); $v.value.ingredient.$touch();}"
      :rules="[val => $v.value.ingredient.required || 'Required']"
    />
    <q-input
      :label="amountLabel"
      type="number"
      outlined
      v-model.number="value.amount"
      @input="() => {$emit('input', value); $v.value.amount.$touch();}"
      :rules="[
        val => $v.value.amount.required || 'Required',
        val => $v.value.amount.minValue || 'Min 1ml'
      ]"
    />
    <q-checkbox
      label="Scale with volume"
      v-model="value.scale"
      @input="$emit('input', value)"
    />
    <slot name="below"/>
  </div>
</template>

<script>
import IngridientService from '../services/ingredient.service'
import { minValue, required } from '@vuelidate/validators'
import CIngredientSelector from './CIngredientSelector'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'IngredientProductionStepForm',
  components: { CIngredientSelector },
  props: {
    value: {
      type: Object,
      required: true
    }
  },
  emits: ['input', 'valid', 'invalid'],
  data () {
    return {
      ingredientOptions: []
    }
  },
  setup () {
    return { v$: useVuelidate() }
  },
  methods: {
    filterIngredients (val, update, abort) {
      if (val.length < 1) {
        abort()
        return
      }
      IngridientService.getIngredientsFilter(val)
        .then(ingredients => {
          update(() => {
            this.ingredientOptions = ingredients
          })
        }, () => abort)
    },
    abortFilterIngredients () {
    }
  },
  validations () {
    const validations = {
      value: {
        ingredient: {
          required
        },
        amount: {
          required,
          minValue: minValue(1)
        }
      }
    }
    return validations
  },
  computed: {
    amountLabel () {
      if (this.value.ingredient) {
        return 'Amount (in ' + this.value.ingredient.unit + ')'
      }
      return 'Amount'
    }
  },
  watch: {
    '$v.value.$invalid': function _watch$vValue$invalid (value) {
      if (!value) {
        this.$emit('valid')
      } else {
        this.$emit('invalid')
      }
    },
    value: {
      immediate: true,
      handler () {
        this.$v.value.$touch()
        if (this.$v.value.$invalid) {
          this.$emit('invalid')
        } else {
          this.$emit('valid')
        }
      }
    }
  }
}
</script>

<style scoped>
</style>
