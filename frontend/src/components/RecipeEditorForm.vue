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
        v-model="value.recipe.name"
        @input="() => {$emit('input', value); $v.value.recipe.name.$touch();}"
        label="Recipe-Name"
        :rules="[
        val => $v.value.recipe.name.required || 'Required',
        val => $v.value.recipe.name.minLength || 'Minimal length 3',
        val => $v.value.recipe.name.maxLength || 'Maximal length 20']"
      />
      <q-file
        filled
        bottom-slots
        v-model="value.image"
        @input="$emit('input', value)"
        label="Image"
        counter
        accept="image/*"
        clearable
      >
        <template v-slot:prepend>
          <q-icon
            name="cloud_upload"
            @click.stop
          />
        </template>
      </q-file>
      <q-input
        outlined
        :loading="loading"
        :disable="loading || disabled"
        v-model="value.recipe.shortDescription"
        @input="() => {$emit('input', value); $v.value.recipe.shortDescription.$touch();}"
        type="textarea"
        input-style="height: 40px"
        label="Short description"
        counter
        maxlength="100"
        :rules="[
          val => $v.value.recipe.shortDescription.required || 'Required',
          val => $v.value.recipe.shortDescription.maxLength || 'Maximal length 100']"
      />
      <q-input
        outlined
        :loading="loading"
        :disable="loading || disabled"
        v-model="value.recipe.description"
        @input="() => {$emit('input', value); $v.value.recipe.description.$touch();}"
        type="textarea"
        label="Description"
        counter
        maxlength="2000"
        :rules="[
          val => $v.value.recipe.description.required || 'Required',
          val => $v.value.recipe.description.maxLength || 'Maximal length 2000']"
      />
      <IngredientList
        v-model="value.recipe.recipeIngredients"
        :editable="true"
      />
      <q-checkbox
        v-model="value.recipe.inPublic"
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
    data() {
      return {
        image: null
      }
    },
    validations() {
      let validations = {
        value: {
          recipe: {
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
        }
      };
      return validations;
    },
    watch: {
      '$v.value.recipe.$invalid': function _watch$vValueRecipe$invalid(value) {
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
