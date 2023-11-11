<template>
  <div>
    <div
      v-if="loading"
      class="flex justify-center q-gutter-sm"
    >
      <q-skeleton
        type="QInput"
        style="width: 400px; border-radius: 30px; border: 1px solid #aaa"
      />
      <q-skeleton
        type="circle"
        width="56px"
        height="56px"
        style="border-radius: 30px; border: 1px solid #aaa;"
      />
    </div>
    <div
      v-else
      class="flex justify-center q-gutter-sm"
    >
      <q-select
        :dark="color.backgroundDark"
        v-if="showGlassSelector"
        :model-value="selectedGlass"
        @update:modelValue="onGlassSelect($event)"
        :label="$t('component.make_cocktail_amount_to_produce.glass_selector_label')"
        input-class="text-center text-weight-medium"
        style="width: 400px"
        rounded
        outlined
        :options="availableGlasses"
        :option-label="x => x.name + ' (' + x.size + 'ml)'"
        :error="!selectedGlass"
        :error-message="$t('errors.field_required')"
        hide-bottom-space
      />
      <q-input
        v-else
        :dark="color.backgroundDark"
        :model-value="modelValue"
        @update:modelValue="emitAmountToProduce(Number($event))"
        :label="$t('component.make_cocktail_amount_to_produce.amount_to_produce_label')"
        type="number"
        outlined
        hide-bottom-space
        input-class="text-center text-weight-medium"
        style="width: 400px"
        debounce="500"
        rounded
        suffix="ml"
        :rules="[
            val => modelValue || $t('errors.field_required'),
            val => modelValue >= 10 || $t('errors.min_metric', {nr: 10, metric: 'ml'}),
            val => modelValue <= 5000 || $t('errors.max_metric', {nr: 5000, metric: 'ml'})
          ]"
      >
        <template v-slot:prepend>
          <q-btn
            :disable="modelValue < 60"
            class="q-mx-xs"
            :icon="mdiMinusThick"
            dense round
            :style="plusMinusColorStyle"
            text-color="background"
            @click="emitAmountToProduce(modelValue - 50)"
          />
          <q-btn
            :disable="modelValue < 20"
            class="q-mx-xs"
            :icon="mdiMinus"
            round
            :style="plusMinusColorStyle"
            text-color="background"
            @click="emitAmountToProduce(modelValue - 10)"
          />
        </template>
        <template v-slot:append>
          <q-btn
            :disable="modelValue > 4990"
            :icon="mdiPlus"
            class="q-mx-xs"
            round
            :style="plusMinusColorStyle"
            text-color="background"
            @click="emitAmountToProduce(modelValue + 10)"
          />
          <q-btn
            :disable="modelValue > 4950"
            :icon="mdiPlusThick"
            class="q-mx-xs"
            dense round
            :style="plusMinusColorStyle"
            text-color="background"
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
          @click="toggleShowGlassSelect"
        >
          <q-icon
            :name="mdiSwapHorizontalBold"
          />
        </q-btn>
      </div>
    </div>
  </div>
</template>

<script>

import GlassService from 'src/services/glass.service'
import { mdiMinus, mdiMinusThick, mdiPlus, mdiPlusThick, mdiSwapHorizontalBold } from '@quasar/extras/mdi-v5'
import { mapGetters } from 'vuex'
import { calcTextColor, complementColor } from 'src/mixins/utils'

export default {
  name: 'CMakeCocktailDialogAmountToProduce',
  props: {
    modelValue: {
      required: true
    },
    recipeDefaultGlass: {
      type: Object,
      required: false
    },
    defaultValueNoGlass: {
      type: Number,
      required: true
    }
  },
  emits: ['update:modelValue'],
  data: () => {
    return {
      loading: false,
      availableGlasses: [],
      selectedGlass: null,
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
        if (this.recipeDefaultGlass) {
          this.onGlassSelect(this.recipeDefaultGlass)
        } else if (this.availableGlasses.length !== 0) {
          for (const glass of this.availableGlasses) {
            if (glass.default) {
              this.onGlassSelect(glass)
            }
          }
        } else {
          this.isGlassSelect = false
          if (!this.modelValue) {
            this.emitAmountToProduce(this.defaultValueNoGlass)
          }
        }
      })
      .finally(() => {
        this.loading = false
      })
  },
  methods: {
    emitAmountToProduce (val) {
      if (val === this.modelValue) {
        return
      }
      this.$emit('update:modelValue', val)
    },
    onGlassSelect (glass) {
      this.selectedGlass = glass
      this.emitAmountToProduce(glass?.size)
    },
    toggleShowGlassSelect () {
      this.isGlassSelect = !this.isGlassSelect
      if (this.isGlassSelect) {
        this.onGlassSelect(this.selectedGlass)
      } else if (!this.modelValue) {
        this.emitAmountToProduce(this.defaultValueNoGlass)
      }
    }
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    }),
    plusMinusBtnColor () {
      const background = complementColor(this.color.background, 15)
      return {
        bg: background,
        text: calcTextColor(background)
      }
    },
    plusMinusColorStyle () {
      return {
        backgroundColor: this.plusMinusBtnColor.bg,
        color: this.plusMinusBtnColor.text
      }
    },
    showGlassSelector () {
      return this.isGlassSelect && this.availableGlasses.length !== 0
    }
  }
}
</script>

<style scoped>

</style>
