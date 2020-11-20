<template>
  <q-form
    greedy
    @submit.prevent="$emit('submit')"
  >
    <c-ingredient-selector
      label="Current ingredient"
      v-model="value.currentIngredient"
      clearable
    />
    <q-input
      label="Pump time per cl"
      v-model="value.timePerClInMs"
      type="number"
      suffix="ms"
      outlined
      @input="$v.value.timePerClInMs.$touch()"
      :rules="[val => $v.value.timePerClInMs.required || 'Required',
              val => $v.value.timePerClInMs.minValue || 'Min 1']"
    />
    <q-input
      label="Pump time per cl (Syrup)"
      v-model="value.syrupTimePerClInMs"
      type="number"
      suffix="ms"
      outlined
      @input="$v.value.syrupTimePerClInMs.$touch()"
      :rules="[val => $v.value.syrupTimePerClInMs.required || 'Required',
              val => $v.value.syrupTimePerClInMs.minValue || 'Min 1']"
    />
    <q-input
      label="Tube capacity"
      v-model="value.tubeCapacityInMl"
      type="number"
      suffix="ml"
      outlined
      @input="$v.value.tubeCapacityInMl.$touch()"
      :rules="[val => $v.value.tubeCapacityInMl.required || 'Required',
              val => $v.value.tubeCapacityInMl.minValue || 'Min 1']"
    />
    <q-input
      label="WiringPi-Pin"
      v-model="value.gpioPin"
      type="number"
      outlined
      @input="$v.value.gpioPin.$touch()"
      :rules="[val => $v.value.gpioPin.required || 'Required',
              val => $v.value.gpioPin.minValue || 'Min 1',
              val => $v.value.gpioPin.maxValue || 'Max 30']"
    />
    <slot name="below"/>
  </q-form>
</template>

<script>

import CIngredientSelector from "./CIngredientSelector";
import {maxValue, minValue, required,} from "vuelidate/lib/validators";

export default {
    name: "PumpEditorForm",
    components: {CIngredientSelector},
    props: {
      value: {
        type: Object,
        required: true
      }
    },
    methods: {
      initialize() {
        this.$v.value.$touch();
        if(this.$v.value.$invalid) {
          this.$emit('invalid');
        } else {
          this.$emit('valid');
        }
      }
    },
    created() {
      this.initialize();
    },
    validations() {
      let validations = {
        value: {
          timePerClInMs: {
            required,
            minValue: minValue(1)
          },
          syrupTimePerClInMs: {
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
          }
        }
      };
      return validations;
    },
    watch: {
      '$v.value.$invalid': function _watch$vValue$invalid(value) {
        if (!value) {
          this.$emit('valid');
        } else {
          this.$emit('invalid');
        }
      },
      value() {
        this.initialize();
      }
    }
  }
</script>

<style scoped>

</style>
