<template>
  <div
    class="q-gutter-y-sm"
  >
    <q-input
      label="Name"
      outlined
      :disable="disable"
      :model-value="modelValue.name"
      :rules="[
                val => !v.modelValue.name.required.$invalid || 'Required',
                val => !v.modelValue.name.maxLength.$invalid || 'Max 30'
              ]"
      filled
      @update:model-value="e => setValue('name', e)"
    />
    <q-input
      label="Alcohol content"
      outlined
      :disable="disable"
      filled
      type="number"
      :model-value="modelValue.alcoholContent"
      :rules="[
                val => !v.modelValue.alcoholContent.required.$invalid || 'Required',
                val => !v.modelValue.alcoholContent.minValue.$invalid || 'Must be positive',
                val => !v.modelValue.alcoholContent.maxValue.$invalid || 'Max 100'
              ]"
      @update:model-value="e => setValue('alcoholContent', e)"
    />
    <q-splitter :model-value="10" horizontal/>
    <q-tabs
      class="text-teal"
      inline-label
      :model-value="modelValue.type"
      @update:model-value="e => setValue('type', e)"
    >
      <q-tab
        :icon="mdiCogs"
        name="automated"
        label="automated"
      />
      <q-tab
        :icon="mdiHandRight"
        name="manual"
        label="manual"
      />
    </q-tabs>
    <q-tab-panels
      v-model:model-value="modelValue.type"
      animated
    >
      <q-tab-panel
        name="automated"
        style="padding: 0"
      >
        <q-input
          label="Pump time multiplier"
          outlined
          :disable="disable"
          :model-value="currentIngredientMultiplierString"
          :rules="[
                val => !v.modelValue.pumpTimeMultiplier.required.$invalid || 'Required',
                val => !v.modelValue.pumpTimeMultiplier.minValue.$invalid || 'Must be positive',
                val => !v.modelValue.pumpTimeMultiplier.maxValue.$invalid || 'Max 10'
              ]"
          filled
          mask="#.##"
          @update:model-value="e => setValue('pumpTimeMultiplier', e)"
        />
      </q-tab-panel>
      <q-tab-panel
        name="manual"
      >
        <q-select
          filled
          clearable
          :model-value="modelValue.unit"
          @update:model-value="e => setValue('unit', e)"
          :options="units"
          emit-value
          map-options
          label="Unit"
          :disable="disable"
        />
      </q-tab-panel>
    </q-tab-panels>
  </div>
</template>

<script>
import {mdiCogs, mdiHandRight, mdiInformation} from '@quasar/extras/mdi-v5'
import {maxLength, maxValue, minValue, required, requiredIf} from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'IngredientForm',
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
  data() {
    return {
      units: [
        {label: 'gram (g)', value: 'g'},
        {label: 'milliliter (ml)', value: 'ml'},
        {label: 'piece(s)', value: 'piece(s)'},
        {label: 'teaspoon(s)', value: 'teaspoon(s)'},
        {label: 'tablespoon(s)', value: 'tablespoon(s)'}
      ]
    }
  },
  methods: {
    setValue(attribute, value) {
      this.v.modelValue[attribute].$model = value;
      this.$emit('update:modelValue', this.modelValue)
    }
  },
  setup() {
    return {
      v: useVuelidate(),
      mdiInformation: mdiInformation,
      mdiCogs: mdiCogs,
      mdiHandRight: mdiHandRight
    }
  },
  validations() {
    return {
      modelValue: {
        name: {
          required,
          maxLength: maxLength(30)
        },
        alcoholContent: {
          required,
          minValue: minValue(0),
          maxValue: maxValue(100)
        },
        type: {},
        pumpTimeMultiplier: {
          required: requiredIf(() => this.modelValue.type === 'automated'),
          minValue: minValue(0),
          maxValue: maxValue(10)
        },
        unit: {
          required: requiredIf(() => this.modelValue.type === 'manual')
        }
      }
    }
  },
  computed: {
    currentIngredientMultiplierString: {
      get: function () {
        const multiplier = this.modelValue.pumpTimeMultiplier
        if (multiplier === undefined ||
          multiplier === null) {
          return null
        }
        let stringVal = '' + multiplier
        if (stringVal.match('^\\d+$')) {
          stringVal += '.0'
        }
        return stringVal
      },
      set: function (value) {
        if (!value) {
          this.modelValue.pumpTimeMultiplier = null
        } else {
          this.modelValue.pumpTimeMultiplier = parseFloat(value)
        }
      }
    }
  },
  watch: {
    'v.modelValue.$invalid': {
      immediate: true,
      handler(value) {
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
