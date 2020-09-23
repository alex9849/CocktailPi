<template>
  <q-dialog
    :value="value"
    @input="event => {$emit('input', event);}"
    @hide="$emit('clickAbort')"
    :full-height="$q.platform.is.mobile"
  >
    <q-card>
      <q-card-section class="text-center" style="width: 500px">
        <h5>{{ question }}</h5>
        <q-splitter
          horizontal
          :value="10"
        />
        <slot name="error-area" />
        <slot />
        <div class="q-pa-md q-gutter-sm">
          <slot name="buttons">
            <q-btn
              color="grey"
              @click="() => {$emit('clickAbort');}"
              style="width: 100px"
            >
              Abort
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
    name: "CQuestion",
    props: {
      value: {
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
      }
    }
  }
</script>

<style scoped>

</style>
