<template>
  <div>
    <c-ingredient-selector
      :rules="[val => !v.modelValue.ingredient.required.$invalid || $t('errors.field_required')]"
      :selected="modelValue.ingredient"
      @update:selected="onIngredientSelect($event)"
    />
    <q-input
      :label="amountLabel"
      type="number"
      outlined
      :model-value="modelValue.amount"
      :rules="[
        val => !v.modelValue.amount.required.$invalid || $t('errors.field_required'),
        val => !v.modelValue.amount.minValue.$invalid || $t('errors.min_metric', {nr: '1', metric: printMetric})
      ]"
      @update:model-value="v.modelValue.amount.$model = Number.parseInt($event); $emit('update:modelValue', modelValue)"
    />
    <div class="row">
      <div class="col-6">
        <q-checkbox
          :label="$t('component.prod_step_editor_ingredient.scale_label')"
          :model-value="modelValue.scale"
          @update:model-value="v.modelValue.scale.$model = $event; $emit('update:modelValue', modelValue)"
        />
      </div>
      <div class="col-6">
        <q-checkbox
          class="col-6"
          :label="$t('component.prod_step_editor_ingredient.boostable_label')"
          :model-value="modelValue.boostable"
          @update:model-value="v.modelValue.boostable.$model = $event; $emit('update:modelValue', modelValue)"
        />
      </div>
    </div>
    <slot name="below"/>
  </div>
</template>

<script>
import { minValue, required } from '@vuelidate/validators'
import CIngredientSelector from './CIngredientSelector'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'IngredientProductionStepForm',
  components: { CIngredientSelector },
  props: {
    modelValue: {
      type: Object,
      required: true
    }
  },
  emits: ['update:modelValue', 'valid', 'invalid'],
  data () {
    return {
      ingredientOptions: []
    }
  },
  setup () {
    return { v: useVuelidate() }
  },
  validations () {
    return {
      modelValue: {
        ingredient: {
          required
        },
        amount: {
          required,
          minValue: minValue(1)
        },
        scale: {},
        boostable: {}
      }
    }
  },
  methods: {
    onIngredientSelect (ingredient) {
      this.v.modelValue.ingredient.$model = ingredient
      if (ingredient) {
        if (ingredient.type === 'group') {
          this.v.modelValue.boostable.$model = ingredient.maxAlcoholContent > 0
        } else {
          this.v.modelValue.boostable.$model = ingredient.alcoholContent > 0
        }
      }
      this.$emit('update:modelValue', this.modelValue)
    }
  },
  computed: {
    amountLabel () {
      if (this.modelValue.ingredient) {
        return this.$t('component.prod_step_editor_ingredient.amount_in',
          { metric: this.modelValue.ingredient.unit })
      }
      return this.$t('component.prod_step_editor_ingredient.amount')
    },
    printMetric () {
      if (this.modelValue.ingredient) {
        return this.modelValue.ingredient.unit
      }
      return ''
    }
  },
  watch: {
    'v.modelValue.$invalid': {
      handler (value) {
        if (!value) {
          this.$emit('valid')
        } else {
          this.$emit('invalid')
        }
      }
    }
  }
}
</script>

<style scoped>
</style>
