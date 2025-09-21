<template>
  <div
    class="q-gutter-y-sm"
  >
    <q-input
      outlined
      :disable="disabled"
      hide-bottom-space
      v-model:model-value="v.modelValue.name.$model"
      :error-message="v.modelValue.name.$errors[0]?.$message"
      :error="v.modelValue.name.$errors.length > 0"
      :label="$t('component.glass_form.name')"
    />
    <q-input
      outlined
      :disable="disabled"
      hide-bottom-space
      v-model:model-value.number="v.modelValue.size.$model"
      :error-message="v.modelValue.size.$errors[0]?.$message"
      :error="v.modelValue.size.$errors.length > 0"
      :label="$t('component.glass_form.size')"
      type="number"
    />
    <q-input
      outlined
      :disable="disabled"
      hide-bottom-space
      v-model:model-value.number="v.modelValue.emptyWeight.$model"
      :error-message="v.modelValue.emptyWeight.$errors[0]?.$message"
      :error="v.modelValue.emptyWeight.$errors.length > 0"
      :label="$t('component.glass_form.empty_weight')"
      type="number"
    >
      <template v-slot:append>
        <q-btn
          :label="$t('component.glass_form.load_cell_measure_btn_label')"
          outline
          no-caps
          size="md"
          :loading="loadingMeasure"
          :icon="mdiReload"
          @click="onClickMeasureLoadCell()"
        />
      </template>
    </q-input>
    <div class="row justify-evenly">
      <q-checkbox
        :label="$t('component.glass_form.default_checkbox')"
        :disable="disabled"
        v-model:model-value="v.modelValue.default.$model"
      />
      <q-checkbox
        :label="$t('component.glass_form.use_for_single_ingredients_checkbox')"
        :disable="disabled"
        v-model:model-value="v.modelValue.useForSingleIngredients.$model"
      />
    </div>
  </div>
</template>

<script>

import useVuelidate from '@vuelidate/core'
import { maxLength, maxValue, minValue, required } from '@vuelidate/validators'
import { mdiReload } from '@mdi/js'
import PumpSettingsService from 'src/services/pumpsettings.service'

export default {
  name: 'CGlassForm',
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
  emits: ['invalid'],
  data: () => {
    return {
      loadingMeasure: false
    }
  },
  methods: {
    onClickMeasureLoadCell () {
      this.loadingMeasure = true
      PumpSettingsService.readLoadCell()
        .then(measurement => {
          this.v.modelValue.emptyWeight.$model = measurement
        })
        .finally(() => {
          this.loadingMeasure = false
        })
    }
  },
  watch: {
    'v.modelValue.$invalid': {
      immediate: true,
      handler (newVal) {
        this.$emit('invalid', newVal)
      }
    }
  },
  setup () {
    return {
      mdiReload,
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
        size: {
          required,
          minValue: minValue(10),
          maxValue: maxValue(5000)
        },
        emptyWeight: {
          minValue: minValue(1)
        },
        default: {},
        useForSingleIngredients: {}
      }
    }
  }
}
</script>

<style scoped>

</style>
