<template>
  <div class="flex justify-center q-col-gutter-sm">
    <q-select
      v-if="showGlassSelector"
      :model-value="selectedGlass"
      @update:modelValue="onGlassSelect($event)"
      label="Glass"
      input-class="text-center text-weight-medium"
      style="width: 400px"
      rounded
      outlined
      bg-color="grey-2"
      :loading="loading"
      :options="availableGlasses"
      :option-label="x => x.name + ' (' + x.size + 'ml)'"
      :error="!selectedGlass"
      error-message="Field required!"
      hide-bottom-space
    />
    <q-input
      v-else
      :model-value="modelValue"
      @update:modelValue="emitAmountToProduce(Number($event))"
      label="Amount to produce"
      type="number"
      outlined
      hide-bottom-space
      bg-color="grey-2"
      input-class="text-center text-weight-medium"
      style="width: 400px"
      debounce="500"
      rounded
      suffix="ml"
      :rules="[
            val => modelValue || 'Required',
            val => modelValue >= 10 || 'Min 10ml',
            val => modelValue <= 5000 || 'Max 5000ml'
          ]"
    >
      <template v-slot:prepend >
        <q-btn
          :disable="modelValue < 60"
          class="q-mx-xs"
          :icon="mdiMinusThick"
          dense round
          @click="emitAmountToProduce(modelValue - 50)"
        />
        <q-btn
          :disable="modelValue < 20"
          class="q-mx-xs"
          :icon="mdiMinus"
          round
          @click="emitAmountToProduce(modelValue - 10)"
        />
      </template>
      <template v-slot:append >
        <q-btn
          :disable="modelValue > 4990"
          :icon="mdiPlus"
          class="q-mx-xs"
          round
          @click="emitAmountToProduce(modelValue + 10)"
        />
        <q-btn
          :disable="modelValue > 4950"
          :icon="mdiPlusThick"
          class="q-mx-xs"
          dense round
          @click="emitAmountToProduce(modelValue + 50)"
        />
      </template>
    </q-input>
    <div
      v-if="availableGlasses.length !== 0"
    >
      <q-btn
        round
        outline
        size="19px"
        text-color="grey-7"
        @click="toggleShowGlassSelect"
      >
        <q-icon
          :name="mdiSwapHorizontalBold"
        />
      </q-btn>
    </div>
  </div>
</template>

<script>

import GlassService from 'src/services/glass.service'
import { mdiMinus, mdiMinusThick, mdiPlus, mdiPlusThick, mdiSwapHorizontalBold } from '@quasar/extras/mdi-v5'
import { maxValue, minValue, required } from '@vuelidate/validators'

export default {
  name: 'CMakeCocktailDialogAmountToProduce',
  props: {
    modelValue: {
      required: true
    },
    recipeDefaultGlass: {
      type: Object,
      required: false
    }
  },
  emits: ['initialized', 'update:modelValue', 'valid'],
  data: () => {
    return {
      loading: false,
      defaultGlass: '',
      availableGlasses: [],
      manuallySelectedGlass: '',
      isGlassSelect: true,
      lastIsValidEmit: null
    }
  },
  created () {
    this.mdiMinusThick = mdiMinusThick
    this.mdiMinus = mdiMinus
    this.mdiPlus = mdiPlus
    this.mdiPlusThick = mdiPlusThick
    this.mdiSwapHorizontalBold = mdiSwapHorizontalBold
    this.loading = true
    GlassService.getAllGlasses()
      .then(x => {
        x.sort((a, b) => a.size - b.size)
        this.availableGlasses = x
        for (const glass of x) {
          if (glass.default && !this.recipeDefaultGlass) {
            this.onGlassSelect(glass)
            this.defaultGlass = glass
          }
        }
        this.$emit('initialized')
        if (this.availableGlasses.length === 0) {
          this.$emit('valid', true)
        }
      })
      .finally(() => {
        this.loading = false
      })
  },
  watch: {
    selectedGlass: {
      immediate: true,
      handler (val) {
        if (!val) {
          return
        }
        this.emitAmountToProduce(val.size)
      }
    },
    modelValue: {
      immediate: true,
      handler (val) {
        this.checkValid()
      }
    }
  },
  methods: {
    checkValid () {
      if (!this.isGlassSelect) {
        if (!this.lastIsValidEmit) {
          this.$emit('valid', true)
          this.lastIsValidEmit = true
        }
        return
      }
      if (this.lastIsValidEmit !== !!this.selectedGlass) {
        this.$emit('valid', !!this.selectedGlass)
        this.lastIsValidEmit = !!this.selectedGlass
      }
    },
    emitAmountToProduce (val) {
      if (val === this.modelValue) {
        return
      }
      this.$emit('update:modelValue', val)
    },
    onGlassSelect (glass) {
      this.manuallySelectedGlass = glass
    },
    toggleShowGlassSelect () {
      this.isGlassSelect = !this.isGlassSelect
      if (!this.isGlassSelect) {
        this.checkValid()
        return
      }
      if (this.selectedGlass) {
        this.emitAmountToProduce(this.selectedGlass.size)
      }
      this.checkValid()
    }
  },
  computed: {
    selectedGlass () {
      if (this.manuallySelectedGlass) {
        return this.manuallySelectedGlass
      }
      return this.recipeDefaultGlass
    },
    showGlassSelector () {
      return this.isGlassSelect && this.availableGlasses.length !== 0
    }
  }
}
</script>

<style scoped>

</style>
