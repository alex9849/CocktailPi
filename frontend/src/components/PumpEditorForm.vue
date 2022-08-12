<template>
  <div class="q-gutter-y-sm">
    <c-ingredient-selector
      label="Current ingredient"
      :selected="modelValue.currentIngredient"
      @update:selected="setValue('currentIngredient', $event)"
      clearable
      filter-manual-ingredients
      filter-ingredient-groups
    />
    <q-input
      label="Current filling level"
      type="number"
      outlined
      hide-bottom-space
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
        />
      </template>
    </q-input>
    <q-input
      label="Pump time per cl"
      type="number"
      outlined
      hide-bottom-space
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
        />
      </template>
    </q-input>
    <q-input
      label="Tube capacity"
      type="number"
      outlined
      hide-bottom-space
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
        />
      </template>
    </q-input>
    <q-input
      :model-value="v.modelValue.bcmPin.$model"
      type="number"
      outlined
      hide-bottom-space
      :rules="[val => !v.modelValue.bcmPin.required.$invalid || 'Required',
              val => !v.modelValue.bcmPin.minValue.$invalid || 'Min 0',
              val => !v.modelValue.bcmPin.maxValue.$invalid || 'Max 30']"
      label="BCM-Pin"
      @update:model-value="setValue('bcmPin', $event)"
    >
      <template v-slot:append>
        <q-icon
          :name="mdiInformation"
          @click="showHelpWiringPi = !showHelpWiringPi"
        />
      </template>
    </q-input>
    <q-select
      :model-value="v.modelValue.powerStateHigh.$model"
      :options="[{label: 'High', value: true}, {label:'Low', value: false}]"
      map-options
      emit-value
      outlined
      hide-bottom-space
      label="Power State"
      @update:model-value="setValue('powerStateHigh', $event)"
    />
    <q-checkbox
      :model-value="v.modelValue.pumpedUp.$model"
      outlined
      hide-bottom-space
      label="Pumped Up"
      @update:model-value="setValue('pumpedUp', $event)"
    />
    <q-dialog v-model:model-value="showHelpTubeFillingLevel">
      <q-card>
        <q-card-section>
          The amount of remaining liquid in the attached container.
        </q-card-section>
      </q-card>
    </q-dialog>
    <q-dialog v-model:model-value="showHelpPumpTime">
      <q-card>
        <q-card-section>
          How long does one pump need to deliver one cl?
          Warning! This number often varies even is all pumps are the exact same model!
        </q-card-section>
      </q-card>
    </q-dialog>
    <q-dialog v-model:model-value="showHelpTubeCapacity">
      <q-card>
        <q-card-section>
          The number of ml the pump should try to deliver on pump up.
        </q-card-section>
      </q-card>
    </q-dialog>
    <q-dialog v-model:model-value="showHelpWiringPi">
      <q-card>
        <q-card-section>
          The number that is used to specify the GPIO to be used.
          BCM refers to the “Broadcom SOC channel” number, which is the numbering inside the chip which is used on the Raspberry Pi.
          These numbers changed between board versions as you can see in the previous tables for the
          26-pin header type 1 versus 2, and or not sequential. These links may help:
          <a href="https://pi4j.com/getting-started/understanding-the-pins/#overview" target="_blank">Pi4J - Understanding the pins</a>
        </q-card-section>
      </q-card>
    </q-dialog>
    <slot name="below"/>
  </div>
</template>

<script>

import CIngredientSelector from './CIngredientSelector'
import { maxValue, minValue, required } from '@vuelidate/validators'
import { mdiInformation } from '@quasar/extras/mdi-v5'
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
    setValue (attribute, value) {
      this.v.modelValue[attribute].$model = value
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
        bcmPin: {
          required,
          minValue: minValue(0),
          maxValue: maxValue(30)
        },
        fillingLevelInMl: {
          required,
          minValue: minValue(0)
        },
        powerStateHigh: {
          required
        },
        pumpedUp: {
          required
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
