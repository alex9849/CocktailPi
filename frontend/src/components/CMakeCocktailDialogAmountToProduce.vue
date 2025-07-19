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
        :model-value="activeGlass"
        @update:modelValue="selectedGlass = $event"
        :label="$t('component.make_cocktail_amount_to_produce.glass_selector_label')"
        input-class="text-center text-weight-medium"
        style="width: 400px"
        rounded
        outlined
        :options="availableGlasses"
        :option-label="x => x.name + ' (' + x.size + 'ml)'"
        :error="!activeGlass"
        :error-message="$t('errors.field_required')"
        hide-bottom-space
      >
        <template v-slot:selected-item="scope">
          <span>
            {{ scope.opt.name }} ({{ scope.opt.size }}ml)
            <span
              class="text-positive text-italic"
              v-if="scope.opt.id === recipeDefaultGlass?.id"
            >
              {{ $t('component.make_cocktail_amount_to_produce.badge_recipe_default') }}
            </span>
            <span
              class="text-positive text-italic"
              v-else-if="scope.opt.default"
            >
              {{ $t('component.make_cocktail_amount_to_produce.badge_global_default') }}
            </span>
            <span
              class="text-positive text-italic"
              v-else-if="scope.opt.id === detectedGlass"
            >
              {{ $t('component.make_cocktail_amount_to_produce.badge_auto_detected') }}
            </span>
          </span>
        </template>
        <template v-slot:option="scope">
          <q-item v-bind="scope.itemProps">
            <q-item-section>
              <q-item-label>{{ scope.opt.name }} ({{ scope.opt.size }}ml)</q-item-label>
              <q-item-label
                caption
                class="text-positive"
                v-if="glassBadges(scope.opt).length !== 0"
              >
                <span
                  class="q-pr-xs"
                  v-for="badge in glassBadges(scope.opt)"
                  :key="badge"
                >
                  {{ badge }}
                </span>
              </q-item-label>
            </q-item-section>
          </q-item>
        </template>
      </q-select>
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
import WebsocketService from 'src/services/websocket.service'

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
      detectedGlass: '',
      detectedGlassValid: false,
      loading: false,
      availableGlasses: [],
      selectedGlass: null,
      isGlassSelect: true
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
    toggleShowGlassSelect () {
      this.isGlassSelect = !this.isGlassSelect
      if (this.isGlassSelect) {
        this.emitAmountToProduce(this.activeGlass?.size)
      } else if (!this.modelValue) {
        this.emitAmountToProduce(this.defaultValueNoGlass)
      }
    },
    glassBadges (glass) {
      const badges = []
      if (glass.id === this.recipeDefaultGlass?.id) {
        badges.push(this.$t('component.make_cocktail_amount_to_produce.badge_recipe_default'))
      } else if (glass.default) {
        badges.push(this.$t('component.make_cocktail_amount_to_produce.badge_global_default'))
      }
      if (glass.id === this.detectedGlass) {
        badges.push(this.$t('component.make_cocktail_amount_to_produce.badge_auto_detected'))
      }
      for (const idx in badges) {
        if (idx < badges.length - 1) {
          badges[idx] = badges[idx] + ','
        }
      }
      return badges
    }
  },
  mounted () {
    this.detectedGlassValid = false
    WebsocketService.subscribe(this, '/user/topic/dispensingarea', glass => {
      const state = JSON.parse(glass.body)
      if (!state.glass) {
        this.detectedGlass = null
        this.detectedGlassValid = true
        return
      }
      this.detectedGlass = state.glass.id
      this.detectedGlassValid = true
    }, true)
  },
  unmounted () {
    WebsocketService.unsubscribe(this, '/user/topic/dispensingarea')
  },
  watch: {
    activeGlass: {
      immediate: true,
      handler () {
        if (this.activeGlass && this.isGlassSelect) {
          this.emitAmountToProduce(this.activeGlass.size)
        }
      }
    },
    dataLoaded () {
      if (this.activeGlass) {
        return
      }
      this.isGlassSelect = false
      this.emitAmountToProduce(this.defaultValueNoGlass)
    }
  },
  computed: {
    ...mapGetters({
      color: 'appearance/getNormalColors'
    }),
    dataLoaded () {
      return !this.loading && this.detectedGlassValid
    },
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
    },
    activeGlass () {
      if (this.selectedGlass) {
        return this.selectedGlass
      }
      let found = null
      for (const glass of this.availableGlasses) {
        if (glass.id === this.detectedGlass) {
          found = glass
        }
      }
      if (found) {
        return found
      }
      if (this.recipeDefaultGlass) {
        return this.recipeDefaultGlass
      }
      for (const glass of this.availableGlasses) {
        if (glass.default) {
          return glass
        }
      }
      return null
    }
  }
}
</script>

<style scoped>

</style>
