<template>
  <q-form
    class="text-center innerpadding"
    @submit.prevent="$emit('submit')"
  >
    <q-select
      outlined
      v-model="value.ingredient"
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
      @input="() => {$emit('input', value); $v.value.ingredient.$touch();}"
      :rules="[val => $v.value.ingredient.required || 'Required']"
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
    <q-input
      label="Amount (in ml)"
      type="number"
      outlined
      v-model.number="value.amount"
      @input="() => {$emit('input', value); $v.value.amount.$touch();}"
      :rules="[
        val => $v.value.amount.required || 'Required',
        val => $v.value.amount.minValue || 'Min 1cl'
      ]"
    />
    <slot name="below"/>
  </q-form>
</template>

<script>
  import IngridientService from '../services/ingredient.service'
  import {minValue, required} from "vuelidate/lib/validators";

  export default {
    name: "IngredientForm",
    props: {
      value: {
        type: Object,
        required: true
      },
      headline: {
        type: String,
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
      },
      initialize() {
        this.$v.value.$touch();
        if(this.$v.value.$invalid) {
          this.$emit('invalid');
        } else {
          this.$emit('valid');
        }
      }
    },
    created() {
      this.initialize();
    },
    validations() {
      let validations = {
        value: {
          ingredient: {
            required
          },
          amount: {
            required,
            minValue: minValue(1)
          }
        }
      };
      return validations;
    },
    watch: {
      '$v.value.$invalid': function _watch$vValue$invalid(value) {
        if (!value) {
          this.$emit('valid');
        } else {
          this.$emit('invalid');
        }
      },
      value() {
        this.initialize();
      }
    }
  }
</script>

<style scoped>
</style>
