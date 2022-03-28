<template>
  <q-card flat
          bordered
          :class="cardClass"
  >
    <q-card-section
      horizontal
    >
      <div
        class="flex items-center col-auto"
        :class="iconBackgroundClass"
      >
        <q-icon :name="icon"
                size="md"
                class="q-pa-xs"
                :class="iconClass"
        ></q-icon>
      </div>
      <q-separator vertical />
      <div class="col flex justify-center items-center">
        <div class="col">
          <div class="row flex justify-center items-center self-center">
            <p class="q-pa-sm">{{ headline }}</p>
          </div>
          <q-separator v-if="!hideContentSlot && $slots.content" />
          <slot name="content" v-if="!hideContentSlot && $slots.content"/>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: 'CQHeadlinedCard',
  props: {
    icon: null,
    iconClass: null,
    iconBackgroundClass: null,
    cardClass: null,
    headline: String,
    iconSize: {
      type: String,
      default: 'xl'
    },
    hideContentSlot: {
      type: Boolean,
      default: false
    }
  }
}
</script>

<style scoped>

</style>
