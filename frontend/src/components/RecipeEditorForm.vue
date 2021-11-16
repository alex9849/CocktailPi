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
        :model-value="modelValue.recipe.name"
        :rules="[
        val => !v.modelValue.recipe.name.required.$invalid || 'Required',
        val => !v.modelValue.recipe.name.minLength.$invalid || 'Minimal length 3',
        val => !v.modelValue.recipe.name.maxLength.$invalid || 'Maximal length 20']"
        label="Recipe-Name"
        @update:model-value="v.modelValue.recipe.name.$model = $event; $emit('update:modelValue', modelValue)"
      />
      <q-select
        :disable="disabled"
        label="Categories"
        :model-value="modelValue.recipe.categories"
        @update:model-value="v.modelValue.recipe.categories.$model = $event; $emit('update:modelValue', modelValue)"
        :options="categories"
        option-label="name"
        multiple
        outlined
      />
      <div :class="{'rounded-borders q-card--bordered q-card--flat no-shadow q-pa-xs': modelValue.recipe.hasImage && !modelValue.image && !modelValue.removeImage}"
           class="row"
      >
        <div class="col">
          <q-toggle v-if="modelValue.recipe.hasImage && !modelValue.image"
                    :disable="disabled"
                    :model-value="modelValue.removeImage"
                    color="red"
                    label="remove existing image"
                    @update:model-value="v.modelValue.removeImage.$model = $event; $emit('update:modelValue', modelValue)"
          />
          <q-file v-if="!modelValue.removeImage"
                  :disable="disabled"
                  :model-value="modelValue.image"
                  accept="image/*"
                  bottom-slots
                  clearable
                  hide-bottom-space
                  label="Image"
                  max-file-size="20971520"
                  outlined
                  @update:model-value="v.modelValue.image.$model = $event; $emit('update:modelValue', modelValue)"
          >
            <template v-slot:prepend>
              <q-icon
                name="cloud_upload"
                @click.stop
              />
            </template>
          </q-file>
        </div>
      </div>
      <q-input
        outlined
        :disable="disabled"
        :model-value="modelValue.recipe.description"
        :rules="[
          val => !v.modelValue.recipe.description.required.$invalid || 'Required',
          val => !v.modelValue.recipe.description.maxLength.$invalid || 'Maximal length 2000']"
        type="textarea"
        label="Description"
        counter
        maxlength="2000"
        @update:model-value="v.modelValue.recipe.description.$model = $event; $emit('update:modelValue', modelValue)"
      />
      <q-input
        label="Default amount to produce"
        :disable="disabled"
        type="number"
        outlined
        :model-value.number="modelValue.recipe.defaultAmountToFill"
        :rules="[
        val => !v.modelValue.recipe.defaultAmountToFill.required.$invalid || 'Required',
        val => !v.modelValue.recipe.defaultAmountToFill.minValue.$invalid || 'Min 50ml']"
        @update:model-value="v.modelValue.recipe.defaultAmountToFill.$model = $event; $emit('update:modelValue', modelValue)"
      >
        <template v-slot:append>
          ml
        </template>
      </q-input>
      <IngredientList
        :model-value="modelValue.recipe.productionSteps"
        @update:model-value="v.modelValue.recipe.productionSteps.$model = $event; $emit('update:modelValue', modelValue)"
        :editable="!disabled"
      />
      <q-checkbox
        :model-value="modelValue.recipe.inPublic"
        :disable="disabled"
        label="Public recipe"
        @update:model-value="v.modelValue.recipe.inPublic.$model = $event; $emit('update:modelValue', modelValue)"
      />
      <slot name="below"/>
    </q-form>
  </div>
</template>

<script>
import {maxLength, minLength, minValue, required} from '@vuelidate/validators'
import IngredientList from './IngredientList'
import {mapGetters} from 'vuex'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'RecipeEditorForm',
  components: { IngredientList },
  props: {
    modelValue: {
      type: Object,
      required: true
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  emits: ['submit', 'update:modelValue', 'valid', 'invalid'],
  data () {
    return {
      image: null
    }
  },
  setup () {
    return { v: useVuelidate() }
  },
  methods: {
    setValue(attribute, value) {
      this.v.modelValue[attribute].$model = value;
      this.$emit('update:modelValue', this.modelValue)
    }
  },
  validations () {
    return {
      modelValue: {
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
          },
          productionSteps: {
            required,
            minLength: minLength(1)
          },
          inPublic: {},
          categories: {}
        },
        image: {},
        removeImage: {}
      }
    }
  },
  watch: {
    'v.modelValue.recipe.$invalid': {
      handler (value) {
        if (!value) {
          this.$emit('valid')
        } else {
          this.$emit('invalid')
        }
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
