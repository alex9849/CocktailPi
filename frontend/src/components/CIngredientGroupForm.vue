<template>
  <div
    class="q-gutter-y-sm"
  >
    <q-input
      :disable="disable"
      :model-value="modelValue.name"
      :rules="[
                val => !v.modelValue.name.required.$invalid || 'Required',
                val => !v.modelValue.name.maxLength.$invalid || 'Max 30'
              ]"
      filled
      label="Name"
      outlined
      @update:model-value="e => setValue('name', e)"
    />
    <c-ingredient-selector
      :selected="parentGroup"
      clearable
      filled
      filter-automatic-ingredients
      filter-manual-ingredients
      label="Parent group"
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
