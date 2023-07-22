<template>
  <div class="row justify-center q-col-gutter-lg">
    <div class="col-12 text-center text-bold text-h5">
      <p>{{ headline }}</p>
    </div>
    <div class="col-12">
      <slot name="default" />
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'CSetupStep',
  props: {
    headline: {
      type: String,
      required: true
    }
  }
})
</script>
