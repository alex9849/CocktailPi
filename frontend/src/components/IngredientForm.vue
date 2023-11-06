<template>
  <div
    class="q-gutter-y-sm"
  >
    <q-input
      :label="$t('component.ingredient_form.name')"
      outlined
      :disable="disable"
      :model-value="modelValue.name"
      hide-bottom-space
      :rules="[
                val => !v.modelValue.name.required.$invalid || $t('errors.field_required'),
                val => !v.modelValue.name.maxLength.$invalid || $t('errors.max_letters', {nr: 30}),
              ]"
      filled
      @update:model-value="e => setValue('name', e)"
    />
    <q-input
      :label="$t('component.ingredient_form.alc_content')"
      outlined
      :disable="disable"
      filled
      hide-bottom-space
      type="number"
      :model-value="modelValue.alcoholContent"
      :rules="[
                val => !v.modelValue.alcoholContent.required.$invalid || $t('errors.field_required'),
                val => !v.modelValue.alcoholContent.minValue.$invalid || $t('errors.positive'),
                val => !v.modelValue.alcoholContent.maxValue.$invalid || $t('errors.max_letters', {nr: 100})
              ]"
      @update:model-value="e => setValue('alcoholContent', e)"
    />
    <q-separator/>
    <q-tabs
      class="text-teal"
      inline-label
      :model-value="modelValue.type"
      @update:model-value="e => setValue('type', e)"
    >
      <q-tab
        :icon="mdiCogs"
        name="automated"
        :label="$t('component.ingredient_form.tab_automated')"
      />
      <q-tab
        :icon="mdiHandRight"
        name="manual"
        :label="$t('component.ingredient_form.tab_manual')"
      />
    </q-tabs>
    <q-tab-panels
      v-model:model-value="modelValue.type"
      animated
    >
      <q-tab-panel
        name="automated"
        class="q-gutter-y-sm q-pa-none"
      >
        <c-ingredient-selector
          :selected="parentGroup"
          hide-bottom-space
          clearable
          filled
          filter-automatic-ingredients
          filter-manual-ingredients
          :label="$t('component.ingredient_form.parent_group')"
          @update:selected="e => setParentGroup(e)"
        />
        <q-input
          :label="$t('component.ingredient_form.bottle_size')"
          outlined
          hide-bottom-space
          :disable="disable"
          filled
          type="number"
          :model-value="modelValue.bottleSize"
          :rules="[
                val => !v.modelValue.bottleSize.required.$invalid || $t('errors.field_required'),
                val => !v.modelValue.bottleSize.minValue.$invalid || $t('errors.positive'),
              ]"
          @update:model-value="e => setValue('bottleSize', e)"
        />
        <q-input
          :label="$t('component.ingredient_form.pump_time_multiplier')"
          outlined
          hide-bottom-space
          :disable="disable"
          :model-value="currentIngredientMultiplierString"
          :rules="[
                val => !v.modelValue.pumpTimeMultiplier.required.$invalid || $t('errors.field_required'),
                val => !v.modelValue.pumpTimeMultiplier.minValue.$invalid || $t('errors.positive'),
                val => !v.modelValue.pumpTimeMultiplier.maxValue.$invalid || $t('errors.max_metric', {nr: 10, metric: ''})
              ]"
          filled
          mask="#.##"
          @update:model-value="e => setValue('pumpTimeMultiplier', e)"
        />
      </q-tab-panel>
      <q-tab-panel
        name="manual"
        class="q-gutter-y-sm q-pa-none"
      >
        <c-ingredient-selector
          :selected="parentGroup"
          clearable
          hide-bottom-space
          filled
          filter-automatic-ingredients
          filter-manual-ingredients
          :label="$t('component.ingredient_form.parent_group')"
          :disable="isDisableParentGroup"
          @update:selected="e => setParentGroup(e)"
        />
        <q-select
          filled
          clearable
          hide-bottom-space
          :model-value="modelValue.unit"
          @update:model-value="e => setValue('unit', e)"
          :options="units"
          emit-value
          map-options
          :label="$t('component.ingredient_form.unit')"
          :disable="disable"
        />
      </q-tab-panel>
    </q-tab-panels>
  </div>
</template>

<script>
import { mdiCogs, mdiHandRight, mdiInformation } from '@quasar/extras/mdi-v5'
import { maxLength, maxValue, minValue, required, requiredIf } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'
import CIngredientSelector from 'components/CIngredientSelector'

export default {
  name: 'IngredientForm',
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
  data () {
    return {
      units: [
        { label: this.$t('component.ingredient_form.units.gram'), value: 'g' },
        { label: this.$t('component.ingredient_form.units.milliliter'), value: 'ml' },
        { label: this.$t('component.ingredient_form.units.piece'), value: 'piece(s)' },
        { label: this.$t('component.ingredient_form.units.teaspoon'), value: 'teaspoon(s)' },
        { label: this.$t('component.ingredient_form.units.tablespoon'), value: 'tablespoon(s)' }
      ]
    }
  },
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
      v: useVuelidate(),
      mdiInformation: mdiInformation,
      mdiCogs: mdiCogs,
      mdiHandRight: mdiHandRight
    }
  },
  validations () {
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
        parentGroupId: {},
        parentGroupName: {},
        pumpTimeMultiplier: {
          required: requiredIf(() => this.modelValue.type === 'automated'),
          minValue: minValue(0),
          maxValue: maxValue(10)
        },
        bottleSize: {
          required: requiredIf(() => this.modelValue.type === 'automated'),
          minValue: minValue(0)
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
    },
    parentGroup () {
      if (!this.modelValue.parentGroupId) {
        return null
      }
      return {
        id: this.modelValue.parentGroupId,
        name: this.modelValue.parentGroupName
      }
    },
    isDisableParentGroup () {
      return !!this.modelValue.unit &&
        this.modelValue.unit !== 'ml' &&
        this.modelValue.type === 'manual'
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
