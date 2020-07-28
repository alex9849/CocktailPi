<template>
  <div>
    <q-form
      class="q-gutter-y-md"
      greedy
      @submit.prevent="$emit('submit')"
    >
      <q-input
        outlined
        :loading="loading"
        :disable="loading || disabled"
        v-model="value.name"
        @input="() => {$emit('input', value); $v.value.name.$touch();}"
        label="Recipe-Name"
        :rules="[
        val => $v.value.name.required || 'Required',
        val => $v.value.name.minLength || 'Minimal length 3',
        val => $v.value.name.maxLength || 'Maximal length 20']"
      />
      <q-input
        outlined
        :loading="loading"
        :disable="loading || disabled"
        v-model="value.shortDescription"
        @input="() => {$emit('input', value); $v.value.shortDescription.$touch();}"
        type="textarea"
        input-style="height: 40px"
        label="Short description"
        counter
        maxlength="100"
        :rules="[
          val => $v.value.shortDescription.required || 'Required',
          val => $v.value.shortDescription.maxLength || 'Maximal length 100']"
      />
      <q-input
        outlined
        :loading="loading"
        :disable="loading || disabled"
        v-model="value.description"
        @input="() => {$emit('input', value); $v.value.description.$touch();}"
        type="textarea"
        label="Description"
        counter
        maxlength="2000"
        :rules="[
          val => $v.value.description.required || 'Required',
          val => $v.value.description.maxLength || 'Maximal length 2000']"
      />
      <IngredientList
        v-model="value.recipeIngredients"
        :editable="true"
      />
      <q-checkbox
        v-model="value.inPublic"
        :disable="loading || disabled"
        label="Public recipe"
        @input="$emit('input', value)"
      />
      <slot name="below"/>
    </q-form>
  </div>
</template>

<script>
  import {maxLength, minLength, required} from "vuelidate/lib/validators";
  import IngredientList from "./IngredientList";

  export default {
    name: "RecipeEditorForm",
    components: {IngredientList},
    props: {
      value: {
        type: Object,
        required: true
      },
      loading: {
        type: Boolean,
        default: false
      },
      disabled: {
        type: Boolean,
        default: false
      }
    },
    validations() {
      let validations = {
        value: {
          name: {
            required,
            minLength: minLength(3),
            maxLength: maxLength(20)
          },
          shortDescription: {
            required,
            maxLength: maxLength(100)
          },
          description: {
            required,
            maxLength: maxLength(2000)
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
      }
    },
    computed: {}
  }
</script>

<style scoped>

</style>
