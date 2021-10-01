<template>
  <div
    class="q-gutter-y-sm"
  >
    <q-input
      label="Name"
      outlined
      :disable="disable"
      v-model="value.name"
      filled
      @input="() => {$v.value.name.$touch(); $emit('input', value)}"
      :rules="[
                val => $v.value.name.required || 'Required',
                val => $v.value.name.maxLength || 'Max 30'
              ]"
    />
    <q-input
      label="Alcohol content"
      outlined
      :disable="disable"
      v-model="value.alcoholContent"
      filled
      type="number"
      @input="() => {$v.value.alcoholContent.$touch(); $emit('input', value)}"
      :rules="[
                val => $v.value.alcoholContent.required || 'Required',
                val => $v.value.alcoholContent.minValue || 'Must be positive',
                val => $v.value.alcoholContent.maxValue || 'Max 100'
              ]"
    />
    <q-splitter horizontal :value="10" />
    <q-tabs
      class="text-teal"
      inline-label
      v-model="value.type"
      @update:model-value="onTypeChange($event)"
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
      v-model="value.type"
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
          v-model="currentIngredientMultiplierString"
          filled
          mask="#.##"
          @input="() => {$v.value.pumpTimeMultiplier.$touch(); $emit('input', value)}"
          :rules="[
                val => $v.value.pumpTimeMultiplier.required || 'Required',
                val => $v.value.pumpTimeMultiplier.minValue || 'Must be positive',
                val => $v.value.pumpTimeMultiplier.maxValue || 'Max 10'
              ]"
        />
      </q-tab-panel>
      <q-tab-panel
        name="manual"
      >
        <q-select
          filled
          clearable
          v-model="value.unit"
          @input="onUnitChange"
          :options="units"
          emit-value
          map-options
          label="Unit"
          :disable="disable"
        />
        <q-checkbox
          v-if="value.unit === 'ml'"
          label="Add to volume"
          v-model="value.addToVolume"
          @input="$emit('input', value)"
          :disable="disable"
        />
        <q-checkbox
          v-if="!!value.unit && value.unit !== 'ml'"
          label="Scale"
          v-model="value.scaleToVolume"
          @input="$emit('input', value)"
          :disable="disable"
        />
      </q-tab-panel>
    </q-tab-panels>
  </div>
</template>

<script>
import {mdiCogs, mdiHandRight, mdiInformation} from "@quasar/extras/mdi-v5";
import {maxLength, maxValue, minValue, required, requiredIf} from "vuelidate/lib/validators";

export default {
  name: "IngredientForm",
  props: {
    value: {
      type: Object,
      required: true
    },
    disable: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      units: [
        {label: 'gram (g)', value: 'g'},
        {label: 'milliliter (ml)', value: 'ml'},
        {label: 'unit(s)', value: 'unit(s)'},
        {label: 'teaspoon(s)', value: 'teaspoon(s)'},
        {label: 'tablespoon(s)', value: 'tablespoon(s)'}
      ],
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
    },
    onTypeChange(newType) {
      this.$v.value.$touch()
    },
    onUnitChange(newUnit) {
      if (newUnit === 'ml') {
        this.value.addToVolume = false;
      } else {
        this.value.addToVolume = null;
      }
      if (newUnit !== 'ml') {
        this.value.scaleToVolume = false;
      } else {
        this.value.scaleToVolume = null;
      }
      this.$emit('input', this.value)
    }
  },
  created() {
    this.initialize();
    this.mdiInformation = mdiInformation;
    this.mdiCogs = mdiCogs;
    this.mdiHandRight = mdiHandRight;
  },
  validations() {
    let validations = {
      value: {
        name: {
          required,
          maxLength: maxLength(30)
        },
        alcoholContent: {
          required,
          minValue: minValue(0),
          maxValue: maxValue(100)
        },
        pumpTimeMultiplier: {
          required: requiredIf(() => this.value.type === "automated"),
          minValue: minValue(0),
          maxValue: maxValue(10)
        },
        unit: {
          required: requiredIf(() => this.value.type === "manual")
        }
      }
    };
    return validations;
  },
  computed: {
    currentIngredientMultiplierString: {
      get: function() {
        const multiplier = this.value.pumpTimeMultiplier;
        if(multiplier === undefined
          || multiplier === null) {
          return null;
        }
        let stringVal = "" +  multiplier;
        if(stringVal.match("^\\d+$")) {
          stringVal += ".0"
        }
        return stringVal;
      },
      set: function(value) {
        if(!value) {
          this.value.pumpTimeMultiplier = null;
        } else {
          this.value.pumpTimeMultiplier = parseFloat(value);
        }
      }
    }
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
