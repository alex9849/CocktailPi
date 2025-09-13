<template>
  <q-card>
    <q-card-section
      v-if="!dense"
    >
      <div class="flex items-center dotted-overflow-1" style="min-height: 32px;">
        <q-icon :name="icon" :color="textColor" size="32px" />
        <span class="text-h6" :class="textColorClass">
          {{ value }}
        </span>
      </div>
    </q-card-section>
    <q-card-section
      v-else
      class="q-pa-sm"
    >
      <div class="flex items-center text-caption text-grey-7 dotted-overflow-1">
        <q-icon :name="icon" class="q-mr-xs" />
        {{ value }}
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'PropertyCard',
  props: {
    dense: {
      type: Boolean,
      default: false
    },
    headline: {
      type: String,
      required: true
    },
    value: {
      type: String,
      required: true
    },
    icon: {
      type: String,
      required: true
    },
    textColor: {
      type: String,
      required: true
    }
  },
  computed: {
    textColorClass () {
      return 'text-' + this.textColor
    }
  }
})
</script>

<style scoped>

</style>
