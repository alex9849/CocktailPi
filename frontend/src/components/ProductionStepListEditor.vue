<template>
  <div>
    <q-tabs
      class="text-teal"
      inline-label
      :model-value="selectedTab"
      @update:model-value="onProductionStepTypeChange($event)"
    >
      <q-tab
        :icon="mdiCupWater"
        name="ingredient"
        label="Ingredient"
        v-if="selectedTab === 'ingredient' || newProductionStep"
      />
      <q-tab
        :icon="mdiPencilOutline"
        name="writtenInstruction"
        label="Instruction"
        v-if="selectedTab === 'writtenInstruction' || newProductionStep"
      />
    </q-tabs>
    <q-tab-panels
      :model-value="selectedTab"
    >
      <q-tab-panel
        name="ingredient"
        v-if="selectedTab === 'ingredient' || newProductionStep"
      >
        <ingredient-production-step-form
          v-if="selectedTab === 'ingredient'"
          :model-value="editStep"
          @update:model-value="$emit('update:modelValue', $event)"
          @valid="$emit('valid')"
          @invalid="$emit('invalid')"
        />
      </q-tab-panel>
      <q-tab-panel
        name="writtenInstruction"
        v-if="selectedTab === 'writtenInstruction' || newProductionStep"
      >
        <written-instruction-production-step-form
          v-if="selectedTab === 'writtenInstruction'"
          :model-value="editStep"
          @update:model-value="$emit('update:modelValue', $event)"
          @valid="$emit('valid')"
          @invalid="$emit('invalid')"
        />
      </q-tab-panel>
    </q-tab-panels>
  </div>
</template>

<script>

import IngredientProductionStepForm from './IngredientProductionStepForm'
import WrittenInstructionProductionStepForm from 'components/WrittenInstructionProductionStepForm'
import { mdiCupWater, mdiPencilOutline } from '@quasar/extras/mdi-v5'

export default {
  name: 'ProductionStepListEditor',
  components: { WrittenInstructionProductionStepForm, IngredientProductionStepForm },
  props: {
    modelValue: {
      type: Object
    },
    newProductionStep: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:modelValue', 'valid', 'invalid'],
  data () {
    return {
      selectedTab: 'ingredient',
      iFormValid: false,
      wiFormValid: false,
      editStep: {}
    }
  },
  created () {
    this.mdiPencilOutline = mdiPencilOutline
    this.mdiCupWater = mdiCupWater
  },
  watch: {
    modelValue: {
      immediate: true,
      handler (newValue) {
        if (newValue) {
          this.selectedTab = this.determineProductionStepType(newValue)
          this.editStep = Object.assign({}, newValue)
          return
        }
        this.selectedTab = 'ingredient'
        this.editStep = {
          ingredient: null,
          amount: '',
          scale: true,
          boostable: false
        }
      }
    }
  },
  methods: {
    determineProductionStepType (prodStep) {
      if (prodStep.type === 'writtenInstruction') {
        return 'writtenInstruction'
      }
      return 'ingredient'
    },
    onProductionStepTypeChange (newType) {
      this.selectedTab = newType
      if (newType === 'ingredient') {
        this.editStep = Object.assign({}, {
          ingredient: null,
          amount: '',
          scale: true,
          boostable: false
        })
      } else {
        this.editStep = Object.assign({}, {
          message: '',
          type: 'writtenInstruction'
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
