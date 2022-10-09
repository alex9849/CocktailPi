<template>
  <q-card class="bg-grey-2 text-center full-height" flat bordered>
    <q-card-section class="q-gutter-sm">
      <p class="text-subtitle2">{{ ingredientName }}</p>
      <q-input v-model:model-value.number="amountCopy"
               readonly
               rounded
               outlined
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

export default {
  name: 'CIngredientAdditionalMlCard',
  props: {
    ingredientName: {
      type: String,
      required: true
    },
    amount: {
      type: Number,
      required: true
    },
    debounce: {
      type: Number,
      default: 0
    }
  },
  emits: ['update:amount'],
  data: () => {
    return {
      amountCopy: 0,
      debounceTask: ''
    }
  },
  created () {
    this.mdiPlus = mdiPlus
    this.mdiMinus = mdiMinus
  },
  methods: {
    updateAmount (toAdd) {
      const changed = this.amountCopy + toAdd
      if (changed < 0) {
        return
      }
      this.amountCopy = changed
      clearTimeout(this.debounceTask)
      this.debounceTask = setTimeout(() => {
        this.$emit('update:amount', this.amountCopy)
      })
    }
  },
  watch: {
    amount (newValue) {
      clearTimeout(this.debounceTask)
      this.amountCopy = newValue
    }
  }
}
</script>

<style scoped>

</style>
