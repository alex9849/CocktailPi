<template>
  <q-dialog
    :model-value="show"
    @update:model-value="$emit('update:show', $event)"
    @hide="$emit('clickAbort')"
  >
    <q-card class="full-width" :class="cardClass">
      <q-card-section class="text-center">
        <h5>{{ question }}</h5>
        <q-splitter
          horizontal
          :model-value="10"
        />
        <slot name="error-area" />
        <slot />
        <div class="q-pa-md q-gutter-sm">
          <slot name="buttons">
            <q-btn
              :color="abortColor"
              @click="() => {$emit('clickAbort');}"
              style="width: 100px"
            >
              {{ abortButtonText }}
            </q-btn>
            <q-btn
              :loading="loading"
              :color="okColor"
              style="width: 100px"
              @click="$emit('clickOk')"
            >
              {{ okButtonText }}
            </q-btn>
          </slot>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script>
export default {
  name: 'CQuestion',
  props: {
    show: {
      type: Boolean,
      required: true
    },
    question: {
      type: String,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    },
    okButtonText: {
      type: String,
      default: 'OK'
    },
    okColor: {
      type: String,
      default: 'green'
    },
    abortButtonText: {
      type: String,
      default: 'Abort'
    },
    abortColor: {
      type: String,
      default: 'grey'
    },
    cardClass: {
      type: String,
      default: 'white'
    }
  },
  emits: ['update:show', 'clickAbort', 'clickOk']
}
</script>
