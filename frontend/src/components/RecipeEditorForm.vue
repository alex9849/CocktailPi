<template>
  <div>
    <q-form
      class="q-gutter-y-md"
      greedy
      @submit.prevent="$emit('submit')"
    >
      <q-input
        outlined
        :disable="disabled"
        v-model="value.recipe.name"
        @input="() => {$emit('input', value); $v.value.recipe.name.$touch();}"
        label="Recipe-Name"
        :rules="[
        val => $v.value.recipe.name.required || 'Required',
        val => $v.value.recipe.name.minLength || 'Minimal length 3',
        val => $v.value.recipe.name.maxLength || 'Maximal length 20']"
      />
      <q-select
        :disable="disabled"
        label="Categories"
        v-model="value.recipe.categories"
        :options="categories"
        option-label="name"
        multiple
        outlined
      />
      <div v-if="allowImageRemoving" style="border: 1px solid #c2c2c2; border-radius: 5px; padding: 3px">
        <q-toggle
          label="Remove image if existing"
          :disable="disabled"
          v-model="value.removeImage"
          @input="$emit('input', value)"
        />
        <q-file
          v-if="!value.removeImage"
          filled
          :disable="disabled"
          bottom-slots
          v-model="value.image"
          @input="$emit('input', value)"
          label="Image"
          max-file-size="20971520"
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
      </div>
      <q-file
        v-else
        filled
        bottom-slots
        v-model="value.image"
        :disable="disabled"
        @input="$emit('input', value)"
        label="Image"
        max-file-size="20971520"
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
        :disable="disabled"
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
      <q-input
        label="Default amount to produce"
        :disable="disabled"
        type="number"
        outlined
        v-model.number="value.recipe.defaultAmountToFill"
        @input="() => {$emit('input', value); $v.value.recipe.defaultAmountToFill.$touch();}"
        :rules="[
        val => $v.value.recipe.defaultAmountToFill.required || 'Required',
        val => $v.value.recipe.defaultAmountToFill.minValue || 'Min 50ml']"
      >
        <template slot="append">
          ml
        </template>
      </q-input>
      <IngredientList
        v-model="value.recipe.recipeIngredients"
        :editable="!disabled"
      />
      <q-checkbox
        v-model="value.recipe.inPublic"
        :disable="disabled"
        label="Public recipe"
        @input="$emit('input', value)"
      />
      <slot name="below"/>
    </q-form>
  </div>
</template>

<script>
import {maxLength, minLength, minValue, required} from "vuelidate/lib/validators";
import IngredientList from "./IngredientList";
import {mapGetters} from "vuex";

export default {
  name: "RecipeEditorForm",
  components: {IngredientList},
  props: {
    value: {
      type: Object,
      required: true
    },
    allowImageRemoving: {
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
          description: {
            required,
            maxLength: maxLength(2000)
          },
          defaultAmountToFill: {
            required,
            minValue: minValue(50)
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
  computed: {
    ...mapGetters({
      categories: 'category/getCategories'
    })
  }
}
</script>

<style scoped>

</style>
