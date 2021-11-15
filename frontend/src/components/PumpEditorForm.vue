<template>
  <div>
    <c-ingredient-selector
      label="Current ingredient"
      :selected="modelValue.currentIngredient"
      @update:selected="setValue('currentIngredient', $event)"
      clearable
      filter-manual-ingredients
    />
    <q-input
      label="Current filling level"
      type="number"
      outlined
      :model-value="v.modelValue.fillingLevelInMl.$model"
      :rules="[val => !v.modelValue.fillingLevelInMl.required.$invalid || 'Required',
              val => !v.modelValue.fillingLevelInMl.minValue.$invalid || 'Min 0']"
      @update:model-value="setValue('fillingLevelInMl', $event)"
    >
      <template v-slot:append>
        ml
        <q-icon
          :name="mdiInformation"
          @click="showHelpTubeFillingLevel = !showHelpTubeFillingLevel"
        >
          <q-dialog v-model="showHelpTubeFillingLevel">
            <q-card>
              <q-card-section>
                The amount of remaining liquid in the attached container.
              </q-card-section>
            </q-card>
          </q-dialog>
        </q-icon>
      </template>
    </q-input>
    <q-input
      label="Pump time per cl"
      type="number"
      outlined
      :model-value="v.modelValue.timePerClInMs.$model"
      :rules="[val => !v.modelValue.timePerClInMs.required.$invalid || 'Required',
              val => !v.modelValue.timePerClInMs.minValue.$invalid || 'Min 1']"
      @update:model-value="setValue('timePerClInMs', $event)"
    >
      <template v-slot:append>
        ms/cl
        <q-icon
          :name="mdiInformation"
          @click="showHelpPumpTime = !showHelpPumpTime"
        >
          <q-dialog v-model="showHelpPumpTime">
            <q-card>
              <q-card-section>
                How long does one pump need to deliver one cl?
                Warning! This number often varies even is all pumps are the exact same model!
              </q-card-section>
            </q-card>
          </q-dialog>
        </q-icon>
      </template>
    </q-input>
    <q-input
      label="Tube capacity"
      type="number"
      outlined
      :model-value="v.modelValue.tubeCapacityInMl.$model"
      :rules="[val => !v.modelValue.tubeCapacityInMl.required.$invalid || 'Required',
              val => !v.modelValue.tubeCapacityInMl.minValue.$invalid || 'Min 1']"
      @update:model-value="setValue('tubeCapacityInMl', $event)"
    >
      <template v-slot:append>
        ml
        <q-icon
          :name="mdiInformation"
          @click="showHelpTubeCapacity = !showHelpTubeCapacity"
        >
          <q-dialog v-model="showHelpTubeCapacity">
            <q-card>
              <q-card-section>
                The number of ml the pump should try to deliver on pump up.
              </q-card-section>
            </q-card>
          </q-dialog>
        </q-icon>
      </template>
    </q-input>
    <q-input
      label="WiringPi-Pin"
      type="number"
      outlined
      :model-value="v.modelValue.gpioPin.$model"
      :rules="[val => !v.modelValue.gpioPin.required.$invalid || 'Required',
              val => !v.modelValue.gpioPin.minValue.$invalid || 'Min 1',
              val => !v.modelValue.gpioPin.maxValue.$invalid || 'Max 30']"
      @update:model-value="setValue('gpioPin', $event)"
    >
      <template v-slot:append>
        <q-icon
          :name="mdiInformation"
          @click="showHelpWiringPi = !showHelpWiringPi"
        >
          <q-dialog v-model="showHelpWiringPi">
            <q-card>
              <q-card-section>
                The WiringPi-Pin is the GPIO-Pin according to the Wiring-Pi library.
                In most cases these are NOT the same.
              </q-card-section>
            </q-card>
          </q-dialog>
        </q-icon>
      </template>
    </q-input>
    <slot name="below"/>
  </div>
</template>

<script>

import CIngredientSelector from './CIngredientSelector'
import {maxValue, minValue, required} from '@vuelidate/validators'
import {mdiInformation} from '@quasar/extras/mdi-v5'
import useVuelidate from '@vuelidate/core'

export default {
  name: 'PumpEditorForm',
  components: { CIngredientSelector },
  props: {
    modelValue: {
      type: Object,
      required: true
    }
  },
  emits: ['update:modelValue', 'invalid', 'valid'],
  data: () => {
    return {
      showHelpTubeFillingLevel: false,
      showHelpPumpTime: false,
      showHelpWiringPi: false,
      showHelpTubeCapacity: false
    }
  },
  methods: {
    setValue(attribute, value) {
      this.v.modelValue[attribute].$model = value;
      this.$emit('update:modelValue', this.modelValue)
    }
  },
  setup () {
    return {
      v: useVuelidate(),
      mdiInformation: mdiInformation
    }
  },
  validations () {
    return {
      modelValue: {
        currentIngredient: {},
        timePerClInMs: {
          required,
          minValue: minValue(1)
        },
        tubeCapacityInMl: {
          required,
          minValue: minValue(1)
        },
        gpioPin: {
          required,
          minValue: minValue(1),
          maxValue: maxValue(30)
        },
        fillingLevelInMl: {
          required,
          minValue: minValue(0)
        }
      }
    }
  },
  watch: {
    'v.modelValue.$invalid': {
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
