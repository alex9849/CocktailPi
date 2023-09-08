<template>
  <q-card class="bg-grey-2 text-center full-height" flat bordered>
    <q-card-section class="q-gutter-sm">
      <p class="text-subtitle2">{{ ingredientName }}</p>
      <q-input :model-value="amountCopy"
               @update:modelValue="updateAmount(Number($event) - amountCopy)"
               type="number"
               debounce="500"
               rounded
               outlined
               input-class="text-center text-weight-medium"
               suffix="ml"
      >
        <template v-slot:prepend >
          <q-btn
            :disable="amountCopy <= 0"
            :icon="mdiMinus"
            round
            @click="updateAmount(-10)"
          />
        </template>
        <template v-slot:append >
          <q-btn :icon="mdiPlus" round @click="updateAmount(10)" />
        </template>
      </q-input>
    </q-card-section>
  </q-card>
</template>

<script>

import { mdiPlus, mdiMinus } from '@quasar/extras/mdi-v5'
import { isNumber } from 'lodash'

export default {
  name: 'CIngredientAdditionalMlCard',
  props: {
    debounce: {
      type: Number,
      default: 0
    },
    ingredientName: {
      type: String,
      required: true
    },
    amount: {
      type: Number,
      required: true
    }
  },
  emits: ['update:amount'],
  data: () => {
    return {
      amountCopy: 0,
      debounceTask: ''
    }
  },
  watch: {
    amount: {
      immediate: true,
      handler (newValue) {
        clearTimeout(this.debounceTask)
        this.amountCopy = newValue
      }
    }
  },
  created () {
    this.mdiPlus = mdiPlus
    this.mdiMinus = mdiMinus
  },
  methods: {
    isNumber,
    updateAmount (toAdd) {
      const changed = this.amountCopy + toAdd
      if (changed < 0) {
        return
      }
      this.amountCopy = changed
      clearTimeout(this.debounceTask)
      this.debounceTask = setTimeout(() => {
        this.$emit('update:amount', changed)
      }, this.debounce)
    }
  }
}
</script>

<style scoped>

</style>
