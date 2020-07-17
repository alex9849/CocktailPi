<template>
  <q-dialog
    :value="value"
    @input="event => {$emit('input', event);}"
    @hide="$emit('clickAbort')"
  >
    <q-card style="width: 500px">
      <q-card-section class="text-center">
        <h5 style="margin: 5px">{{ question }}</h5>
        <slot />
        <div class="q-pa-md q-gutter-sm">
          <slot name="buttons">
            <q-btn
              color="grey"
              @click="() => {value = false; $emit('clickAbort');}"
              style="width: 150px"
            >
              Abort
            </q-btn>
            <q-btn
              :loading="loading"
              :color="okColor"
              style="width: 150px"
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
