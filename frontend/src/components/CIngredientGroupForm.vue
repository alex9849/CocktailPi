<template>
  <div
    class="q-gutter-y-sm"
  >
    <q-input
      :disable="disable"
      :model-value="modelValue.name"
      :rules="[
                val => !v.modelValue.name.required.$invalid || $t('errors.field_required'),
                val => !v.modelValue.name.maxLength.$invalid || $t('errors.max_letters', {nr: 30})
              ]"
      :label="$t('component.ingredientGroupForm.name')"
      outlined
      hide-bottom-space
      @update:model-value="e => setValue('name', e)"
    />
    <c-ingredient-selector
      :selected="parentGroup"
      clearable
      outlined
      hide-bottom-space
      filter-automatic-ingredients
      filter-manual-ingredients
      :label="$t('component.ingredientGroupForm.parent_group')"
      :after-fetch-filter-function="removeSelfFromResults"
      @update:selected="e => setParentGroup(e)"
    />
  </div>
</template>

<script>
import CIngredientSelector from 'components/CIngredientSelector'
import useVuelidate from '@vuelidate/core'
import { maxLength, required } from '@vuelidate/validators'

export default {
  name: 'CIngredientGroupForm',
  components: { CIngredientSelector },
  props: {
    modelValue: {
      type: Object,
      required: true
    },
    disable: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:modelValue', 'invalid', 'valid'],
  methods: {
    setValue (attribute, value) {
      this.v.modelValue[attribute].$model = value
      this.$emit('update:modelValue', this.modelValue)
    },
    setParentGroup (parentGroup) {
      this.v.modelValue.parentGroupId.$model = parentGroup?.id
      this.v.modelValue.parentGroupName.$model = parentGroup?.name
      this.$emit('update:modelValue', this.modelValue)
    },
    removeSelfFromResults (ingredients) {
      if (!this.modelValue.id) {
        return ingredients
      }
      return ingredients.filter(x => x.id !== this.modelValue.id)
    }
  },
  setup () {
    return {
      v: useVuelidate()
    }
  },
  validations () {
    return {
      modelValue: {
        name: {
          required,
          maxLength: maxLength(30)
        },
        parentGroupId: {},
        parentGroupName: {}
      }
    }
  },
  computed: {
    parentGroup () {
      if (!this.modelValue.parentGroupId) {
        return null
      }
      return {
        id: this.modelValue.parentGroupId,
        name: this.modelValue.parentGroupName
      }
    }
  },
  watch: {
    'v.modelValue.$invalid': {
      immediate: true,
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
