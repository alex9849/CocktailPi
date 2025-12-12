<template>
  <q-card
    flat
    bordered
    class="bg-card-item-group text-card-item-group"
  >
    <q-card-section
      class="q-py-xs q-pr-xs bg-cyan-1 row items-center bg-card-header text-card-header"
    >
      <div class="col">
        <p class="text-weight-medium">
          {{ headline }}
        </p>
      </div>
      <div
        class="col-shrink"
        v-if="headlineButtonLabel"
      >
        <q-btn
          color="info"
          :label="headlineButtonLabel"
          :icon="headlineButtonIcon"
          @click="$router.push(headlineButtonDestination)"
          dense
          no-caps
          size="sm"
        />
      </div>
    </q-card-section>
    <q-separator
      :dark="color.cardItemGroupDark"
    />
    <slot name="cardBody">
      <q-card-section
        class="q-py-sm"
      >
        <slot name="default"/>
      </q-card-section>
    </slot>
  </q-card>
</template>

<script setup>
import { computed, defineProps } from 'vue'
import { useStore } from 'vuex'

defineProps({
  headline: {
    type: String,
    required: true
  },
  headlineButtonLabel: {
    type: String,
    default: null
  },
  headlineButtonDestination: {
    type: Object,
    default: null
  },
  headlineButtonIcon: {
    default: null
  }
})

const store = useStore()
const color = computed(() => store.getters['appearance/getNormalColors'])

</script>

<style scoped>

</style>
