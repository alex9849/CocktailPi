<template>
  <div>
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
    >
      <template slot="append">
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
      v-model="value.tubeCapacityInMl"
      type="number"
      suffix="ml"
      outlined
      @input="$v.value.tubeCapacityInMl.$touch()"
      :rules="[val => $v.value.tubeCapacityInMl.required || 'Required',
              val => $v.value.tubeCapacityInMl.minValue || 'Min 1']"
    >
      <template slot="append">
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
      v-model="value.gpioPin"
      type="number"
      outlined
      @input="$v.value.gpioPin.$touch()"
      :rules="[val => $v.value.gpioPin.required || 'Required',
              val => $v.value.gpioPin.minValue || 'Min 1',
              val => $v.value.gpioPin.maxValue || 'Max 30']"
    >
      <template slot="append">
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

import CIngredientSelector from "./CIngredientSelector";
import {maxValue, minValue, required,} from "vuelidate/lib/validators";
import {mdiInformation} from "@quasar/extras/mdi-v5";

export default {
    name: "PumpEditorForm",
    components: {CIngredientSelector},
    props: {
      value: {
        type: Object,
        required: true
      }
    },
    data: () => {
      return {
        showHelpPumpTime: false,
        showHelpWiringPi: false,
        showHelpTubeCapacity: false
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
      this.mdiInformation = mdiInformation;
    },
    validations() {
      let validations = {
        value: {
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
